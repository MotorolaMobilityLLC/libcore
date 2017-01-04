/*
 * Copyright (c) 2005, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#include "precompiled.hpp"
#include "classfile/javaClasses.hpp"
#include "classfile/systemDictionary.hpp"
#include "gc_implementation/shared/vmGCOperations.hpp"
#include "memory/resourceArea.hpp"
#include "prims/jvmtiExport.hpp"
#include "runtime/arguments.hpp"
#include "runtime/globals.hpp"
#include "runtime/java.hpp"
#include "runtime/javaCalls.hpp"
#include "runtime/os.hpp"
#include "services/attachListener.hpp"
#include "services/diagnosticCommand.hpp"
#include "services/heapDumper.hpp"

volatile bool AttachListener::_initialized;

// Implementation of "properties" command.
//
// Invokes sun.misc.VMSupport.serializePropertiesToByteArray to serialize
// the system properties into a byte array.

static Klass* load_and_initialize_klass(Symbol* sh, TRAPS) {
  Klass* k = SystemDictionary::resolve_or_fail(sh, true, CHECK_NULL);
  instanceKlassHandle ik (THREAD, k);
  if (ik->should_be_initialized()) {
    ik->initialize(CHECK_NULL);
  }
  return ik();
}

static jint get_properties(AttachOperation* op, outputStream* out, Symbol* serializePropertiesMethod) {
  Thread* THREAD = Thread::current();
  HandleMark hm;

  // load sun.misc.VMSupport
  Symbol* klass = vmSymbols::sun_misc_VMSupport();
  Klass* k = load_and_initialize_klass(klass, THREAD);
  if (HAS_PENDING_EXCEPTION) {
    java_lang_Throwable::print(PENDING_EXCEPTION, out);
    CLEAR_PENDING_EXCEPTION;
    return JNI_ERR;
  }
  instanceKlassHandle ik(THREAD, k);

  // invoke the serializePropertiesToByteArray method
  JavaValue result(T_OBJECT);
  JavaCallArguments args;


  Symbol* signature = vmSymbols::serializePropertiesToByteArray_signature();
  JavaCalls::call_static(&result,
                           ik,
                           serializePropertiesMethod,
                           signature,
                           &args,
                           THREAD);
  if (HAS_PENDING_EXCEPTION) {
    java_lang_Throwable::print(PENDING_EXCEPTION, out);
    CLEAR_PENDING_EXCEPTION;
    return JNI_ERR;
  }

  // The result should be a [B
  oop res = (oop)result.get_jobject();
  assert(res->is_typeArray(), "just checking");
  assert(TypeArrayKlass::cast(res->klass())->element_type() == T_BYTE, "just checking");

  // copy the bytes to the output stream
  typeArrayOop ba = typeArrayOop(res);
  jbyte* addr = typeArrayOop(res)->byte_at_addr(0);
  out->print_raw((const char*)addr, ba->length());

  return JNI_OK;
}

// Implementation of "properties" command.
// See also: PrintSystemPropertiesDCmd class
static jint get_system_properties(AttachOperation* op, outputStream* out) {
  return get_properties(op, out, vmSymbols::serializePropertiesToByteArray_name());
}

// Implementation of "agent_properties" command.
static jint get_agent_properties(AttachOperation* op, outputStream* out) {
  return get_properties(op, out, vmSymbols::serializeAgentPropertiesToByteArray_name());
}

// Implementation of "datadump" command.
//
// Raises a SIGBREAK signal so that VM dump threads, does deadlock detection,
// etc. In theory this command should only post a DataDumpRequest to any
// JVMTI environment that has enabled this event. However it's useful to
// trigger the SIGBREAK handler.

static jint data_dump(AttachOperation* op, outputStream* out) {
  if (!ReduceSignalUsage) {
    AttachListener::pd_data_dump();
  } else {
    if (JvmtiExport::should_post_data_dump()) {
      JvmtiExport::post_data_dump();
    }
  }
  return JNI_OK;
}

// Implementation of "threaddump" command - essentially a remote ctrl-break
// See also: ThreadDumpDCmd class
//
static jint thread_dump(AttachOperation* op, outputStream* out) {
  bool print_concurrent_locks = false;
  if (op->arg(0) != NULL && strcmp(op->arg(0), "-l") == 0) {
    print_concurrent_locks = true;
  }

  // thread stacks
  VM_PrintThreads op1(out, print_concurrent_locks);
  VMThread::execute(&op1);

  // JNI global handles
  VM_PrintJNI op2(out);
  VMThread::execute(&op2);

  // Deadlock detection
  VM_FindDeadlocks op3(out);
  VMThread::execute(&op3);

  return JNI_OK;
}

// A jcmd attach operation request was received, which will now
// dispatch to the diagnostic commands used for serviceability functions.
static jint jcmd(AttachOperation* op, outputStream* out) {
  Thread* THREAD = Thread::current();
  // All the supplied jcmd arguments are stored as a single
  // string (op->arg(0)). This is parsed by the Dcmd framework.
  DCmd::parse_and_execute(DCmd_Source_AttachAPI, out, op->arg(0), ' ', THREAD);
  if (HAS_PENDING_EXCEPTION) {
    java_lang_Throwable::print(PENDING_EXCEPTION, out);
    out->cr();
    CLEAR_PENDING_EXCEPTION;
    return JNI_ERR;
  }
  return JNI_OK;
}

// Implementation of "dumpheap" command.
// See also: HeapDumpDCmd class
//
// Input arguments :-
//   arg0: Name of the dump file
//   arg1: "-live" or "-all"
jint dump_heap(AttachOperation* op, outputStream* out) {
  const char* path = op->arg(0);
  if (path == NULL || path[0] == '\0') {
    out->print_cr("No dump file specified");
  } else {
    bool live_objects_only = true;   // default is true to retain the behavior before this change is made
    const char* arg1 = op->arg(1);
    if (arg1 != NULL && (strlen(arg1) > 0)) {
      if (strcmp(arg1, "-all") != 0 && strcmp(arg1, "-live") != 0) {
        out->print_cr("Invalid argument to dumpheap operation: %s", arg1);
        return JNI_ERR;
      }
      live_objects_only = strcmp(arg1, "-live") == 0;
    }

    // Request a full GC before heap dump if live_objects_only = true
    // This helps reduces the amount of unreachable objects in the dump
    // and makes it easier to browse.
    HeapDumper dumper(live_objects_only /* request GC */);
    int res = dumper.dump(op->arg(0));
    if (res == 0) {
      out->print_cr("Heap dump file created");
    } else {
      // heap dump failed
      ResourceMark rm;
      char* error = dumper.error_as_C_string();
      if (error == NULL) {
        out->print_cr("Dump failed - reason unknown");
      } else {
        out->print_cr("%s", error);
      }
    }
  }
  return JNI_OK;
}

// Implementation of "inspectheap" command
// See also: ClassHistogramDCmd class
//
// Input arguments :-
//   arg0: "-live" or "-all"
static jint heap_inspection(AttachOperation* op, outputStream* out) {
  bool live_objects_only = true;   // default is true to retain the behavior before this change is made
  const char* arg0 = op->arg(0);
  if (arg0 != NULL && (strlen(arg0) > 0)) {
    if (strcmp(arg0, "-all") != 0 && strcmp(arg0, "-live") != 0) {
      out->print_cr("Invalid argument to inspectheap operation: %s", arg0);
      return JNI_ERR;
    }
    live_objects_only = strcmp(arg0, "-live") == 0;
  }
  VM_GC_HeapInspection heapop(out, live_objects_only /* request full gc */);
  VMThread::execute(&heapop);
  return JNI_OK;
}

// set a boolean global flag using value from AttachOperation
static jint set_bool_flag(const char* name, AttachOperation* op, outputStream* out) {
  bool value = true;
  const char* arg1;
  if ((arg1 = op->arg(1)) != NULL) {
    int tmp;
    int n = sscanf(arg1, "%d", &tmp);
    if (n != 1) {
      out->print_cr("flag value must be a boolean (1 or 0)");
      return JNI_ERR;
    }
    value = (tmp != 0);
  }
  bool res = CommandLineFlags::boolAtPut((char*)name, &value, Flag::ATTACH_ON_DEMAND);
  if (! res) {
    out->print_cr("setting flag %s failed", name);
  }
  return res? JNI_OK : JNI_ERR;
}

// set a intx global flag using value from AttachOperation
static jint set_intx_flag(const char* name, AttachOperation* op, outputStream* out) {
  intx value;
  const char* arg1;
  if ((arg1 = op->arg(1)) != NULL) {
    int n = sscanf(arg1, INTX_FORMAT, &value);
    if (n != 1) {
      out->print_cr("flag value must be an integer");
      return JNI_ERR;
    }
  }
  bool res = CommandLineFlags::intxAtPut((char*)name, &value, Flag::ATTACH_ON_DEMAND);
  if (! res) {
    out->print_cr("setting flag %s failed", name);
  }

  return res? JNI_OK : JNI_ERR;
}

// set a uintx global flag using value from AttachOperation
static jint set_uintx_flag(const char* name, AttachOperation* op, outputStream* out) {
  uintx value;

  const char* arg1 = op->arg(1);
  if (arg1 == NULL) {
    out->print_cr("flag value must be specified");
    return JNI_ERR;
  }

  int n = sscanf(arg1, UINTX_FORMAT, &value);
  if (n != 1) {
    out->print_cr("flag value must be an unsigned integer");
    return JNI_ERR;
  }

  if (strncmp(name, "MaxHeapFreeRatio", 17) == 0) {
    FormatBuffer<80> err_msg("%s", "");
    if (!Arguments::verify_MaxHeapFreeRatio(err_msg, value)) {
      out->print_cr("%s", err_msg.buffer());
      return JNI_ERR;
    }
  } else if (strncmp(name, "MinHeapFreeRatio", 17) == 0) {
    FormatBuffer<80> err_msg("%s", "");
    if (!Arguments::verify_MinHeapFreeRatio(err_msg, value)) {
      out->print_cr("%s", err_msg.buffer());
      return JNI_ERR;
    }
  }
  bool res = CommandLineFlags::uintxAtPut((char*)name, &value, Flag::ATTACH_ON_DEMAND);
  if (! res) {
    out->print_cr("setting flag %s failed", name);
  }

  return res? JNI_OK : JNI_ERR;
}

// set a uint64_t global flag using value from AttachOperation
static jint set_uint64_t_flag(const char* name, AttachOperation* op, outputStream* out) {
  uint64_t value;
  const char* arg1;
  if ((arg1 = op->arg(1)) != NULL) {
    int n = sscanf(arg1, UINT64_FORMAT, &value);
    if (n != 1) {
      out->print_cr("flag value must be an unsigned 64-bit integer");
      return JNI_ERR;
    }
  }
  bool res = CommandLineFlags::uint64_tAtPut((char*)name, &value, Flag::ATTACH_ON_DEMAND);
  if (! res) {
    out->print_cr("setting flag %s failed", name);
  }

  return res? JNI_OK : JNI_ERR;
}

// set a string global flag using value from AttachOperation
static jint set_ccstr_flag(const char* name, AttachOperation* op, outputStream* out) {
  const char* value;
  if ((value = op->arg(1)) == NULL) {
    out->print_cr("flag value must be a string");
    return JNI_ERR;
  }
  bool res = CommandLineFlags::ccstrAtPut((char*)name, &value, Flag::ATTACH_ON_DEMAND);
  if (res) {
    FREE_C_HEAP_ARRAY(char, value, mtInternal);
  } else {
    out->print_cr("setting flag %s failed", name);
  }

  return res? JNI_OK : JNI_ERR;
}

// Implementation of "setflag" command
static jint set_flag(AttachOperation* op, outputStream* out) {

  const char* name = NULL;
  if ((name = op->arg(0)) == NULL) {
    out->print_cr("flag name is missing");
    return JNI_ERR;
  }

  Flag* f = Flag::find_flag((char*)name, strlen(name));
  if (f && f->is_external() && f->is_writeable()) {
    if (f->is_bool()) {
      return set_bool_flag(name, op, out);
    } else if (f->is_intx()) {
      return set_intx_flag(name, op, out);
    } else if (f->is_uintx()) {
      return set_uintx_flag(name, op, out);
    } else if (f->is_uint64_t()) {
      return set_uint64_t_flag(name, op, out);
    } else if (f->is_ccstr()) {
      return set_ccstr_flag(name, op, out);
    } else {
      ShouldNotReachHere();
      return JNI_ERR;
    }
  } else {
    return AttachListener::pd_set_flag(op, out);
  }
}

// Implementation of "printflag" command
// See also: PrintVMFlagsDCmd class
static jint print_flag(AttachOperation* op, outputStream* out) {
  const char* name = NULL;
  if ((name = op->arg(0)) == NULL) {
    out->print_cr("flag name is missing");
    return JNI_ERR;
  }
  Flag* f = Flag::find_flag((char*)name, strlen(name));
  if (f) {
    f->print_as_flag(out);
    out->cr();
  } else {
    out->print_cr("no such flag '%s'", name);
  }
  return JNI_OK;
}

// Table to map operation names to functions.

// names must be of length <= AttachOperation::name_length_max
static AttachOperationFunctionInfo funcs[] = {
  { "agentProperties",  get_agent_properties },
  { "datadump",         data_dump },
  { "dumpheap",         dump_heap },
  { "load",             JvmtiExport::load_agent_library },
  { "properties",       get_system_properties },
  { "threaddump",       thread_dump },
  { "inspectheap",      heap_inspection },
  { "setflag",          set_flag },
  { "printflag",        print_flag },
  { "jcmd",             jcmd },
  { NULL,               NULL }
};



// The Attach Listener threads services a queue. It dequeues an operation
// from the queue, examines the operation name (command), and dispatches
// to the corresponding function to perform the operation.

static void attach_listener_thread_entry(JavaThread* thread, TRAPS) {
  os::set_priority(thread, NearMaxPriority);

  thread->record_stack_base_and_size();

  if (AttachListener::pd_init() != 0) {
    return;
  }
  AttachListener::set_initialized();

  for (;;) {
    AttachOperation* op = AttachListener::dequeue();
    if (op == NULL) {
      return;   // dequeue failed or shutdown
    }

    ResourceMark rm;
    bufferedStream st;
    jint res = JNI_OK;

    // handle special detachall operation
    if (strcmp(op->name(), AttachOperation::detachall_operation_name()) == 0) {
      AttachListener::detachall();
    } else {
      // find the function to dispatch too
      AttachOperationFunctionInfo* info = NULL;
      for (int i=0; funcs[i].name != NULL; i++) {
        const char* name = funcs[i].name;
        assert(strlen(name) <= AttachOperation::name_length_max, "operation <= name_length_max");
        if (strcmp(op->name(), name) == 0) {
          info = &(funcs[i]);
          break;
        }
      }

      // check for platform dependent attach operation
      if (info == NULL) {
        info = AttachListener::pd_find_operation(op->name());
      }

      if (info != NULL) {
        // dispatch to the function that implements this operation
        res = (info->func)(op, &st);
      } else {
        st.print("Operation %s not recognized!", op->name());
        res = JNI_ERR;
      }
    }

    // operation complete - send result and output to client
    op->complete(res, &st);
  }
}

// Starts the Attach Listener thread
void AttachListener::init() {
  EXCEPTION_MARK;
  Klass* k = SystemDictionary::resolve_or_fail(vmSymbols::java_lang_Thread(), true, CHECK);
  instanceKlassHandle klass (THREAD, k);
  instanceHandle thread_oop = klass->allocate_instance_handle(CHECK);

  const char thread_name[] = "Attach Listener";
  Handle string = java_lang_String::create_from_str(thread_name, CHECK);

  // Initialize thread_oop to put it into the system threadGroup
  Handle thread_group (THREAD, Universe::system_thread_group());
  JavaValue result(T_VOID);
  JavaCalls::call_special(&result, thread_oop,
                       klass,
                       vmSymbols::object_initializer_name(),
                       vmSymbols::threadgroup_string_void_signature(),
                       thread_group,
                       string,
                       THREAD);

  if (HAS_PENDING_EXCEPTION) {
    tty->print_cr("Exception in VM (AttachListener::init) : ");
    java_lang_Throwable::print(PENDING_EXCEPTION, tty);
    tty->cr();

    CLEAR_PENDING_EXCEPTION;

    return;
  }

  KlassHandle group(THREAD, SystemDictionary::ThreadGroup_klass());
  JavaCalls::call_special(&result,
                        thread_group,
                        group,
                        vmSymbols::add_method_name(),
                        vmSymbols::thread_void_signature(),
                        thread_oop,             // ARG 1
                        THREAD);

  if (HAS_PENDING_EXCEPTION) {
    tty->print_cr("Exception in VM (AttachListener::init) : ");
    java_lang_Throwable::print(PENDING_EXCEPTION, tty);
    tty->cr();

    CLEAR_PENDING_EXCEPTION;

    return;
  }

  { MutexLocker mu(Threads_lock);
    JavaThread* listener_thread = new JavaThread(&attach_listener_thread_entry);

    // Check that thread and osthread were created
    if (listener_thread == NULL || listener_thread->osthread() == NULL) {
      vm_exit_during_initialization("java.lang.OutOfMemoryError",
                                    "unable to create new native thread");
    }

    java_lang_Thread::set_thread(thread_oop(), listener_thread);
    java_lang_Thread::set_daemon(thread_oop());

    listener_thread->set_threadObj(thread_oop());
    Threads::add(listener_thread);
    Thread::start(listener_thread);
  }
}

// Performs clean-up tasks on platforms where we can detect that the last
// client has detached
void AttachListener::detachall() {
  // call the platform dependent clean-up
  pd_detachall();
}
