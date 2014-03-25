/*
 * Copyright (c) 2001, 2012, Oracle and/or its affiliates. All rights reserved.
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

#ifndef SHARE_VM_GC_IMPLEMENTATION_SHARED_MUTABLESPACE_HPP
#define SHARE_VM_GC_IMPLEMENTATION_SHARED_MUTABLESPACE_HPP

#include "gc_implementation/shared/immutableSpace.hpp"
#include "memory/memRegion.hpp"
#include "utilities/copy.hpp"

// A MutableSpace is a subtype of ImmutableSpace that supports the
// concept of allocation. This includes the concepts that a space may
// be only partially full, and the querry methods that go with such
// an assumption. MutableSpace is also responsible for minimizing the
// page allocation time by having the memory pretouched (with
// AlwaysPretouch) and for optimizing page placement on NUMA systems
// by make the underlying region interleaved (with UseNUMA).
//
// Invariant: (ImmutableSpace +) bottom() <= top() <= end()
// top() is inclusive and end() is exclusive.

class MutableSpaceMangler;

class MutableSpace: public ImmutableSpace {
  friend class VMStructs;

  // Helper for mangling unused space in debug builds
  MutableSpaceMangler* _mangler;
  // The last region which page had been setup to be interleaved.
  MemRegion _last_setup_region;
  size_t _alignment;
 protected:
  HeapWord* _top;

  MutableSpaceMangler* mangler() { return _mangler; }

  void numa_setup_pages(MemRegion mr, bool clear_space);
  void pretouch_pages(MemRegion mr);

  void set_last_setup_region(MemRegion mr) { _last_setup_region = mr;   }
  MemRegion last_setup_region() const      { return _last_setup_region; }

 public:
  virtual ~MutableSpace();
  MutableSpace(size_t page_size);

  // Accessors
  HeapWord* top() const                    { return _top;    }
  virtual void set_top(HeapWord* value)    { _top = value;   }

  HeapWord** top_addr()                    { return &_top; }
  HeapWord** end_addr()                    { return &_end; }

  virtual void set_bottom(HeapWord* value) { _bottom = value; }
  virtual void set_end(HeapWord* value)    { _end = value; }

  size_t alignment()                       { return _alignment; }

  // Returns a subregion containing all objects in this space.
  MemRegion used_region() { return MemRegion(bottom(), top()); }

  static const bool SetupPages = true;
  static const bool DontSetupPages = false;

  // Initialization
  virtual void initialize(MemRegion mr,
                          bool clear_space,
                          bool mangle_space,
                          bool setup_pages = SetupPages);

  virtual void clear(bool mangle_space);
  // Does the usual initialization but optionally resets top to bottom.
#if 0  // MANGLE_SPACE
  void initialize(MemRegion mr, bool clear_space, bool reset_top);
#endif
  virtual void update() { }
  virtual void accumulate_statistics() { }

  // Methods used in mangling.  See descriptions under SpaceMangler.
  virtual void mangle_unused_area() PRODUCT_RETURN;
  virtual void mangle_unused_area_complete() PRODUCT_RETURN;
  virtual void check_mangled_unused_area(HeapWord* limit) PRODUCT_RETURN;
  virtual void check_mangled_unused_area_complete() PRODUCT_RETURN;
  virtual void set_top_for_allocations(HeapWord* v) PRODUCT_RETURN;

  // Used to save the space's current top for later use during mangling.
  virtual void set_top_for_allocations() PRODUCT_RETURN;

  virtual void ensure_parsability() { }

  virtual void mangle_region(MemRegion mr) PRODUCT_RETURN;

  // Boolean querries.
  bool is_empty() const              { return used_in_words() == 0; }
  bool not_empty() const             { return used_in_words() > 0; }
  bool contains(const void* p) const { return _bottom <= p && p < _end; }

  // Size computations.  Sizes are in bytes.
  size_t used_in_bytes() const                { return used_in_words() * HeapWordSize; }
  size_t free_in_bytes() const                { return free_in_words() * HeapWordSize; }

  // Size computations.  Sizes are in heapwords.
  virtual size_t used_in_words() const                    { return pointer_delta(top(), bottom()); }
  virtual size_t free_in_words() const                    { return pointer_delta(end(),    top()); }
  virtual size_t tlab_capacity(Thread* thr) const         { return capacity_in_bytes();            }
  virtual size_t tlab_used(Thread* thr) const             { return used_in_bytes();                }
  virtual size_t unsafe_max_tlab_alloc(Thread* thr) const { return free_in_bytes();                }

  // Allocation (return NULL if full)
  virtual HeapWord* allocate(size_t word_size);
  virtual HeapWord* cas_allocate(size_t word_size);
  // Optional deallocation. Used in NUMA-allocator.
  bool cas_deallocate(HeapWord *obj, size_t size);

  // Iteration.
  void oop_iterate(ExtendedOopClosure* cl);
  void oop_iterate_no_header(OopClosure* cl);
  void object_iterate(ObjectClosure* cl);

  // Debugging
  virtual void print() const;
  virtual void print_on(outputStream* st) const;
  virtual void print_short() const;
  virtual void print_short_on(outputStream* st) const;
  virtual void verify();
};

#endif // SHARE_VM_GC_IMPLEMENTATION_SHARED_MUTABLESPACE_HPP
