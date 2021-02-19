#
# Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved.
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
#
# This code is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 2 only, as
# published by the Free Software Foundation.
#
# This code is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# version 2 for more details (a copy is included in the LICENSE file that
# accompanied this code).
#
# You should have received a copy of the GNU General Public License version
# 2 along with this work; if not, write to the Free Software Foundation,
# Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
#
# Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
# or visit www.oracle.com if you need additional information or have any
# questions.
#

# @test
# @bug 4990825
# @run shell jstatFileURITest1.sh
# @summary Test that output of 'jstat -gcutil file:path' has expected line counts

. ${TESTSRC-.}/../../jvmstat/testlibrary/utils.sh

setup

JSTAT="${TESTJAVA}/bin/jstat"
RC=0

OS=`uname -s`
case ${OS} in
Windows*)
    # work-around for strange problems trying to translate back slash
    # characters into forward slash characters in an effort to convert
    # TESTSRC into a canonical form useable as URI path.
    cp ${TESTSRC}/hsperfdata_3433 .
    ${JSTAT} -J-XX:+UsePerfData -gcutil file:/`pwd`/hsperfdata_3433 2>&1 | awk -f ${TESTSRC}/fileURITest1.awk
    RC=$?
    rm -f hsperfdata_3433 2>&1 > /dev/null
    ;;
*)
    ${JSTAT} -J-XX:+UsePerfData -gcutil file:${TESTSRC}/hsperfdata_3433 2>&1 | awk -f ${TESTSRC}/fileURITest1.awk
    RC=$?
    ;;
esac

echo ${RC}
