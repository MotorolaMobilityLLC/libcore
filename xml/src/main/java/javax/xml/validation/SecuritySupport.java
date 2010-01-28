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
// $Id: SecuritySupport.java 522337 2007-03-25 20:03:27Z mrglavas $

package javax.xml.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

/**
 * This class is duplicated for each JAXP subpackage so keep it in sync.
 * It is package private and therefore is not exposed as part of the JAXP
 * API.
 *
 * Security related methods that only work on J2SE 1.2 and newer.
 */
final class SecuritySupport  {
    
    private SecuritySupport() {}
    
    static ClassLoader getContextClassLoader() {
	return (ClassLoader)
		AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		ClassLoader cl = null;
		try {
		    cl = Thread.currentThread().getContextClassLoader();
		} catch (SecurityException ex) { }
		return cl;
	    }
	});
    }

    static String getSystemProperty(final String propName) {
	return (String)
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return System.getProperty(propName);
                }
            });
    }

    static FileInputStream getFileInputStream(final File file)
        throws FileNotFoundException
    {
	try {
            return (FileInputStream)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws FileNotFoundException {
                        return new FileInputStream(file);
                    }
                });
	} catch (PrivilegedActionException e) {
	    throw (FileNotFoundException)e.getException();
	}
    }

    static InputStream getURLInputStream(final URL url)
        throws IOException
    {
	try {
            return (InputStream)
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws IOException {
                        return url.openStream();
                    }
                });
	} catch (PrivilegedActionException e) {
	    throw (IOException)e.getException();
	}
    }

    static URL getResourceAsURL(final ClassLoader cl,
                                final String name)
    {
        return (URL)
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    URL url;
                    if (cl == null) {
                        url = ClassLoader.getSystemResource(name);
                    } 
                    else {
                        url = cl.getResource(name);
                    }
                    return url;
                }
            });
    }

    static Enumeration getResources(final ClassLoader cl,
                                    final String name) throws IOException
    {
        try{
        return (Enumeration)
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException{
                    Enumeration enumeration;
                    if (cl == null) {
                        enumeration = ClassLoader.getSystemResources(name);
                    } 
                    else {
                        enumeration = cl.getResources(name);
                    }
                    return enumeration;
                }
            });
        }catch(PrivilegedActionException e){
            throw (IOException)e.getException();
        }
    }
    
    static InputStream getResourceAsStream(final ClassLoader cl,
                                           final String name)
    {
        return (InputStream)
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    InputStream ris;
                    if (cl == null) {
                        ris = ClassLoader.getSystemResourceAsStream(name);
                    } else {
                        ris = cl.getResourceAsStream(name);
                    }
                    return ris;
                }
            });
    }

    static boolean doesFileExist(final File f) {
    return ((Boolean)
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return f.exists() ? Boolean.TRUE : Boolean.FALSE;
                }
            })).booleanValue();
    }

}
