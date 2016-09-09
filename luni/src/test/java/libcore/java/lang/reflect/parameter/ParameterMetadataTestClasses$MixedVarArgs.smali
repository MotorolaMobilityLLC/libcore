#
# Copyright (C) 2016 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Originally generated using baksmali and edited. See README.txt in this directory.

.class Llibcore/java/lang/reflect/parameter/ParameterMetadataTestClasses$MixedVarArgs;
.super Ljava/lang/Object;
.source "ParameterMetadataTestClasses.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Llibcore/java/lang/reflect/parameter/ParameterMetadataTestClasses;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "MixedVarArgs"
.end annotation


# direct methods
.method varargs constructor <init>([Ljava/lang/Integer;[Ljava/lang/String;)V
    .registers 3
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x0,
            0x0
        }
        names = {
            "p0",
            "p1"
        }
    .end annotation

    .prologue
    .line 48
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method varargs both([Ljava/lang/Integer;[Ljava/lang/String;)V
    .registers 3
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x0,
            0x0
        }
        names = {
            "p0",
            "p1"
        }
    .end annotation

    .prologue
    .line 50
    return-void
.end method
