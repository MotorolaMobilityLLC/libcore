/*
 * Copyright (c) 2008, 2016, Oracle and/or its affiliates. All rights reserved.
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
#include "c1/c1_Compilation.hpp"
#include "c1/c1_FrameMap.hpp"
#include "c1/c1_Instruction.hpp"
#include "c1/c1_LIRAssembler.hpp"
#include "c1/c1_LIRGenerator.hpp"
#include "c1/c1_Runtime1.hpp"
#include "c1/c1_ValueStack.hpp"
#include "ci/ciArray.hpp"
#include "ci/ciObjArrayKlass.hpp"
#include "ci/ciTypeArrayKlass.hpp"
#include "gc/shared/cardTableModRefBS.hpp"
#include "runtime/sharedRuntime.hpp"
#include "runtime/stubRoutines.hpp"
#include "vmreg_arm.inline.hpp"

#ifdef ASSERT
#define __ gen()->lir(__FILE__, __LINE__)->
#else
#define __ gen()->lir()->
#endif

void LIRItem::load_byte_item() {
  load_item();
}

void LIRItem::load_nonconstant() {
  LIR_Opr r = value()->operand();
  if (_gen->can_inline_as_constant(value())) {
    if (!r->is_constant()) {
      r = LIR_OprFact::value_type(value()->type());
    }
    _result = r;
  } else {
    load_item();
  }
}

//--------------------------------------------------------------
//               LIRGenerator
//--------------------------------------------------------------


LIR_Opr LIRGenerator::exceptionOopOpr() {
  return FrameMap::Exception_oop_opr;
}

LIR_Opr LIRGenerator::exceptionPcOpr()  {
  return FrameMap::Exception_pc_opr;
}

LIR_Opr LIRGenerator::syncLockOpr()     {
  return new_register(T_INT);
}

LIR_Opr LIRGenerator::syncTempOpr()     {
  return new_register(T_OBJECT);
}

LIR_Opr LIRGenerator::getThreadTemp()   {
  return LIR_OprFact::illegalOpr;
}

LIR_Opr LIRGenerator::atomicLockOpr() {
  return LIR_OprFact::illegalOpr;
}

LIR_Opr LIRGenerator::result_register_for(ValueType* type, bool callee) {
  LIR_Opr opr;
  switch (type->tag()) {
    case intTag:     opr = FrameMap::Int_result_opr;    break;
    case objectTag:  opr = FrameMap::Object_result_opr; break;
    case longTag:    opr = FrameMap::Long_result_opr;   break;
    case floatTag:   opr = FrameMap::Float_result_opr;  break;
    case doubleTag:  opr = FrameMap::Double_result_opr; break;
    case addressTag:
    default: ShouldNotReachHere(); return LIR_OprFact::illegalOpr;
  }
  assert(opr->type_field() == as_OprType(as_BasicType(type)), "type mismatch");
  return opr;
}


LIR_Opr LIRGenerator::rlock_byte(BasicType type) {
  return new_register(T_INT);
}


//--------- loading items into registers --------------------------------


bool LIRGenerator::can_store_as_constant(Value v, BasicType type) const {
#ifdef AARCH64
  if (v->type()->as_IntConstant() != NULL) {
    return v->type()->as_IntConstant()->value() == 0;
  } else if (v->type()->as_LongConstant() != NULL) {
    return v->type()->as_LongConstant()->value() == 0;
  } else if (v->type()->as_ObjectConstant() != NULL) {
    return v->type()->as_ObjectConstant()->value()->is_null_object();
  } else if (v->type()->as_FloatConstant() != NULL) {
    return jint_cast(v->type()->as_FloatConstant()->value()) == 0;
  } else if (v->type()->as_DoubleConstant() != NULL) {
    return jlong_cast(v->type()->as_DoubleConstant()->value()) == 0;
  }
#endif // AARCH64
  return false;
}


bool LIRGenerator::can_inline_as_constant(Value v) const {
  if (v->type()->as_IntConstant() != NULL) {
    return Assembler::is_arith_imm_in_range(v->type()->as_IntConstant()->value());
  } else if (v->type()->as_ObjectConstant() != NULL) {
    return v->type()->as_ObjectConstant()->value()->is_null_object();
#ifdef AARCH64
  } else if (v->type()->as_LongConstant() != NULL) {
    return Assembler::is_arith_imm_in_range(v->type()->as_LongConstant()->value());
#else
  } else if (v->type()->as_FloatConstant() != NULL) {
    return v->type()->as_FloatConstant()->value() == 0.0f;
  } else if (v->type()->as_DoubleConstant() != NULL) {
    return v->type()->as_DoubleConstant()->value() == 0.0;
#endif // AARCH64
  }
  return false;
}


bool LIRGenerator::can_inline_as_constant(LIR_Const* c) const {
  ShouldNotCallThis(); // Not used on ARM
  return false;
}


#ifdef AARCH64

static bool can_inline_as_constant_in_cmp(Value v) {
  jlong constant;
  if (v->type()->as_IntConstant() != NULL) {
    constant = v->type()->as_IntConstant()->value();
  } else if (v->type()->as_LongConstant() != NULL) {
    constant = v->type()->as_LongConstant()->value();
  } else if (v->type()->as_ObjectConstant() != NULL) {
    return v->type()->as_ObjectConstant()->value()->is_null_object();
  } else if (v->type()->as_FloatConstant() != NULL) {
    return v->type()->as_FloatConstant()->value() == 0.0f;
  } else if (v->type()->as_DoubleConstant() != NULL) {
    return v->type()->as_DoubleConstant()->value() == 0.0;
  } else {
    return false;
  }

  return Assembler::is_arith_imm_in_range(constant) || Assembler::is_arith_imm_in_range(-constant);
}


static bool can_inline_as_constant_in_logic(Value v) {
  if (v->type()->as_IntConstant() != NULL) {
    return Assembler::LogicalImmediate(v->type()->as_IntConstant()->value(), true).is_encoded();
  } else if (v->type()->as_LongConstant() != NULL) {
    return Assembler::LogicalImmediate(v->type()->as_LongConstant()->value(), false).is_encoded();
  }
  return false;
}


#endif // AARCH64


LIR_Opr LIRGenerator::safepoint_poll_register() {
  return LIR_OprFact::illegalOpr;
}


static LIR_Opr make_constant(BasicType type, jlong c) {
  switch (type) {
    case T_ADDRESS:
    case T_OBJECT:  return LIR_OprFact::intptrConst(c);
    case T_LONG:    return LIR_OprFact::longConst(c);
    case T_INT:     return LIR_OprFact::intConst(c);
    default: ShouldNotReachHere();
    return LIR_OprFact::intConst(-1);
  }
}

#ifdef AARCH64

void LIRGenerator::add_constant(LIR_Opr src, jlong c, LIR_Opr dest) {
  if (c == 0) {
    __ move(src, dest);
    return;
  }

  BasicType type = src->type();
  bool is_neg = (c < 0);
  c = ABS(c);

  if ((c >> 24) == 0) {
    for (int shift = 0; shift <= 12; shift += 12) {
      int part = ((int)c) & (right_n_bits(12) << shift);
      if (part != 0) {
        if (is_neg) {
          __ sub(src, make_constant(type, part), dest);
        } else {
          __ add(src, make_constant(type, part), dest);
        }
        src = dest;
      }
    }
  } else {
    __ move(make_constant(type, c), dest);
    if (is_neg) {
      __ sub(src, dest, dest);
    } else {
      __ add(src, dest, dest);
    }
  }
}

#endif // AARCH64


void LIRGenerator::add_large_constant(LIR_Opr src, int c, LIR_Opr dest) {
  assert(c != 0, "must be");
#ifdef AARCH64
  add_constant(src, c, dest);
#else
  // Find first non-zero bit
  int shift = 0;
  while ((c & (3 << shift)) == 0) {
    shift += 2;
  }
  // Add the least significant part of the constant
  int mask = 0xff << shift;
  __ add(src, LIR_OprFact::intConst(c & mask), dest);
  // Add up to 3 other parts of the constant;
  // each of them can be represented as rotated_imm
  if (c & (mask << 8)) {
    __ add(dest, LIR_OprFact::intConst(c & (mask << 8)), dest);
  }
  if (c & (mask << 16)) {
    __ add(dest, LIR_OprFact::intConst(c & (mask << 16)), dest);
  }
  if (c & (mask << 24)) {
    __ add(dest, LIR_OprFact::intConst(c & (mask << 24)), dest);
  }
#endif // AARCH64
}

static LIR_Address* make_address(LIR_Opr base, LIR_Opr index, LIR_Address::Scale scale, BasicType type) {
  return new LIR_Address(base, index, scale, 0, type);
}

LIR_Address* LIRGenerator::generate_address(LIR_Opr base, LIR_Opr index,
                                            int shift, int disp, BasicType type) {
  assert(base->is_register(), "must be");

  if (index->is_constant()) {
    disp += index->as_constant_ptr()->as_jint() << shift;
    index = LIR_OprFact::illegalOpr;
  }

#ifndef AARCH64
  if (base->type() == T_LONG) {
    LIR_Opr tmp = new_register(T_INT);
    __ convert(Bytecodes::_l2i, base, tmp);
    base = tmp;
  }
  if (index != LIR_OprFact::illegalOpr && index->type() == T_LONG) {
    LIR_Opr tmp = new_register(T_INT);
    __ convert(Bytecodes::_l2i, index, tmp);
    index = tmp;
  }
  // At this point base and index should be all ints and not constants
  assert(base->is_single_cpu() && !base->is_constant(), "base should be an non-constant int");
  assert(index->is_illegal() || (index->type() == T_INT && !index->is_constant()), "index should be an non-constant int");
#endif

  int max_disp;
  bool disp_is_in_range;
  bool embedded_shift;

#ifdef AARCH64
  int align = exact_log2(type2aelembytes(type, true));
  assert((disp & right_n_bits(align)) == 0, "displacement is not aligned");
  assert(shift == 0 || shift == align, "shift should be zero or equal to embedded align");
  max_disp = (1 << 12) << align;

  if (disp >= 0) {
    disp_is_in_range = Assembler::is_unsigned_imm_in_range(disp, 12, align);
  } else {
    disp_is_in_range = Assembler::is_imm_in_range(disp, 9, 0);
  }

  embedded_shift = true;
#else
  switch (type) {
    case T_BYTE:
    case T_SHORT:
    case T_CHAR:
      max_disp = 256;          // ldrh, ldrsb encoding has 8-bit offset
      embedded_shift = false;
      break;
    case T_FLOAT:
    case T_DOUBLE:
      max_disp = 1024;         // flds, fldd have 8-bit offset multiplied by 4
      embedded_shift = false;
      break;
    case T_LONG:
      max_disp = 4096;
      embedded_shift = false;
      break;
    default:
      max_disp = 4096;         // ldr, ldrb allow 12-bit offset
      embedded_shift = true;
  }

  disp_is_in_range = (-max_disp < disp && disp < max_disp);
#endif // !AARCH64

  if (index->is_register()) {
    LIR_Opr tmp = new_pointer_register();
    if (!disp_is_in_range) {
      add_large_constant(base, disp, tmp);
      base = tmp;
      disp = 0;
    }
    LIR_Address* addr = make_address(base, index, (LIR_Address::Scale)shift, type);
    if (disp == 0 && embedded_shift) {
      // can use ldr/str instruction with register index
      return addr;
    } else {
      LIR_Opr tmp = new_pointer_register();
      __ add(base, LIR_OprFact::address(addr), tmp); // add with shifted/extended register
      return new LIR_Address(tmp, disp, type);
    }
  }

  // If the displacement is too large to be inlined into LDR instruction,
  // generate large constant with additional sequence of ADD instructions
  int excess_disp = disp & ~(max_disp - 1);
  if (excess_disp != 0) {
    LIR_Opr tmp = new_pointer_register();
    add_large_constant(base, excess_disp, tmp);
    base = tmp;
  }
  return new LIR_Address(base, disp & (max_disp - 1), type);
}


LIR_Address* LIRGenerator::emit_array_address(LIR_Opr array_opr, LIR_Opr index_opr,
                                              BasicType type, bool needs_card_mark) {
  int base_offset = arrayOopDesc::base_offset_in_bytes(type);
  int elem_size = type2aelembytes(type);

  if (index_opr->is_constant()) {
    int offset = base_offset + index_opr->as_constant_ptr()->as_jint() * elem_size;
    if (needs_card_mark) {
      LIR_Opr base_opr = new_pointer_register();
      add_large_constant(array_opr, offset, base_opr);
      return new LIR_Address(base_opr, (intx)0, type);
    } else {
      return generate_address(array_opr, offset, type);
    }
  } else {
    assert(index_opr->is_register(), "must be");
    int scale = exact_log2(elem_size);
    if (needs_card_mark) {
      LIR_Opr base_opr = new_pointer_register();
      LIR_Address* addr = make_address(base_opr, index_opr, (LIR_Address::Scale)scale, type);
      __ add(array_opr, LIR_OprFact::intptrConst(base_offset), base_opr);
      __ add(base_opr, LIR_OprFact::address(addr), base_opr); // add with shifted/extended register
      return new LIR_Address(base_opr, type);
    } else {
      return generate_address(array_opr, index_opr, scale, base_offset, type);
    }
  }
}


LIR_Opr LIRGenerator::load_immediate(int x, BasicType type) {
  assert(type == T_LONG || type == T_INT, "should be");
  LIR_Opr r = make_constant(type, x);
#ifdef AARCH64
  bool imm_in_range = Assembler::LogicalImmediate(x, type == T_INT).is_encoded();
#else
  bool imm_in_range = AsmOperand::is_rotated_imm(x);
#endif // AARCH64
  if (!imm_in_range) {
    LIR_Opr tmp = new_register(type);
    __ move(r, tmp);
    return tmp;
  }
  return r;
}


void LIRGenerator::increment_counter(address counter, BasicType type, int step) {
  LIR_Opr pointer = new_pointer_register();
  __ move(LIR_OprFact::intptrConst(counter), pointer);
  LIR_Address* addr = new LIR_Address(pointer, type);
  increment_counter(addr, step);
}


void LIRGenerator::increment_counter(LIR_Address* addr, int step) {
  LIR_Opr temp = new_register(addr->type());
  __ move(addr, temp);
  __ add(temp, make_constant(addr->type(), step), temp);
  __ move(temp, addr);
}


void LIRGenerator::cmp_mem_int(LIR_Condition condition, LIR_Opr base, int disp, int c, CodeEmitInfo* info) {
  __ load(new LIR_Address(base, disp, T_INT), FrameMap::LR_opr, info);
  __ cmp(condition, FrameMap::LR_opr, c);
}


void LIRGenerator::cmp_reg_mem(LIR_Condition condition, LIR_Opr reg, LIR_Opr base, int disp, BasicType type, CodeEmitInfo* info) {
  __ load(new LIR_Address(base, disp, type), FrameMap::LR_opr, info);
  __ cmp(condition, reg, FrameMap::LR_opr);
}


bool LIRGenerator::strength_reduce_multiply(LIR_Opr left, int c, LIR_Opr result, LIR_Opr tmp) {
  assert(left != result, "should be different registers");
  if (is_power_of_2(c + 1)) {
#ifdef AARCH64
    __ shift_left(left, log2_intptr(c + 1), result);
    __ sub(result, left, result);
#else
    LIR_Address::Scale scale = (LIR_Address::Scale) log2_intptr(c + 1);
    LIR_Address* addr = new LIR_Address(left, left, scale, 0, T_INT);
    __ sub(LIR_OprFact::address(addr), left, result); // rsb with shifted register
#endif // AARCH64
    return true;
  } else if (is_power_of_2(c - 1)) {
    LIR_Address::Scale scale = (LIR_Address::Scale) log2_intptr(c - 1);
    LIR_Address* addr = new LIR_Address(left, left, scale, 0, T_INT);
    __ add(left, LIR_OprFact::address(addr), result); // add with shifted register
    return true;
  }
  return false;
}


void LIRGenerator::store_stack_parameter(LIR_Opr item, ByteSize offset_from_sp) {
  assert(item->type() == T_INT, "other types are not expected");
  __ store(item, new LIR_Address(FrameMap::SP_opr, in_bytes(offset_from_sp), item->type()));
}

void LIRGenerator::set_card(LIR_Opr value, LIR_Address* card_addr) {
  assert(CardTableModRefBS::dirty_card_val() == 0,
    "Cannot use ZR register (aarch64) or the register containing the card table base address directly (aarch32) otherwise");
#ifdef AARCH64
  // AARCH64 has a register that is constant zero. We can use that one to set the
  // value in the card table to dirty.
  __ move(FrameMap::ZR_opr, card_addr);
#else // AARCH64
  CardTableModRefBS* ct = (CardTableModRefBS*)_bs;
  if(((intx)ct->byte_map_base & 0xff) == 0) {
    // If the card table base address is aligned to 256 bytes, we can use the register
    // that contains the card_table_base_address.
    __ move(value, card_addr);
  } else {
    // Otherwise we need to create a register containing that value.
    LIR_Opr tmp_zero = new_register(T_INT);
    __ move(LIR_OprFact::intConst(CardTableModRefBS::dirty_card_val()), tmp_zero);
    __ move(tmp_zero, card_addr);
  }
#endif // AARCH64
}

void LIRGenerator::CardTableModRef_post_barrier_helper(LIR_OprDesc* addr, LIR_Const* card_table_base) {
  assert(addr->is_register(), "must be a register at this point");

  LIR_Opr tmp = FrameMap::LR_ptr_opr;

  // TODO-AARCH64: check performance
  bool load_card_table_base_const = AARCH64_ONLY(false) NOT_AARCH64(VM_Version::supports_movw());
  if (load_card_table_base_const) {
    __ move((LIR_Opr)card_table_base, tmp);
  } else {
    __ move(new LIR_Address(FrameMap::Rthread_opr, in_bytes(JavaThread::card_table_base_offset()), T_ADDRESS), tmp);
  }

#ifdef AARCH64
  LIR_Address* shifted_reg_operand = new LIR_Address(tmp, addr, (LIR_Address::Scale) -CardTableModRefBS::card_shift, 0, T_BYTE);
  LIR_Opr tmp2 = tmp;
  __ add(tmp, LIR_OprFact::address(shifted_reg_operand), tmp2); // tmp2 = tmp + (addr >> CardTableModRefBS::card_shift)
  LIR_Address* card_addr = new LIR_Address(tmp2, T_BYTE);
#else
  // Use unsigned type T_BOOLEAN here rather than (signed) T_BYTE since signed load
  // byte instruction does not support the addressing mode we need.
  LIR_Address* card_addr = new LIR_Address(tmp, addr, (LIR_Address::Scale) -CardTableModRefBS::card_shift, 0, T_BOOLEAN);
#endif
  if (UseCondCardMark) {
    if (UseConcMarkSweepGC) {
      __ membar_storeload();
    }
    LIR_Opr cur_value = new_register(T_INT);
    __ move(card_addr, cur_value);

    LabelObj* L_already_dirty = new LabelObj();
    __ cmp(lir_cond_equal, cur_value, LIR_OprFact::intConst(CardTableModRefBS::dirty_card_val()));
    __ branch(lir_cond_equal, T_BYTE, L_already_dirty->label());
    set_card(tmp, card_addr);
    __ branch_destination(L_already_dirty->label());
  } else {
    if (UseConcMarkSweepGC && CMSPrecleaningEnabled) {
      __ membar_storestore();
    }
    set_card(tmp, card_addr);
  }
}

//----------------------------------------------------------------------
//             visitor functions
//----------------------------------------------------------------------


void LIRGenerator::do_StoreIndexed(StoreIndexed* x) {
  assert(x->is_pinned(),"");
  bool needs_range_check = x->compute_needs_range_check();
  bool use_length = x->length() != NULL;
  bool obj_store = x->elt_type() == T_ARRAY || x->elt_type() == T_OBJECT;
  bool needs_store_check = obj_store && (x->value()->as_Constant() == NULL ||
                                         !get_jobject_constant(x->value())->is_null_object() ||
                                         x->should_profile());

  LIRItem array(x->array(), this);
  LIRItem index(x->index(), this);
  LIRItem value(x->value(), this);
  LIRItem length(this);

  array.load_item();
  index.load_nonconstant();

  if (use_length && needs_range_check) {
    length.set_instruction(x->length());
    length.load_item();
  }
  if (needs_store_check || x->check_boolean()) {
    value.load_item();
  } else {
    value.load_for_store(x->elt_type());
  }

  set_no_result(x);

  // the CodeEmitInfo must be duplicated for each different
  // LIR-instruction because spilling can occur anywhere between two
  // instructions and so the debug information must be different
  CodeEmitInfo* range_check_info = state_for(x);
  CodeEmitInfo* null_check_info = NULL;
  if (x->needs_null_check()) {
    null_check_info = new CodeEmitInfo(range_check_info);
  }

  // emit array address setup early so it schedules better
  LIR_Address* array_addr = emit_array_address(array.result(), index.result(), x->elt_type(), obj_store);

  if (GenerateRangeChecks && needs_range_check) {
    if (use_length) {
      __ cmp(lir_cond_belowEqual, length.result(), index.result());
      __ branch(lir_cond_belowEqual, T_INT, new RangeCheckStub(range_check_info, index.result()));
    } else {
      array_range_check(array.result(), index.result(), null_check_info, range_check_info);
      // range_check also does the null check
      null_check_info = NULL;
    }
  }

  if (GenerateArrayStoreCheck && needs_store_check) {
    LIR_Opr tmp1 = FrameMap::R0_oop_opr;
    LIR_Opr tmp2 = FrameMap::R1_oop_opr;
    CodeEmitInfo* store_check_info = new CodeEmitInfo(range_check_info);
    __ store_check(value.result(), array.result(), tmp1, tmp2,
                   LIR_OprFact::illegalOpr, store_check_info,
                   x->profiled_method(), x->profiled_bci());
  }

#if INCLUDE_ALL_GCS
  if (obj_store) {
    // Needs GC write barriers.
    pre_barrier(LIR_OprFact::address(array_addr), LIR_OprFact::illegalOpr /* pre_val */,
                true /* do_load */, false /* patch */, NULL);
  }
#endif // INCLUDE_ALL_GCS

  LIR_Opr result = maybe_mask_boolean(x, array.result(), value.result(), null_check_info);
  __ move(result, array_addr, null_check_info);
  if (obj_store) {
    post_barrier(LIR_OprFact::address(array_addr), value.result());
  }
}


void LIRGenerator::do_MonitorEnter(MonitorEnter* x) {
  assert(x->is_pinned(),"");
  LIRItem obj(x->obj(), this);
  obj.load_item();
  set_no_result(x);

  LIR_Opr lock = new_pointer_register();
  LIR_Opr hdr  = new_pointer_register();

  // Need a scratch register for biased locking on arm
  LIR_Opr scratch = LIR_OprFact::illegalOpr;
  if(UseBiasedLocking) {
    scratch = new_pointer_register();
  } else {
    scratch = atomicLockOpr();
  }

  CodeEmitInfo* info_for_exception = NULL;
  if (x->needs_null_check()) {
    info_for_exception = state_for(x);
  }

  CodeEmitInfo* info = state_for(x, x->state(), true);
  monitor_enter(obj.result(), lock, hdr, scratch,
                x->monitor_no(), info_for_exception, info);
}


void LIRGenerator::do_MonitorExit(MonitorExit* x) {
  assert(x->is_pinned(),"");
  LIRItem obj(x->obj(), this);
  obj.dont_load_item();
  set_no_result(x);

  LIR_Opr obj_temp = new_pointer_register();
  LIR_Opr lock     = new_pointer_register();
  LIR_Opr hdr      = new_pointer_register();

  monitor_exit(obj_temp, lock, hdr, atomicLockOpr(), x->monitor_no());
}


// _ineg, _lneg, _fneg, _dneg
void LIRGenerator::do_NegateOp(NegateOp* x) {
#ifdef __SOFTFP__
  address runtime_func = NULL;
  ValueTag tag = x->type()->tag();
  if (tag == floatTag) {
    runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::fneg);
  } else if (tag == doubleTag) {
    runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dneg);
  }
  if (runtime_func != NULL) {
    set_result(x, call_runtime(x->x(), runtime_func, x->type(), NULL));
    return;
  }
#endif // __SOFTFP__
  LIRItem value(x->x(), this);
  value.load_item();
  LIR_Opr reg = rlock_result(x);
  __ negate(value.result(), reg);
}


// for  _fadd, _fmul, _fsub, _fdiv, _frem
//      _dadd, _dmul, _dsub, _ddiv, _drem
void LIRGenerator::do_ArithmeticOp_FPU(ArithmeticOp* x) {
  address runtime_func;
  switch (x->op()) {
    case Bytecodes::_frem:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::frem);
      break;
    case Bytecodes::_drem:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::drem);
      break;
#ifdef __SOFTFP__
    // Call function compiled with -msoft-float.

      // __aeabi_XXXX_glibc: Imported code from glibc soft-fp bundle for calculation accuracy improvement. See CR 6757269.

    case Bytecodes::_fadd:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_fadd_glibc);
      break;
    case Bytecodes::_fmul:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_fmul);
      break;
    case Bytecodes::_fsub:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_fsub_glibc);
      break;
    case Bytecodes::_fdiv:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_fdiv);
      break;
    case Bytecodes::_dadd:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_dadd_glibc);
      break;
    case Bytecodes::_dmul:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_dmul);
      break;
    case Bytecodes::_dsub:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_dsub_glibc);
      break;
    case Bytecodes::_ddiv:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_ddiv);
      break;
    default:
      ShouldNotReachHere();
#else // __SOFTFP__
    default: {
      LIRItem left(x->x(), this);
      LIRItem right(x->y(), this);
      left.load_item();
      right.load_item();
      rlock_result(x);
      arithmetic_op_fpu(x->op(), x->operand(), left.result(), right.result(), x->is_strictfp());
      return;
    }
#endif // __SOFTFP__
  }

  LIR_Opr result = call_runtime(x->x(), x->y(), runtime_func, x->type(), NULL);
  set_result(x, result);
}


void LIRGenerator::make_div_by_zero_check(LIR_Opr right_arg, BasicType type, CodeEmitInfo* info) {
  assert(right_arg->is_register(), "must be");
  __ cmp(lir_cond_equal, right_arg, make_constant(type, 0));
  __ branch(lir_cond_equal, type, new DivByZeroStub(info));
}


// for  _ladd, _lmul, _lsub, _ldiv, _lrem
void LIRGenerator::do_ArithmeticOp_Long(ArithmeticOp* x) {
  CodeEmitInfo* info = NULL;
  if (x->op() == Bytecodes::_ldiv || x->op() == Bytecodes::_lrem) {
    info = state_for(x);
  }

#ifdef AARCH64
  LIRItem left(x->x(), this);
  LIRItem right(x->y(), this);
  LIRItem* left_arg = &left;
  LIRItem* right_arg = &right;

  // Test if instr is commutative and if we should swap
  if (x->is_commutative() && left.is_constant()) {
    left_arg = &right;
    right_arg = &left;
  }

  left_arg->load_item();
  switch (x->op()) {
    case Bytecodes::_ldiv:
      right_arg->load_item();
      make_div_by_zero_check(right_arg->result(), T_LONG, info);
      __ idiv(left_arg->result(), right_arg->result(), rlock_result(x), LIR_OprFact::illegalOpr, NULL);
      break;

    case Bytecodes::_lrem: {
      right_arg->load_item();
      make_div_by_zero_check(right_arg->result(), T_LONG, info);
      // a % b is implemented with 2 instructions:
      // tmp = a/b       (sdiv)
      // res = a - b*tmp (msub)
      LIR_Opr tmp = FrameMap::as_long_opr(Rtemp);
      __ irem(left_arg->result(), right_arg->result(), rlock_result(x), tmp, NULL);
      break;
    }

    case Bytecodes::_lmul:
      if (right_arg->is_constant() && is_power_of_2_long(right_arg->get_jlong_constant())) {
        right_arg->dont_load_item();
        __ shift_left(left_arg->result(), exact_log2_long(right_arg->get_jlong_constant()), rlock_result(x));
      } else {
        right_arg->load_item();
        __ mul(left_arg->result(), right_arg->result(), rlock_result(x));
      }
      break;

    case Bytecodes::_ladd:
    case Bytecodes::_lsub:
      if (right_arg->is_constant()) {
        jlong c = right_arg->get_jlong_constant();
        add_constant(left_arg->result(), (x->op() == Bytecodes::_ladd) ? c : -c, rlock_result(x));
      } else {
        right_arg->load_item();
        arithmetic_op_long(x->op(), rlock_result(x), left_arg->result(), right_arg->result(), NULL);
      }
      break;

    default:
      ShouldNotReachHere();
  }
#else
  switch (x->op()) {
    case Bytecodes::_ldiv:
    case Bytecodes::_lrem: {
      LIRItem right(x->y(), this);
      right.load_item();
      make_div_by_zero_check(right.result(), T_LONG, info);
    }
    // Fall through
    case Bytecodes::_lmul: {
      address entry;
      switch (x->op()) {
      case Bytecodes::_lrem:
        entry = CAST_FROM_FN_PTR(address, SharedRuntime::lrem);
        break;
      case Bytecodes::_ldiv:
        entry = CAST_FROM_FN_PTR(address, SharedRuntime::ldiv);
        break;
      case Bytecodes::_lmul:
        entry = CAST_FROM_FN_PTR(address, SharedRuntime::lmul);
        break;
      default:
        ShouldNotReachHere();
      }
      LIR_Opr result = call_runtime(x->y(), x->x(), entry, x->type(), NULL);
      set_result(x, result);
      break;
    }
    case Bytecodes::_ladd:
    case Bytecodes::_lsub: {
      LIRItem left(x->x(), this);
      LIRItem right(x->y(), this);
      left.load_item();
      right.load_item();
      rlock_result(x);
      arithmetic_op_long(x->op(), x->operand(), left.result(), right.result(), NULL);
      break;
    }
    default:
      ShouldNotReachHere();
  }
#endif // AARCH64
}


// for: _iadd, _imul, _isub, _idiv, _irem
void LIRGenerator::do_ArithmeticOp_Int(ArithmeticOp* x) {
  bool is_div_rem = x->op() == Bytecodes::_idiv || x->op() == Bytecodes::_irem;
  LIRItem left(x->x(), this);
  LIRItem right(x->y(), this);
  LIRItem* left_arg = &left;
  LIRItem* right_arg = &right;

  // Test if instr is commutative and if we should swap
  if (x->is_commutative() && left.is_constant()) {
    left_arg = &right;
    right_arg = &left;
  }

  if (is_div_rem) {
    CodeEmitInfo* info = state_for(x);
    if (x->op() == Bytecodes::_idiv && right_arg->is_constant() && is_power_of_2(right_arg->get_jint_constant())) {
      left_arg->load_item();
      right_arg->dont_load_item();
      LIR_Opr tmp = LIR_OprFact::illegalOpr;
      LIR_Opr result = rlock_result(x);
      __ idiv(left_arg->result(), right_arg->result(), result, tmp, info);
    } else {
#ifdef AARCH64
      left_arg->load_item();
      right_arg->load_item();
      make_div_by_zero_check(right_arg->result(), T_INT, info);
      if (x->op() == Bytecodes::_idiv) {
        __ idiv(left_arg->result(), right_arg->result(), rlock_result(x), LIR_OprFact::illegalOpr, NULL);
      } else {
        // a % b is implemented with 2 instructions:
        // tmp = a/b       (sdiv)
        // res = a - b*tmp (msub)
        LIR_Opr tmp = FrameMap::as_opr(Rtemp);
        __ irem(left_arg->result(), right_arg->result(), rlock_result(x), tmp, NULL);
      }
#else
      left_arg->load_item_force(FrameMap::R0_opr);
      right_arg->load_item_force(FrameMap::R2_opr);
      LIR_Opr tmp = FrameMap::R1_opr;
      LIR_Opr result = rlock_result(x);
      LIR_Opr out_reg;
      if (x->op() == Bytecodes::_irem) {
        out_reg = FrameMap::R0_opr;
        __ irem(left_arg->result(), right_arg->result(), out_reg, tmp, info);
      } else if (x->op() == Bytecodes::_idiv) {
        out_reg = FrameMap::R1_opr;
        __ idiv(left_arg->result(), right_arg->result(), out_reg, tmp, info);
      }
      __ move(out_reg, result);
#endif // AARCH64
    }

#ifdef AARCH64
  } else if (((x->op() == Bytecodes::_iadd) || (x->op() == Bytecodes::_isub)) && right_arg->is_constant()) {
    left_arg->load_item();
    jint c = right_arg->get_jint_constant();
    right_arg->dont_load_item();
    add_constant(left_arg->result(), (x->op() == Bytecodes::_iadd) ? c : -c, rlock_result(x));
#endif // AARCH64

  } else {
    left_arg->load_item();
    if (x->op() == Bytecodes::_imul && right_arg->is_constant()) {
      int c = right_arg->get_jint_constant();
      if (c > 0 && (is_power_of_2(c) || is_power_of_2(c - 1) || is_power_of_2(c + 1))) {
        right_arg->dont_load_item();
      } else {
        right_arg->load_item();
      }
    } else {
      AARCH64_ONLY(assert(!right_arg->is_constant(), "constant right_arg is already handled by this moment");)
      right_arg->load_nonconstant();
    }
    rlock_result(x);
    assert(right_arg->is_constant() || right_arg->is_register(), "wrong state of right");
    arithmetic_op_int(x->op(), x->operand(), left_arg->result(), right_arg->result(), NULL);
  }
}


void LIRGenerator::do_ArithmeticOp(ArithmeticOp* x) {
  ValueTag tag = x->type()->tag();
  assert(x->x()->type()->tag() == tag && x->y()->type()->tag() == tag, "wrong parameters");
  switch (tag) {
    case floatTag:
    case doubleTag:  do_ArithmeticOp_FPU(x);  return;
    case longTag:    do_ArithmeticOp_Long(x); return;
    case intTag:     do_ArithmeticOp_Int(x);  return;
  }
  ShouldNotReachHere();
}


// _ishl, _lshl, _ishr, _lshr, _iushr, _lushr
void LIRGenerator::do_ShiftOp(ShiftOp* x) {
  LIRItem value(x->x(), this);
  LIRItem count(x->y(), this);

#ifndef AARCH64
  if (value.type()->is_long()) {
    count.set_destroys_register();
  }
#endif // !AARCH64

  if (count.is_constant()) {
    assert(count.type()->as_IntConstant() != NULL, "should be");
    count.dont_load_item();
  } else {
    count.load_item();
  }
  value.load_item();

  LIR_Opr res = rlock_result(x);
  shift_op(x->op(), res, value.result(), count.result(), LIR_OprFact::illegalOpr);
}


// _iand, _land, _ior, _lor, _ixor, _lxor
void LIRGenerator::do_LogicOp(LogicOp* x) {
  LIRItem left(x->x(), this);
  LIRItem right(x->y(), this);

  left.load_item();

#ifdef AARCH64
  if (right.is_constant() && can_inline_as_constant_in_logic(right.value())) {
    right.dont_load_item();
  } else {
    right.load_item();
  }
#else
  right.load_nonconstant();
#endif // AARCH64

  logic_op(x->op(), rlock_result(x), left.result(), right.result());
}


// _lcmp, _fcmpl, _fcmpg, _dcmpl, _dcmpg
void LIRGenerator::do_CompareOp(CompareOp* x) {
#ifdef __SOFTFP__
  address runtime_func;
  switch (x->op()) {
    case Bytecodes::_fcmpl:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::fcmpl);
      break;
    case Bytecodes::_fcmpg:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::fcmpg);
      break;
    case Bytecodes::_dcmpl:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dcmpl);
      break;
    case Bytecodes::_dcmpg:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dcmpg);
      break;
    case Bytecodes::_lcmp: {
        LIRItem left(x->x(), this);
        LIRItem right(x->y(), this);
        left.load_item();
        right.load_nonconstant();
        LIR_Opr reg = rlock_result(x);
         __ lcmp2int(left.result(), right.result(), reg);
        return;
      }
    default:
      ShouldNotReachHere();
  }
  LIR_Opr result = call_runtime(x->x(), x->y(), runtime_func, x->type(), NULL);
  set_result(x, result);
#else // __SOFTFP__
  LIRItem left(x->x(), this);
  LIRItem right(x->y(), this);
  left.load_item();

#ifdef AARCH64
  if (right.is_constant() && can_inline_as_constant_in_cmp(right.value())) {
    right.dont_load_item();
  } else {
    right.load_item();
  }
#else
  right.load_nonconstant();
#endif // AARCH64

  LIR_Opr reg = rlock_result(x);

  if (x->x()->type()->is_float_kind()) {
    Bytecodes::Code code = x->op();
    __ fcmp2int(left.result(), right.result(), reg, (code == Bytecodes::_fcmpl || code == Bytecodes::_dcmpl));
  } else if (x->x()->type()->tag() == longTag) {
    __ lcmp2int(left.result(), right.result(), reg);
  } else {
    ShouldNotReachHere();
  }
#endif // __SOFTFP__
}


void LIRGenerator::do_CompareAndSwap(Intrinsic* x, ValueType* type) {
  assert(x->number_of_arguments() == 4, "wrong type");
  LIRItem obj   (x->argument_at(0), this);  // object
  LIRItem offset(x->argument_at(1), this);  // offset of field
  LIRItem cmp   (x->argument_at(2), this);  // value to compare with field
  LIRItem val   (x->argument_at(3), this);  // replace field with val if matches cmp

  LIR_Opr addr = new_pointer_register();
  LIR_Opr tmp1 = LIR_OprFact::illegalOpr;
  LIR_Opr tmp2 = LIR_OprFact::illegalOpr;

  // get address of field
  obj.load_item();
  offset.load_item();
  cmp.load_item();
  val.load_item();

  __ add(obj.result(), offset.result(), addr);
  LIR_Opr result = rlock_result(x);

  if (type == objectType) {
#if INCLUDE_ALL_GCS
    // Do the pre-write barrier, if any.
    pre_barrier(addr, LIR_OprFact::illegalOpr /* pre_val */,
                true /* do_load */, false /* patch */, NULL);
#endif // INCLUDE_ALL_GCS
#ifdef AARCH64
    if (UseCompressedOops) {
      tmp1 = new_pointer_register();
      tmp2 = new_pointer_register();
    }
#endif // AARCH64
    __ cas_obj(addr, cmp.result(), val.result(), tmp1, tmp2, result);
    post_barrier(addr, val.result());
  }
  else if (type == intType) {
    __ cas_int(addr, cmp.result(), val.result(), tmp1, tmp1, result);
  }
  else if (type == longType) {
#ifndef AARCH64
    tmp1 = new_register(T_LONG);
#endif // !AARCH64
    __ cas_long(addr, cmp.result(), val.result(), tmp1, tmp2, result);
  }
  else {
    ShouldNotReachHere();
  }
}


void LIRGenerator::do_MathIntrinsic(Intrinsic* x) {
  address runtime_func;
  switch (x->id()) {
    case vmIntrinsics::_dabs: {
#ifdef __SOFTFP__
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dabs);
      break;
#else
      assert(x->number_of_arguments() == 1, "wrong type");
      LIRItem value(x->argument_at(0), this);
      value.load_item();
      __ abs(value.result(), rlock_result(x), LIR_OprFact::illegalOpr);
      return;
#endif // __SOFTFP__
    }
    case vmIntrinsics::_dsqrt: {
#ifdef __SOFTFP__
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dsqrt);
      break;
#else
      assert(x->number_of_arguments() == 1, "wrong type");
      LIRItem value(x->argument_at(0), this);
      value.load_item();
      __ sqrt(value.result(), rlock_result(x), LIR_OprFact::illegalOpr);
      return;
#endif // __SOFTFP__
    }
    case vmIntrinsics::_dsin:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dsin);
      break;
    case vmIntrinsics::_dcos:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dcos);
      break;
    case vmIntrinsics::_dtan:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dtan);
      break;
    case vmIntrinsics::_dlog:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dlog);
      break;
    case vmIntrinsics::_dlog10:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dlog10);
      break;
    case vmIntrinsics::_dexp:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dexp);
      break;
    case vmIntrinsics::_dpow:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::dpow);
      break;
    default:
      ShouldNotReachHere();
      return;
  }

  LIR_Opr result;
  if (x->number_of_arguments() == 1) {
    result = call_runtime(x->argument_at(0), runtime_func, x->type(), NULL);
  } else {
    assert(x->number_of_arguments() == 2 && x->id() == vmIntrinsics::_dpow, "unexpected intrinsic");
    result = call_runtime(x->argument_at(0), x->argument_at(1), runtime_func, x->type(), NULL);
  }
  set_result(x, result);
}

void LIRGenerator::do_FmaIntrinsic(Intrinsic* x) {
  fatal("FMA intrinsic is not implemented on this platform");
}

void LIRGenerator::do_vectorizedMismatch(Intrinsic* x) {
  fatal("vectorizedMismatch intrinsic is not implemented on this platform");
}

void LIRGenerator::do_ArrayCopy(Intrinsic* x) {
  CodeEmitInfo* info = state_for(x, x->state());
  assert(x->number_of_arguments() == 5, "wrong type");
  LIRItem src(x->argument_at(0), this);
  LIRItem src_pos(x->argument_at(1), this);
  LIRItem dst(x->argument_at(2), this);
  LIRItem dst_pos(x->argument_at(3), this);
  LIRItem length(x->argument_at(4), this);

  // We put arguments into the same registers which are used for a Java call.
  // Note: we used fixed registers for all arguments because all registers
  // are caller-saved, so register allocator treats them all as used.
  src.load_item_force    (FrameMap::R0_oop_opr);
  src_pos.load_item_force(FrameMap::R1_opr);
  dst.load_item_force    (FrameMap::R2_oop_opr);
  dst_pos.load_item_force(FrameMap::R3_opr);
  length.load_item_force (FrameMap::R4_opr);
  LIR_Opr tmp =          (FrameMap::R5_opr);
  set_no_result(x);

  int flags;
  ciArrayKlass* expected_type;
  arraycopy_helper(x, &flags, &expected_type);
  __ arraycopy(src.result(), src_pos.result(), dst.result(), dst_pos.result(), length.result(),
               tmp, expected_type, flags, info);
}

void LIRGenerator::do_update_CRC32(Intrinsic* x) {
  fatal("CRC32 intrinsic is not implemented on this platform");
}

void LIRGenerator::do_update_CRC32C(Intrinsic* x) {
  Unimplemented();
}

void LIRGenerator::do_Convert(Convert* x) {
  address runtime_func;
  switch (x->op()) {
#ifndef AARCH64
    case Bytecodes::_l2f:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::l2f);
      break;
    case Bytecodes::_l2d:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::l2d);
      break;
    case Bytecodes::_f2l:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::f2l);
      break;
    case Bytecodes::_d2l:
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::d2l);
      break;
#ifdef __SOFTFP__
    case Bytecodes::_f2d:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_f2d);
      break;
    case Bytecodes::_d2f:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_d2f);
      break;
    case Bytecodes::_i2f:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_i2f);
      break;
    case Bytecodes::_i2d:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_i2d);
      break;
    case Bytecodes::_f2i:
      runtime_func = CAST_FROM_FN_PTR(address, __aeabi_f2iz);
      break;
    case Bytecodes::_d2i:
      // This is implemented in hard float in assembler on arm but a call
      // on other platforms.
      runtime_func = CAST_FROM_FN_PTR(address, SharedRuntime::d2i);
      break;
#endif // __SOFTFP__
#endif // !AARCH64
    default: {
      LIRItem value(x->value(), this);
      value.load_item();
      LIR_Opr reg = rlock_result(x);
      __ convert(x->op(), value.result(), reg, NULL);
      return;
    }
  }

  LIR_Opr result = call_runtime(x->value(), runtime_func, x->type(), NULL);
  set_result(x, result);
}


void LIRGenerator::do_NewInstance(NewInstance* x) {
  print_if_not_loaded(x);

  CodeEmitInfo* info = state_for(x, x->state());
  LIR_Opr reg = result_register_for(x->type());  // R0 is required by runtime call in NewInstanceStub::emit_code
  LIR_Opr klass_reg = FrameMap::R1_metadata_opr; // R1 is required by runtime call in NewInstanceStub::emit_code
  LIR_Opr tmp1 = new_register(objectType);
  LIR_Opr tmp2 = new_register(objectType);
  LIR_Opr tmp3 = FrameMap::LR_oop_opr;

  new_instance(reg, x->klass(), x->is_unresolved(), tmp1, tmp2, tmp3,
               LIR_OprFact::illegalOpr, klass_reg, info);

  LIR_Opr result = rlock_result(x);
  __ move(reg, result);
}


void LIRGenerator::do_NewTypeArray(NewTypeArray* x) {
  // Evaluate state_for() first, because it can emit code
  // with the same fixed registers that are used here (R1, R2)
  CodeEmitInfo* info = state_for(x, x->state());
  LIRItem length(x->length(), this);

  length.load_item_force(FrameMap::R2_opr);      // R2 is required by runtime call in NewTypeArrayStub::emit_code
  LIR_Opr len = length.result();

  LIR_Opr reg = result_register_for(x->type());  // R0 is required by runtime call in NewTypeArrayStub::emit_code
  LIR_Opr klass_reg = FrameMap::R1_metadata_opr; // R1 is required by runtime call in NewTypeArrayStub::emit_code

  LIR_Opr tmp1 = new_register(objectType);
  LIR_Opr tmp2 = new_register(objectType);
  LIR_Opr tmp3 = FrameMap::LR_oop_opr;
  LIR_Opr tmp4 = LIR_OprFact::illegalOpr;

  BasicType elem_type = x->elt_type();
  __ metadata2reg(ciTypeArrayKlass::make(elem_type)->constant_encoding(), klass_reg);

  CodeStub* slow_path = new NewTypeArrayStub(klass_reg, len, reg, info);
  __ allocate_array(reg, len, tmp1, tmp2, tmp3, tmp4, elem_type, klass_reg, slow_path);

  LIR_Opr result = rlock_result(x);
  __ move(reg, result);
}


void LIRGenerator::do_NewObjectArray(NewObjectArray* x) {
  // Evaluate state_for() first, because it can emit code
  // with the same fixed registers that are used here (R1, R2)
  CodeEmitInfo* info = state_for(x, x->state());
  LIRItem length(x->length(), this);

  length.load_item_force(FrameMap::R2_opr);           // R2 is required by runtime call in NewObjectArrayStub::emit_code
  LIR_Opr len = length.result();

  CodeEmitInfo* patching_info = NULL;
  if (!x->klass()->is_loaded() || PatchALot) {
    patching_info = state_for(x, x->state_before());
  }

  LIR_Opr reg = result_register_for(x->type());       // R0 is required by runtime call in NewObjectArrayStub::emit_code
  LIR_Opr klass_reg = FrameMap::R1_metadata_opr;      // R1 is required by runtime call in NewObjectArrayStub::emit_code

  LIR_Opr tmp1 = new_register(objectType);
  LIR_Opr tmp2 = new_register(objectType);
  LIR_Opr tmp3 = FrameMap::LR_oop_opr;
  LIR_Opr tmp4 = LIR_OprFact::illegalOpr;

  CodeStub* slow_path = new NewObjectArrayStub(klass_reg, len, reg, info);
  ciMetadata* obj = ciObjArrayKlass::make(x->klass());
  if (obj == ciEnv::unloaded_ciobjarrayklass()) {
    BAILOUT("encountered unloaded_ciobjarrayklass due to out of memory error");
  }
  klass2reg_with_patching(klass_reg, obj, patching_info);
  __ allocate_array(reg, len, tmp1, tmp2, tmp3, tmp4, T_OBJECT, klass_reg, slow_path);

  LIR_Opr result = rlock_result(x);
  __ move(reg, result);
}


void LIRGenerator::do_NewMultiArray(NewMultiArray* x) {
  Values* dims = x->dims();
  int i = dims->length();
  LIRItemList* items = new LIRItemList(i, i, NULL);
  while (i-- > 0) {
    LIRItem* size = new LIRItem(dims->at(i), this);
    items->at_put(i, size);
  }

  // Need to get the info before, as the items may become invalid through item_free
  CodeEmitInfo* patching_info = NULL;
  if (!x->klass()->is_loaded() || PatchALot) {
    patching_info = state_for(x, x->state_before());

    // Cannot re-use same xhandlers for multiple CodeEmitInfos, so
    // clone all handlers (NOTE: Usually this is handled transparently
    // by the CodeEmitInfo cloning logic in CodeStub constructors but
    // is done explicitly here because a stub isn't being used).
    x->set_exception_handlers(new XHandlers(x->exception_handlers()));
  }

  i = dims->length();
  while (i-- > 0) {
    LIRItem* size = items->at(i);
    size->load_item();
    LIR_Opr sz = size->result();
    assert(sz->type() == T_INT, "should be");
    store_stack_parameter(sz, in_ByteSize(i * BytesPerInt));
  }

  CodeEmitInfo* info = state_for(x, x->state());
  LIR_Opr klass_reg = FrameMap::R0_metadata_opr;
  klass2reg_with_patching(klass_reg, x->klass(), patching_info);

  LIR_Opr rank = FrameMap::R2_opr;
  __ move(LIR_OprFact::intConst(x->rank()), rank);
  LIR_Opr varargs = FrameMap::SP_opr;
  LIR_OprList* args = new LIR_OprList(3);
  args->append(klass_reg);
  args->append(rank);
  args->append(varargs);
  LIR_Opr reg = result_register_for(x->type());
  __ call_runtime(Runtime1::entry_for(Runtime1::new_multi_array_id),
                  LIR_OprFact::illegalOpr, reg, args, info);

  LIR_Opr result = rlock_result(x);
  __ move(reg, result);
}


void LIRGenerator::do_BlockBegin(BlockBegin* x) {
  // nothing to do for now
}


void LIRGenerator::do_CheckCast(CheckCast* x) {
  LIRItem obj(x->obj(), this);
  CodeEmitInfo* patching_info = NULL;
  if (!x->klass()->is_loaded() || (PatchALot && !x->is_incompatible_class_change_check())) {
    patching_info = state_for(x, x->state_before());
  }

  obj.load_item();

  CodeEmitInfo* info_for_exception =
    (x->needs_exception_state() ? state_for(x) :
                                  state_for(x, x->state_before(), true /*ignore_xhandler*/));

  CodeStub* stub;
  if (x->is_incompatible_class_change_check()) {
    assert(patching_info == NULL, "can't patch this");
    stub = new SimpleExceptionStub(Runtime1::throw_incompatible_class_change_error_id,
                                   LIR_OprFact::illegalOpr, info_for_exception);
  } else if (x->is_invokespecial_receiver_check()) {
    assert(patching_info == NULL, "can't patch this");
    stub = new DeoptimizeStub(info_for_exception,
                              Deoptimization::Reason_class_check,
                              Deoptimization::Action_none);
  } else {
    stub = new SimpleExceptionStub(Runtime1::throw_class_cast_exception_id,
                                   LIR_OprFact::illegalOpr, info_for_exception);
  }

  LIR_Opr out_reg = rlock_result(x);
  LIR_Opr tmp1 = FrameMap::R0_oop_opr;
  LIR_Opr tmp2 = FrameMap::R1_oop_opr;
  LIR_Opr tmp3 = LIR_OprFact::illegalOpr;

  __ checkcast(out_reg, obj.result(), x->klass(), tmp1, tmp2, tmp3, x->direct_compare(),
               info_for_exception, patching_info, stub, x->profiled_method(), x->profiled_bci());
}


void LIRGenerator::do_InstanceOf(InstanceOf* x) {
  LIRItem obj(x->obj(), this);
  CodeEmitInfo* patching_info = NULL;
  if (!x->klass()->is_loaded() || PatchALot) {
    patching_info = state_for(x, x->state_before());
  }

  obj.load_item();
  LIR_Opr out_reg = rlock_result(x);
  LIR_Opr tmp1 = FrameMap::R0_oop_opr;
  LIR_Opr tmp2 = FrameMap::R1_oop_opr;
  LIR_Opr tmp3 = LIR_OprFact::illegalOpr;

  __ instanceof(out_reg, obj.result(), x->klass(), tmp1, tmp2, tmp3,
                x->direct_compare(), patching_info, x->profiled_method(), x->profiled_bci());
}


#ifdef __SOFTFP__
// Turn operator if (f <op> g) into runtime call:
//     call _aeabi_fcmp<op>(f, g)
//     cmp(eq, 1)
//     branch(eq, true path).
void LIRGenerator::do_soft_float_compare(If* x) {
  assert(x->number_of_sux() == 2, "inconsistency");
  ValueTag tag = x->x()->type()->tag();
  If::Condition cond = x->cond();
  address runtime_func;
  // unordered comparison gets the wrong answer because aeabi functions
  //  return false.
  bool unordered_is_true = x->unordered_is_true();
  // reverse of condition for ne
  bool compare_to_zero = false;
  switch (lir_cond(cond)) {
    case lir_cond_notEqual:
      compare_to_zero = true;  // fall through
    case lir_cond_equal:
      runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, __aeabi_fcmpeq):
          CAST_FROM_FN_PTR(address, __aeabi_dcmpeq);
      break;
    case lir_cond_less:
      if (unordered_is_true) {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_fcmplt):
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_dcmplt);
      } else {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, __aeabi_fcmplt):
          CAST_FROM_FN_PTR(address, __aeabi_dcmplt);
      }
      break;
    case lir_cond_lessEqual:
      if (unordered_is_true) {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_fcmple):
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_dcmple);
      } else {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, __aeabi_fcmple):
          CAST_FROM_FN_PTR(address, __aeabi_dcmple);
      }
      break;
    case lir_cond_greaterEqual:
      if (unordered_is_true) {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_fcmpge):
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_dcmpge);
      } else {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, __aeabi_fcmpge):
          CAST_FROM_FN_PTR(address, __aeabi_dcmpge);
      }
      break;
    case lir_cond_greater:
      if (unordered_is_true) {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_fcmpgt):
          CAST_FROM_FN_PTR(address, SharedRuntime::unordered_dcmpgt);
      } else {
        runtime_func = tag == floatTag ?
          CAST_FROM_FN_PTR(address, __aeabi_fcmpgt):
          CAST_FROM_FN_PTR(address, __aeabi_dcmpgt);
      }
      break;
    case lir_cond_aboveEqual:
    case lir_cond_belowEqual:
      ShouldNotReachHere();  // We're not going to get these.
    default:
      assert(lir_cond(cond) == lir_cond_always, "must be");
      ShouldNotReachHere();
  }
  set_no_result(x);

  // add safepoint before generating condition code so it can be recomputed
  if (x->is_safepoint()) {
    increment_backedge_counter(state_for(x, x->state_before()), x->profiled_bci());
    __ safepoint(LIR_OprFact::illegalOpr, state_for(x, x->state_before()));
  }
  // Call float compare function, returns (1,0) if true or false.
  LIR_Opr result = call_runtime(x->x(), x->y(), runtime_func, intType, NULL);
  __ cmp(lir_cond_equal, result,
         compare_to_zero ?
           LIR_OprFact::intConst(0) : LIR_OprFact::intConst(1));
  profile_branch(x, cond);
  move_to_phi(x->state());
  __ branch(lir_cond_equal, T_INT, x->tsux());
}
#endif // __SOFTFP__

void LIRGenerator::do_If(If* x) {
  assert(x->number_of_sux() == 2, "inconsistency");
  ValueTag tag = x->x()->type()->tag();

#ifdef __SOFTFP__
  if (tag == floatTag || tag == doubleTag) {
    do_soft_float_compare(x);
    assert(x->default_sux() == x->fsux(), "wrong destination above");
    __ jump(x->default_sux());
    return;
  }
#endif // __SOFTFP__

  LIRItem xitem(x->x(), this);
  LIRItem yitem(x->y(), this);
  LIRItem* xin = &xitem;
  LIRItem* yin = &yitem;
  If::Condition cond = x->cond();

#ifndef AARCH64
  if (tag == longTag) {
    if (cond == If::gtr || cond == If::leq) {
      cond = Instruction::mirror(cond);
      xin = &yitem;
      yin = &xitem;
    }
    xin->set_destroys_register();
  }
#endif // !AARCH64

  xin->load_item();
  LIR_Opr left = xin->result();
  LIR_Opr right;

#ifdef AARCH64
  if (yin->is_constant() && can_inline_as_constant_in_cmp(yin->value())) {
    yin->dont_load_item();
  } else {
    yin->load_item();
  }
  right = yin->result();
#else
  if (tag == longTag && yin->is_constant() && yin->get_jlong_constant() == 0 &&
      (cond == If::eql || cond == If::neq)) {
    // inline long zero
    right = LIR_OprFact::value_type(yin->value()->type());
  } else {
    yin->load_nonconstant();
    right = yin->result();
  }
#endif // AARCH64

  set_no_result(x);

  // add safepoint before generating condition code so it can be recomputed
  if (x->is_safepoint()) {
    increment_backedge_counter(state_for(x, x->state_before()), x->profiled_bci());
    __ safepoint(LIR_OprFact::illegalOpr, state_for(x, x->state_before()));
  }

  __ cmp(lir_cond(cond), left, right);
  profile_branch(x, cond);
  move_to_phi(x->state());
  if (x->x()->type()->is_float_kind()) {
    __ branch(lir_cond(cond), right->type(), x->tsux(), x->usux());
  } else {
    __ branch(lir_cond(cond), right->type(), x->tsux());
  }
  assert(x->default_sux() == x->fsux(), "wrong destination above");
  __ jump(x->default_sux());
}


LIR_Opr LIRGenerator::getThreadPointer() {
  return FrameMap::Rthread_opr;
}

void LIRGenerator::trace_block_entry(BlockBegin* block) {
  __ move(LIR_OprFact::intConst(block->block_id()), FrameMap::R0_opr);
  LIR_OprList* args = new LIR_OprList(1);
  args->append(FrameMap::R0_opr);
  address func = CAST_FROM_FN_PTR(address, Runtime1::trace_block_entry);
  __ call_runtime_leaf(func, getThreadTemp(), LIR_OprFact::illegalOpr, args);
}


void LIRGenerator::volatile_field_store(LIR_Opr value, LIR_Address* address,
                                        CodeEmitInfo* info) {
#ifndef AARCH64
  if (value->is_double_cpu()) {
    assert(address->index()->is_illegal(), "should have a constant displacement");
    LIR_Opr tmp = new_pointer_register();
    add_large_constant(address->base(), address->disp(), tmp);
    __ volatile_store_mem_reg(value, new LIR_Address(tmp, (intx)0, address->type()), info);
    return;
  }
#endif // !AARCH64
  // TODO-AARCH64 implement with stlr instruction
  __ store(value, address, info, lir_patch_none);
}

void LIRGenerator::volatile_field_load(LIR_Address* address, LIR_Opr result,
                                       CodeEmitInfo* info) {
#ifndef AARCH64
  if (result->is_double_cpu()) {
    assert(address->index()->is_illegal(), "should have a constant displacement");
    LIR_Opr tmp = new_pointer_register();
    add_large_constant(address->base(), address->disp(), tmp);
    __ volatile_load_mem_reg(new LIR_Address(tmp, (intx)0, address->type()), result, info);
    return;
  }
#endif // !AARCH64
  // TODO-AARCH64 implement with ldar instruction
  __ load(address, result, info, lir_patch_none);
}

void LIRGenerator::get_Object_unsafe(LIR_Opr dst, LIR_Opr src, LIR_Opr offset,
                                     BasicType type, bool is_volatile) {
#ifdef AARCH64
  __ load(new LIR_Address(src, offset, type), dst);
#else
  assert(offset->is_single_cpu(), "must be");
  if (is_volatile && dst->is_double_cpu()) {
    LIR_Opr tmp = new_pointer_register();
    __ add(src, offset, tmp);
    __ volatile_load_mem_reg(new LIR_Address(tmp, (intx)0, type), dst, NULL);
  } else if (type == T_FLOAT || type == T_DOUBLE) {
    // fld doesn't have indexed addressing mode
    LIR_Opr tmp = new_register(T_INT);
    __ add(src, offset, tmp);
    __ load(new LIR_Address(tmp, (intx)0, type), dst);
  } else {
    __ load(new LIR_Address(src, offset, type), dst);
  }
#endif // AARCH64
}

void LIRGenerator::put_Object_unsafe(LIR_Opr src, LIR_Opr offset, LIR_Opr data,
                                     BasicType type, bool is_volatile) {
#ifdef AARCH64
  LIR_Address* addr = new LIR_Address(src, offset, type);
  if (type == T_ARRAY || type == T_OBJECT) {
    pre_barrier(LIR_OprFact::address(addr), LIR_OprFact::illegalOpr /* pre_val */,
                true /* do_load */, false /* patch */, NULL);
    __ move(data, addr);
    assert(src->is_register(), "must be register");
    post_barrier(LIR_OprFact::address(addr), data);
  } else {
    __ move(data, addr);
  }
#else
  assert(offset->is_single_cpu(), "must be");
  if (is_volatile && data->is_double_cpu()) {
    LIR_Opr tmp = new_register(T_INT);
    __ add(src, offset, tmp);
    __ volatile_store_mem_reg(data, new LIR_Address(tmp, (intx)0, type), NULL);
  } else if (type == T_FLOAT || type == T_DOUBLE) {
    // fst doesn't have indexed addressing mode
    LIR_Opr tmp = new_register(T_INT);
    __ add(src, offset, tmp);
    __ move(data, new LIR_Address(tmp, (intx)0, type));
  } else {
    LIR_Address* addr = new LIR_Address(src, offset, type);
    bool is_obj = (type == T_ARRAY || type == T_OBJECT);
#if INCLUDE_ALL_GCS
    if (is_obj) {
      // Do the pre-write barrier, if any.
      pre_barrier(LIR_OprFact::address(addr), LIR_OprFact::illegalOpr /* pre_val */,
                  true /* do_load */, false /* patch */, NULL);
    }
#endif // INCLUDE_ALL_GCS
    __ move(data, addr);
    if (is_obj) {
      assert(src->is_register(), "must be register");
      post_barrier(LIR_OprFact::address(addr), data);
    }
  }
#endif // AARCH64
}

void LIRGenerator::do_UnsafeGetAndSetObject(UnsafeGetAndSetObject* x) {
  BasicType type = x->basic_type();
  LIRItem src(x->object(), this);
  LIRItem off(x->offset(), this);
  LIRItem value(x->value(), this);

  src.load_item();
  if (x->is_add()) {
    value.load_nonconstant();
  } else {
    value.load_item();
  }
  off.load_nonconstant();

  LIR_Opr dst = rlock_result(x, type);
  LIR_Opr data = value.result();
  bool is_obj = (type == T_ARRAY || type == T_OBJECT);

  assert (type == T_INT || type == T_LONG || (!x->is_add() && is_obj), "unexpected type");
  LIR_Opr addr_ptr = new_pointer_register();

  __ add(src.result(), off.result(), addr_ptr);

  LIR_Address* addr = new LIR_Address(addr_ptr, (intx)0, type);

  if (x->is_add()) {
    LIR_Opr tmp = new_register(type);
    __ xadd(addr_ptr, data, dst, tmp);
  } else {
    LIR_Opr tmp = (UseCompressedOops && is_obj) ? new_pointer_register() : LIR_OprFact::illegalOpr;
    if (is_obj) {
      // Do the pre-write barrier, if any.
      pre_barrier(LIR_OprFact::address(addr), LIR_OprFact::illegalOpr /* pre_val */,
                  true /* do_load */, false /* patch */, NULL);
    }
    __ xchg(addr_ptr, data, dst, tmp);
    if (is_obj) {
      // Seems to be a precise address
      post_barrier(LIR_OprFact::address(addr), data);
    }
  }
}
