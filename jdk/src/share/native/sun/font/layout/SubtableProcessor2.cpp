/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

/*
 *
 * (C) Copyright IBM Corp.  and others 1998-2013 - All Rights Reserved
 *
 */

#include "LETypes.h"
#include "MorphTables.h"
#include "SubtableProcessor2.h"
#include "LESwaps.h"

U_NAMESPACE_BEGIN

SubtableProcessor2::SubtableProcessor2()
{
}

SubtableProcessor2::SubtableProcessor2(const LEReferenceTo<MorphSubtableHeader2> &morphSubtableHeader, LEErrorCode &success)
  : subtableHeader(morphSubtableHeader, success), length(0), coverage(0), subtableFeatures(0L)
{
  if(LE_FAILURE(success)) return;

  length = SWAPL(subtableHeader->length);
  coverage = SWAPL(subtableHeader->coverage);
  subtableFeatures = SWAPL(subtableHeader->subtableFeatures);
}

SubtableProcessor2::~SubtableProcessor2()
{
}

U_NAMESPACE_END
