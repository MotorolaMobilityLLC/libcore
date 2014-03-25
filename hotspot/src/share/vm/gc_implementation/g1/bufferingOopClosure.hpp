/*
 * Copyright (c) 2001, 2010, Oracle and/or its affiliates. All rights reserved.
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

#ifndef SHARE_VM_GC_IMPLEMENTATION_G1_BUFFERINGOOPCLOSURE_HPP
#define SHARE_VM_GC_IMPLEMENTATION_G1_BUFFERINGOOPCLOSURE_HPP

#include "memory/genOopClosures.hpp"
#include "memory/generation.hpp"
#include "runtime/os.hpp"
#include "utilities/taskqueue.hpp"

// A BufferingOops closure tries to separate out the cost of finding roots
// from the cost of applying closures to them.  It maintains an array of
// ref-containing locations.  Until the array is full, applying the closure
// to an oop* merely records that location in the array.  Since this
// closure app cost is small, an elapsed timer can approximately attribute
// all of this cost to the cost of finding the roots.  When the array fills
// up, the wrapped closure is applied to all elements, keeping track of
// this elapsed time of this process, and leaving the array empty.
// The caller must be sure to call "done" to process any unprocessed
// buffered entriess.

class Generation;
class HeapRegion;

class BufferingOopClosure: public OopClosure {
protected:
  enum PrivateConstants {
    BufferLength = 1024
  };

  StarTask  _buffer[BufferLength];
  StarTask* _buffer_top;
  StarTask* _buffer_curr;

  OopClosure* _oc;
  double      _closure_app_seconds;

  void process_buffer () {
    double start = os::elapsedTime();
    for (StarTask* curr = _buffer; curr < _buffer_curr; ++curr) {
      if (curr->is_narrow()) {
        assert(UseCompressedOops, "Error");
        _oc->do_oop((narrowOop*)(*curr));
      } else {
        _oc->do_oop((oop*)(*curr));
      }
    }
    _buffer_curr = _buffer;
    _closure_app_seconds += (os::elapsedTime() - start);
  }

  template <class T> inline void do_oop_work(T* p) {
    if (_buffer_curr == _buffer_top) {
      process_buffer();
    }
    StarTask new_ref(p);
    *_buffer_curr = new_ref;
    ++_buffer_curr;
  }

public:
  virtual void do_oop(narrowOop* p) { do_oop_work(p); }
  virtual void do_oop(oop* p)       { do_oop_work(p); }

  void done () {
    if (_buffer_curr > _buffer) {
      process_buffer();
    }
  }
  double closure_app_seconds () {
    return _closure_app_seconds;
  }
  BufferingOopClosure (OopClosure *oc) :
    _oc(oc),
    _buffer_curr(_buffer), _buffer_top(_buffer + BufferLength),
    _closure_app_seconds(0.0) { }
};

#endif // SHARE_VM_GC_IMPLEMENTATION_G1_BUFFERINGOOPCLOSURE_HPP
