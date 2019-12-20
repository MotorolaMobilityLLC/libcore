#
# Copyright (C) 2019 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Generated by running the following java through dx/baksmali:
# package java.lang;
#
# public class ThreadTestHelper {
#     static StackTraceElement[] createStackTrace() {
#         return Thread.currentThread().getStackTrace();
#     }
#
#     static public StackTraceElement[] debugInfo() {
#         return createStackTrace();
#     }
#
#     static public StackTraceElement[] noDebugInfo() {
#         return createStackTrace();
#     }
# }
# Additionally .line / .prologue declarations were removed from noDebugInfo
# so that no debug info is generated for that method and three nops were
# introduced so that the stack trace is a little bit more interesting.
.class public Ljava/lang/ThreadTestHelper;
.super Ljava/lang/Object;
.source "ThreadTestHelper.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method static createStackTrace()[Ljava/lang/StackTraceElement;
    .registers 1

    .prologue
    .line 5
    invoke-static {}, Ljava/lang/Thread;->currentThread()Ljava/lang/Thread;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Thread;->getStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v0

    return-object v0
.end method

.method public static debugInfo()[Ljava/lang/StackTraceElement;
    .registers 1

    .prologue
    .line 9
    invoke-static {}, Ljava/lang/ThreadTestHelper;->createStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v0

    return-object v0
.end method

.method public static noDebugInfo()[Ljava/lang/StackTraceElement;
    .registers 1

    # Removed so this method doesn't have debug info
    #.prologue
    #.line 13

    # Added so the stack trace looks more interesting
    nop
    nop
    nop
    invoke-static {}, Ljava/lang/ThreadTestHelper;->createStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v0

    return-object v0
.end method
