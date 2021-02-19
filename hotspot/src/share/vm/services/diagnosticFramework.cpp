/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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
#include "memory/oopFactory.hpp"
#include "runtime/javaCalls.hpp"
#include "runtime/mutexLocker.hpp"
#include "services/diagnosticArgument.hpp"
#include "services/diagnosticFramework.hpp"
#include "services/management.hpp"

CmdLine::CmdLine(const char* line, size_t len, bool no_command_name) {
  assert(line != NULL, "Command line string should not be NULL");
  const char* line_end;
  const char* cmd_end;

  _cmd = line;
  line_end = &line[len];

  // Skip whitespace in the beginning of the line.
  while (_cmd < line_end && isspace((int) _cmd[0])) {
    _cmd++;
  }
  cmd_end = _cmd;

  if (no_command_name) {
    _cmd = NULL;
    _cmd_len = 0;
  } else {
    // Look for end of the command name
    while (cmd_end < line_end && !isspace((int) cmd_end[0])) {
      cmd_end++;
    }
    _cmd_len = cmd_end - _cmd;
  }
  _args = cmd_end;
  _args_len = line_end - _args;
}

bool DCmdArgIter::next(TRAPS) {
  if (_len == 0) return false;
  // skipping spaces
  while (_cursor < _len - 1 && isspace(_buffer[_cursor])) {
    _cursor++;
  }
  // handling end of command line
  if (_cursor >= _len - 1) {
    _cursor = _len - 1;
    _key_addr = &_buffer[_len - 1];
    _key_len = 0;
    _value_addr = &_buffer[_len - 1];
    _value_len = 0;
    return false;
  }
  // extracting first item, argument or option name
  _key_addr = &_buffer[_cursor];
  while (_cursor <= _len - 1 && _buffer[_cursor] != '=' && _buffer[_cursor] != _delim) {
    // argument can be surrounded by single or double quotes
    if (_buffer[_cursor] == '\"' || _buffer[_cursor] == '\'') {
      _key_addr++;
      char quote = _buffer[_cursor];
      while (_cursor < _len - 1) {
        _cursor++;
        if (_buffer[_cursor] == quote && _buffer[_cursor - 1] != '\\') {
          break;
        }
      }
      if (_buffer[_cursor] != quote) {
        THROW_MSG_(vmSymbols::java_lang_IllegalArgumentException(),
                "Format error in diagnostic command arguments", false);
      }
      break;
    }
    _cursor++;
  }
  _key_len = &_buffer[_cursor] - _key_addr;
  // check if the argument has the <key>=<value> format
  if (_cursor <= _len -1 && _buffer[_cursor] == '=') {
    _cursor++;
    _value_addr = &_buffer[_cursor];
    // extract the value
    while (_cursor <= _len - 1 && _buffer[_cursor] != _delim) {
      // value can be surrounded by simple or double quotes
      if (_buffer[_cursor] == '\"' || _buffer[_cursor] == '\'') {
        _value_addr++;
        char quote = _buffer[_cursor];
        while (_cursor < _len - 1) {
          _cursor++;
          if (_buffer[_cursor] == quote && _buffer[_cursor - 1] != '\\') {
            break;
          }
        }
        if (_buffer[_cursor] != quote) {
          THROW_MSG_(vmSymbols::java_lang_IllegalArgumentException(),
                  "Format error in diagnostic command arguments", false);
        }
        break;
      }
      _cursor++;
    }
    _value_len = &_buffer[_cursor] - _value_addr;
  } else {
    _value_addr = NULL;
    _value_len = 0;
  }
  return _key_len != 0;
}

bool DCmdInfo::by_name(void* cmd_name, DCmdInfo* info) {
  if (info == NULL) return false;
  return strcmp((const char*)cmd_name, info->name()) == 0;
}

void DCmdParser::add_dcmd_option(GenDCmdArgument* arg) {
  assert(arg != NULL, "Sanity");
  if (_options == NULL) {
    _options = arg;
  } else {
    GenDCmdArgument* o = _options;
    while (o->next() != NULL) {
      o = o->next();
    }
    o->set_next(arg);
  }
  arg->set_next(NULL);
  Thread* THREAD = Thread::current();
  arg->init_value(THREAD);
  if (HAS_PENDING_EXCEPTION) {
    fatal("Initialization must be successful");
  }
}

void DCmdParser::add_dcmd_argument(GenDCmdArgument* arg) {
  assert(arg != NULL, "Sanity");
  if (_arguments_list == NULL) {
    _arguments_list = arg;
  } else {
    GenDCmdArgument* a = _arguments_list;
    while (a->next() != NULL) {
      a = a->next();
    }
    a->set_next(arg);
  }
  arg->set_next(NULL);
  Thread* THREAD = Thread::current();
  arg->init_value(THREAD);
  if (HAS_PENDING_EXCEPTION) {
    fatal("Initialization must be successful");
  }
}

void DCmdParser::parse(CmdLine* line, char delim, TRAPS) {
  GenDCmdArgument* next_argument = _arguments_list;
  DCmdArgIter iter(line->args_addr(), line->args_len(), delim);
  bool cont = iter.next(CHECK);
  while (cont) {
    GenDCmdArgument* arg = lookup_dcmd_option(iter.key_addr(),
            iter.key_length());
    if (arg != NULL) {
      arg->read_value(iter.value_addr(), iter.value_length(), CHECK);
    } else {
      if (next_argument != NULL) {
        arg = next_argument;
        arg->read_value(iter.key_addr(), iter.key_length(), CHECK);
        next_argument = next_argument->next();
      } else {
        THROW_MSG(vmSymbols::java_lang_IllegalArgumentException(),
          "Unknown argument in diagnostic command");
      }
    }
    cont = iter.next(CHECK);
  }
  check(CHECK);
}

GenDCmdArgument* DCmdParser::lookup_dcmd_option(const char* name, size_t len) {
  GenDCmdArgument* arg = _options;
  while (arg != NULL) {
    if (strlen(arg->name()) == len &&
      strncmp(name, arg->name(), len) == 0) {
      return arg;
    }
    arg = arg->next();
  }
  return NULL;
}

void DCmdParser::check(TRAPS) {
  GenDCmdArgument* arg = _arguments_list;
  while (arg != NULL) {
    if (arg->is_mandatory() && !arg->has_value()) {
      THROW_MSG(vmSymbols::java_lang_IllegalArgumentException(),
              "Missing argument for diagnostic command");
    }
    arg = arg->next();
  }
  arg = _options;
  while (arg != NULL) {
    if (arg->is_mandatory() && !arg->has_value()) {
      THROW_MSG(vmSymbols::java_lang_IllegalArgumentException(),
              "Missing option for diagnostic command");
    }
    arg = arg->next();
  }
}

void DCmdParser::print_help(outputStream* out, const char* cmd_name) {
  out->print("\nSyntax : %s %s", cmd_name, _options == NULL ? "" : "[options]");
  GenDCmdArgument* arg = _arguments_list;
  while (arg != NULL) {
    if (arg->is_mandatory()) {
      out->print(" <%s>", arg->name());
    } else {
      out->print(" [<%s>]", arg->name());
    }
    arg = arg->next();
  }
  out->print_cr("");
  if (_arguments_list != NULL) {
    out->print_cr("\nArguments:");
    arg = _arguments_list;
    while (arg != NULL) {
      out->print("\t%s : %s %s (%s, ", arg->name(),
                 arg->is_mandatory() ? "" : "[optional]",
                 arg->description(), arg->type());
      if (arg->has_default()) {
        out->print(arg->default_string());
      } else {
        out->print("no default value");
      }
      out->print_cr(")");
      arg = arg->next();
    }
  }
  if (_options != NULL) {
    out->print_cr("\nOptions: (options must be specified using the <key> or <key>=<value> syntax)");
    arg = _options;
    while (arg != NULL) {
      out->print("\t%s : %s %s (%s, ", arg->name(),
                 arg->is_mandatory() ? "" : "[optional]",
                 arg->description(), arg->type());
      if (arg->has_default()) {
        out->print(arg->default_string());
      } else {
        out->print("no default value");
      }
      out->print_cr(")");
      arg = arg->next();
    }
  }
}

void DCmdParser::reset(TRAPS) {
  GenDCmdArgument* arg = _arguments_list;
  while (arg != NULL) {
    arg->reset(CHECK);
    arg = arg->next();
  }
  arg = _options;
  while (arg != NULL) {
    arg->reset(CHECK);
    arg = arg->next();
  }
}

void DCmdParser::cleanup() {
  GenDCmdArgument* arg = _arguments_list;
  while (arg != NULL) {
    arg->cleanup();
    arg = arg->next();
  }
  arg = _options;
  while (arg != NULL) {
    arg->cleanup();
    arg = arg->next();
  }
}

int DCmdParser::num_arguments() {
  GenDCmdArgument* arg = _arguments_list;
  int count = 0;
  while (arg != NULL) {
    count++;
    arg = arg->next();
  }
  arg = _options;
  while (arg != NULL) {
    count++;
    arg = arg->next();
  }
  return count;
}

GrowableArray<const char *>* DCmdParser::argument_name_array() {
  int count = num_arguments();
  GrowableArray<const char *>* array = new GrowableArray<const char *>(count);
  GenDCmdArgument* arg = _arguments_list;
  while (arg != NULL) {
    array->append(arg->name());
    arg = arg->next();
  }
  arg = _options;
  while (arg != NULL) {
    array->append(arg->name());
    arg = arg->next();
  }
  return array;
}

GrowableArray<DCmdArgumentInfo*>* DCmdParser::argument_info_array() {
  int count = num_arguments();
  GrowableArray<DCmdArgumentInfo*>* array = new GrowableArray<DCmdArgumentInfo *>(count);
  int idx = 0;
  GenDCmdArgument* arg = _arguments_list;
  while (arg != NULL) {
    array->append(new DCmdArgumentInfo(arg->name(), arg->description(),
                  arg->type(), arg->default_string(), arg->is_mandatory(),
                  false, idx));
    idx++;
    arg = arg->next();
  }
  arg = _options;
  while (arg != NULL) {
    array->append(new DCmdArgumentInfo(arg->name(), arg->description(),
                  arg->type(), arg->default_string(), arg->is_mandatory(),
                  true));
    arg = arg->next();
  }
  return array;
}

DCmdFactory* DCmdFactory::_DCmdFactoryList = NULL;

void DCmd::parse_and_execute(outputStream* out, const char* cmdline,
                             char delim, TRAPS) {

  if (cmdline == NULL) return; // Nothing to do!
  DCmdIter iter(cmdline, '\n');

  while (iter.has_next()) {
    CmdLine line = iter.next();
    if (line.is_stop()) {
      break;
    }
    if (line.is_executable()) {
      DCmd* command = DCmdFactory::create_local_DCmd(line, out, CHECK);
      assert(command != NULL, "command error must be handled before this line");
      DCmdMark mark(command);
      command->parse(&line, delim, CHECK);
      command->execute(CHECK);
    }
  }
}

Mutex* DCmdFactory::_dcmdFactory_lock = new Mutex(Mutex::leaf, "DCmdFactory", true);

DCmdFactory* DCmdFactory::factory(const char* name, size_t len) {
  MutexLockerEx ml(_dcmdFactory_lock, Mutex::_no_safepoint_check_flag);
  DCmdFactory* factory = _DCmdFactoryList;
  while (factory != NULL) {
    if (strlen(factory->name()) == len &&
        strncmp(name, factory->name(), len) == 0) {
      return factory;
    }
    factory = factory->_next;
  }
  return NULL;
}

int DCmdFactory::register_DCmdFactory(DCmdFactory* factory) {
  MutexLockerEx ml(_dcmdFactory_lock, Mutex::_no_safepoint_check_flag);
  factory->_next = _DCmdFactoryList;
  _DCmdFactoryList = factory;
  return 0; // Actually, there's no checks for duplicates
}

DCmd* DCmdFactory::create_global_DCmd(CmdLine &line, outputStream* out, TRAPS) {
  DCmdFactory* f = factory(line.cmd_addr(), line.cmd_len());
  if (f != NULL) {
    if (f->is_enabled()) {
      THROW_MSG_NULL(vmSymbols::java_lang_IllegalArgumentException(),
                     f->disabled_message());
    }
    return f->create_Cheap_instance(out);
  }
  THROW_MSG_NULL(vmSymbols::java_lang_IllegalArgumentException(),
             "Unknown diagnostic command");
}

DCmd* DCmdFactory::create_local_DCmd(CmdLine &line, outputStream* out, TRAPS) {
  DCmdFactory* f = factory(line.cmd_addr(), line.cmd_len());
  if (f != NULL) {
    if (!f->is_enabled()) {
      THROW_MSG_NULL(vmSymbols::java_lang_IllegalArgumentException(),
                     f->disabled_message());
    }
    return f->create_resource_instance(out);
  }
  THROW_MSG_NULL(vmSymbols::java_lang_IllegalArgumentException(),
             "Unknown diagnostic command");
}

GrowableArray<const char*>* DCmdFactory::DCmd_list() {
  MutexLockerEx ml(_dcmdFactory_lock, Mutex::_no_safepoint_check_flag);
  GrowableArray<const char*>* array = new GrowableArray<const char*>();
  DCmdFactory* factory = _DCmdFactoryList;
  while (factory != NULL) {
    if (!factory->is_hidden()) {
      array->append(factory->name());
    }
    factory = factory->next();
  }
  return array;
}

GrowableArray<DCmdInfo*>* DCmdFactory::DCmdInfo_list() {
  MutexLockerEx ml(_dcmdFactory_lock, Mutex::_no_safepoint_check_flag);
  GrowableArray<DCmdInfo*>* array = new GrowableArray<DCmdInfo*>();
  DCmdFactory* factory = _DCmdFactoryList;
  while (factory != NULL) {
    if (!factory->is_hidden()) {
      array->append(new DCmdInfo(factory->name(),
                    factory->description(), factory->impact(),
                    factory->num_arguments(), factory->is_enabled()));
    }
    factory = factory->next();
  }
  return array;
}
