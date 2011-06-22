/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.sql;

import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Guard;
import java.security.Permission;

/**
 * Legacy security code; do not use.
 */
public final class SQLPermission extends BasicPermission implements Guard, Serializable {
    public SQLPermission(String name) { super(""); }

    public SQLPermission(String name, String actions) { super("", ""); }

    @Override public String getActions() { return null; }

    @Override public boolean implies(Permission permission) { return true; }
}
