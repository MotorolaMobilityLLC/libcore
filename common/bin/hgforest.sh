#!/bin/sh

#
# Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
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

# Shell script for a fast parallel forest command

global_opts=""
status_output="/dev/stdout"
qflag="false"
vflag="false"
sflag="false"
while [ $# -gt 0 ]
do
  case $1 in
    -q | --quiet )
      qflag="true"
      global_opts="${global_opts} -q"
      status_output="/dev/null"
      ;;

    -v | --verbose )
      vflag="true"
      global_opts="${global_opts} -v"
      ;;

    -s | --sequential )
      sflag="true"
      ;;

    '--' ) # no more options
      shift; break
      ;;

    -*)  # bad option
      usage
      ;;

     * )  # non option
      break
      ;;
  esac
  shift
done


command="$1"; shift
command_args="$@"

usage() {
      echo "usage: $0 [-q|--quiet] [-v|--verbose] [-s|--sequential] [--] <command> [commands...]" > ${status_output}
      exit 1
}

if [ "x" = "x$command" ] ; then
  echo "ERROR: No command to hg supplied!"
  usage
fi

# Check if we can use fifos for monitoring sub-process completion.
on_windows=`uname -s | egrep -ic -e 'cygwin|msys'`
if [ ${on_windows} = "1" ]; then
  # cygwin has (2014-04-18) broken (single writer only) FIFOs
  # msys has (2014-04-18) no FIFOs.
  have_fifos="false"
else
  have_fifos="true"
fi

# Clean out the temporary directory that stores the pid files.
tmp=/tmp/forest.$$
rm -f -r ${tmp}
mkdir -p ${tmp}

safe_interrupt () {
  if [ -d ${tmp} ]; then
    if [ "`ls ${tmp}/*.pid`" != "" ]; then
      echo "Waiting for processes ( `cat ${tmp}/*.pid | tr '\n' ' '`) to terminate nicely!" > ${status_output}
      sleep 1
      # Pipe stderr to dev/null to silence kill, that complains when trying to kill
      # a subprocess that has already exited.
      kill -TERM `cat ${tmp}/*.pid | tr '\n' ' '` 2> /dev/null
      wait
      echo "Interrupt complete!" > ${status_output}
    fi
    rm -f -r ${tmp}
  fi
  exit 130
}

nice_exit () {
  if [ -d ${tmp} ]; then
    if [ "`ls ${tmp}`" != "" ]; then
      wait
    fi
    rm -f -r ${tmp}
  fi
}

trap 'safe_interrupt' INT QUIT
trap 'nice_exit' EXIT

subrepos="corba jaxp jaxws langtools jdk hotspot nashorn"
subrepos_extra="jdk/src/closed jdk/make/closed jdk/test/closed hotspot/make/closed hotspot/src/closed hotspot/test/closed deploy install sponsors pubs"

# Only look in specific locations for possible forests (avoids long searches)
pull_default=""
repos=""
repos_extra=""
if [ "${command}" = "clone" -o "${command}" = "fclone" -o "${command}" = "tclone" ] ; then
  if [ ! -f .hg/hgrc ] ; then
    echo "ERROR: Need initial repository to use this script" > ${status_output}
    exit 1
  fi

  pull_default=`hg paths default`
  if [ "${pull_default}" = "" ] ; then
    echo "ERROR: Need initial clone with 'hg paths default' defined" > ${status_output}
    exit 1
  fi

  for i in ${subrepos} ; do
    if [ ! -f ${i}/.hg/hgrc ] ; then
      repos="${repos} ${i}"
    fi
  done

  pull_default_tail=`echo ${pull_default} | sed -e 's@^.*://[^/]*/\(.*\)@\1@'`

  if [ "${command_args}" != "" ] ; then
    if [ "x${pull_default}" = "x${pull_default_tail}" ] ; then
      echo "ERROR: Need initial clone from non-local source" > ${status_output}
      exit 1
    fi
    pull_extra="${command_args}/${pull_default_tail}"
    for i in ${subrepos_extra} ; do
      if [ ! -f ${i}/.hg/hgrc ] ; then
        repos_extra="${repos_extra} ${i}"
      fi
    done
  else
    if [ "x${pull_default}" = "x${pull_default_tail}" ] ; then
      # local source repo. Copy the extras ones that exist there.
      for i in ${subrepos_extra} ; do
        if [ -f ${pull_default}/${i}/.hg/hgrc -a ! -f ${i}/.hg/hgrc ] ; then
          # sub-repo there in source but not here
          repos_extra="${repos_extra} ${i}"
        fi
      done
    fi
  fi
  at_a_time=2
  # Any repos to deal with?
  if [ "${repos}" = "" -a "${repos_extra}" = "" ] ; then
    echo "No repositories to process." > ${status_output}
    exit
  fi
else
  for i in . ${subrepos} ${subrepos_extra} ; do
    if [ -d ${i}/.hg ] ; then
      repos="${repos} ${i}"
    fi
  done

  # Any repos to deal with?
  if [ "${repos}" = "" ] ; then
    echo "No repositories to process." > ${status_output}
    exit
  fi

  # any of the repos locked?
  for i in ${repos} ; do
    if [ -h ${i}/.hg/store/lock -o -f ${i}/.hg/store/lock ] ; then
      locked="${i} ${locked}"
    fi
  done
  if [ "${locked}" != "" ] ; then
    echo "ERROR: These repositories are locked: ${locked}" > ${status_output}
    exit 1
  fi
  at_a_time=8
fi

# Echo out what repositories we do a command on.
echo "# Repositories: ${repos} ${repos_extra}" > ${status_output}

if [ "${command}" = "serve" ] ; then
  # "serve" is run for all the repos.
  (
    (
      (
        echo "[web]"
        echo "description = $(basename $(pwd))"
        echo "allow_push = *"
        echo "push_ssl = False"

        echo "[paths]"
        for i in ${repos} ${repos_extra} ; do
          if [ "${i}" != "." ] ; then
            echo "/$(basename $(pwd))/${i} = ${i}"
          else
            echo "/$(basename $(pwd)) = $(pwd)"
          fi
        done
      ) > ${tmp}/serve.web-conf

      echo "serving root repo $(basename $(pwd))"

      (PYTHONUNBUFFERED=true hg${global_opts} serve -A ${status_output} -E ${status_output} --pid-file ${tmp}/serve.pid --web-conf ${tmp}/serve.web-conf; echo "$?" > ${tmp}/serve.pid.rc ) 2>&1 &
    ) 2>&1 | sed -e "s@^@serve:   @" > ${status_output}
  ) &
else
  # Run the supplied command on all repos in parallel.

  # n is the number of subprocess started or which might still be running.
  n=0
  if [ $have_fifos = "true" ]; then
    # if we have fifos use them to detect command completion.
    mkfifo ${tmp}/fifo
    exec 3<>${tmp}/fifo
    if [ "${sflag}" = "true" ] ; then
      # force sequential
      at_a_time=1
    fi
  fi

  for i in ${repos} ${repos_extra} ; do
    n=`expr ${n} '+' 1`
    repopidfile=`echo ${i} | sed -e 's@./@@' -e 's@/@_@g'`
    reponame=`echo ${i} | sed -e :a -e 's/^.\{1,20\}$/ &/;ta'`
    pull_base="${pull_default}"
    for j in $repos_extra ; do
      if [ "$i" = "$j" ] ; then
          pull_base="${pull_extra}"
      fi
    done
    pull_base="`echo ${pull_base} | sed -e 's@[/]*$@@'`"
    (
      (
        if [ "${command}" = "clone" -o "${command}" = "fclone" -o "${command}" = "tclone" ] ; then
          pull_newrepo="${pull_base}/${i}"
          path="`dirname ${i}`"
          if [ "${path}" != "." ] ; then
            times=0
            while [ ! -d "${path}" ]   ## nested repo, ensure containing dir exists
            do
              times=`expr ${times} '+' 1`
              if [ `expr ${times} '%' 10` -eq 0 ] ; then
                echo "${path} still not created, waiting..." > ${status_output}
              fi
              sleep 5
            done
          fi
          echo "hg${global_opts} clone ${pull_newrepo} ${i}" > ${status_output}
          (PYTHONUNBUFFERED=true hg${global_opts} clone ${pull_newrepo} ${i}; echo "$?" > ${tmp}/${repopidfile}.pid.rc ) 2>&1 &
        else
          echo "cd ${i} && hg${global_opts} ${command} ${command_args}" > ${status_output}
          cd ${i} && (PYTHONUNBUFFERED=true hg${global_opts} ${command} ${command_args}; echo "$?" > ${tmp}/${repopidfile}.pid.rc ) 2>&1 &
        fi

        echo $! > ${tmp}/${repopidfile}.pid
      ) 2>&1 | sed -e "s@^@${reponame}:   @" > ${status_output}
      if [ $have_fifos = "true" ]; then
        echo "${reponame}" >&3
      fi
    ) &

    if [ $have_fifos = "true" ]; then
      # check on count of running subprocesses and possibly wait for completion
      if [ ${at_a_time} -lt ${n} ] ; then
        # read will block until there are completed subprocesses
        while read repo_done; do
          n=`expr ${n} '-' 1`
          if [ ${n} -lt ${at_a_time} ] ; then
            # we should start more subprocesses
            break;
          fi
        done <&3
      fi
    else
      if [ "${sflag}" = "false" ] ; then
        # Compare completions to starts
        completed="`(ls -1 ${tmp}/*.pid.rc 2> /dev/null | wc -l) || echo 0`"
        while [ ${at_a_time} -lt `expr ${n} '-' ${completed}` ] ; do
          # sleep a short time to give time for something to complete
          sleep 1
          completed="`(ls -1 ${tmp}/*.pid.rc 2> /dev/null | wc -l) || echo 0`"
        done
      else
        # complete this task before starting another.
        wait
      fi
    fi
  done
fi

# Wait for all subprocesses to complete
wait

# Terminate with exit 0 only if all subprocesses were successful
ec=0
if [ -d ${tmp} ]; then
  for rc in ${tmp}/*.pid.rc ; do
    exit_code=`cat ${rc} | tr -d ' \n\r'`
    if [ "${exit_code}" != "0" ] ; then
      repo="`echo ${rc} | sed -e s@^${tmp}@@ -e 's@/*\([^/]*\)\.pid\.rc$@\1@' -e 's@_@/@g'`"
      echo "WARNING: ${repo} exited abnormally ($exit_code)" > ${status_output}
      ec=1
    fi
  done
fi
exit ${ec}
