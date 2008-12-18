/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package tests.api.java.io;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass; 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Date;

@TestTargetClass(ObjectStreamField.class) 
public class ObjectStreamFieldTest extends junit.framework.TestCase {

    static class DummyClass implements Serializable {
        private static final long serialVersionUID = 999999999999998L;

        long bam = 999L;

        int ham = 9999;

        int sam = 8888;

        Object hola = new Object();

        public static long getUID() {
            return serialVersionUID;
        }
    }

    ObjectStreamClass osc;

    ObjectStreamField hamField;

    ObjectStreamField samField;

    ObjectStreamField bamField;

    ObjectStreamField holaField;

    /**
     * @tests java.io.ObjectStreamField#ObjectStreamField(java.lang.String,
     *        java.lang.Class)
     */
    @TestInfo(
            level = TestLevel.TODO,
            purpose = "Dummy test.",
            targets = { @TestTarget(methodName = "ObjectStreamField", 
                                    methodArgs = {java.lang.String.class,
                                                  java.lang.Class.class})                                    
            }
        )      
    public void test_ConstructorLjava_lang_StringLjava_lang_Class() {
        // Test for method java.io.ObjectStreamField(java.lang.String,
        // java.lang.Class)
        assertTrue("Used to test", true);
    }

    /**
     * @tests java.io.ObjectStreamField#compareTo(java.lang.Object)
     */
    @TestInfo(
            level = TestLevel.COMPLETE,
            purpose = "",
            targets = { @TestTarget(methodName = "compareTo", 
                                    methodArgs = {java.lang.Object.class})                                    
            }
        )      
    public void test_compareToLjava_lang_Object() {
        // Test for method int
        // java.io.ObjectStreamField.compareTo(java.lang.Object)
        assertTrue("Object compared to int did not return > 0", holaField
                .compareTo(hamField) > 0);
        assertEquals("Int compared to itself did not return 0", 0, hamField
                .compareTo(hamField));
        assertTrue("(Int)ham compared to (Int)sam did not return < 0", hamField
                .compareTo(samField) < 0);
    }

    /**
     * @tests java.io.ObjectStreamField#getName()
     */
    @TestInfo(
            level = TestLevel.COMPLETE,
            purpose = "",
            targets = { @TestTarget(methodName = "getName", 
                                    methodArgs = {})                                    
            }
        )    
    public void test_getName() {
        // Test for method java.lang.String java.io.ObjectStreamField.getName()
        assertEquals("Field did not return correct name", "hola", holaField.getName());
    }

    /**
     * @tests java.io.ObjectStreamField#getOffset()
     */
    @TestInfo(
            level = TestLevel.COMPLETE,
            purpose = "",
            targets = { @TestTarget(methodName = "getOffset", 
                                    methodArgs = {})                                    
            }
        )    
    public void test_getOffset() {
        // Test for method int java.io.ObjectStreamField.getOffset()
        ObjectStreamField[] osfArray;
        osfArray = osc.getFields();
        assertTrue("getOffset did not return reasonable values", osfArray[0]
                .getOffset() != osfArray[1].getOffset());
        assertEquals("getOffset for osfArray[0].getOffset() did not return 0",
                0, osfArray[0].getOffset());
        assertEquals("osfArray[1].getOffset() did not return    8", 8, osfArray[1]
                .getOffset());
        assertEquals("osfArray[2].getOffset() did not return 12", 12, osfArray[2]
                .getOffset());
    }

    /**
     * @tests java.io.ObjectStreamField#getType()
     */
    @TestInfo(
            level = TestLevel.COMPLETE,
            purpose = "",
            targets = { @TestTarget(methodName = "getType", 
                                    methodArgs = {})                                    
            }
        )     
    public void test_getType() {
        // Test for method java.lang.Class java.io.ObjectStreamField.getType()
        assertTrue("getType on an Object field did not answer Object",
                holaField.getType().equals(Object.class));
    }

    /**
     * @tests java.io.ObjectStreamField#getTypeCode()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getTypeCode",
          methodArgs = {}
        )
    })
    public void test_getTypeCode() {
        // Test for method char java.io.ObjectStreamField.getTypeCode()
        assertEquals("getTypeCode on an Object field did not answer 'L'",
                'L', holaField.getTypeCode());
        assertEquals("getTypeCode on a long field did not answer 'J'", 'J', bamField
                .getTypeCode());
    }

    /**
     * @tests java.io.ObjectStreamField#getTypeString()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getTypeString",
          methodArgs = {}
        )
    })
    public void test_getTypeString() {
        assertTrue("getTypeString returned: " + holaField.getTypeString(),
                holaField.getTypeString().indexOf("Object") >= 0);
        assertNull("Primitive types' strings should be null", hamField.getTypeString());
        
        ObjectStreamField osf = new ObjectStreamField("s", String.class, true);
        assertTrue(osf.getTypeString() == "Ljava/lang/String;");
    }

    /**
     * @tests java.io.ObjectStreamField#toString()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "toString",
          methodArgs = {}
        )
    })
    public void test_toString() {
        // Test for method java.lang.String java.io.ObjectStreamField.toString()
        assertTrue("toString on a long returned: " + bamField.toString(),
                bamField.toString().indexOf("bam") >= 0);
    }
    
    /**
     * @tests java.io.ObjectStreamField#getType() 
     */
    @TestInfo(
            level = TestLevel.COMPLETE,
            purpose = "",
            targets = { @TestTarget(methodName = "getType", 
                                    methodArgs = {})                                    
            }
        )      
    public void test_getType_Deserialized() throws IOException,
            ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new SerializableObject());
        oos.close();
        baos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SerializableObject obj = (SerializableObject) ois.readObject();

        ObjectStreamClass oc = obj.getObjectStreamClass();
        ObjectStreamField field = oc.getField("i");
        assertEquals(Object.class, field.getType());
    }
    
    /**
     * @tests java.io.ObjectStreamField#getType()
     */
    @TestInfo(
          level = TestLevel.COMPLETE,
          purpose = "",
          targets = {
            @TestTarget(
              methodName = "getType",
              methodArgs = {}
            )
        })    
    public void test_getType_MockObjectInputStream() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new SerializableObject());
        oos.close();
        baos.close();

        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        MockObjectInputStream ois = new MockObjectInputStream(bais);
        ois.readObject();

        ObjectStreamClass oc = ois.getObjectStreamClass();
        ObjectStreamField field = oc.getField("i");
        assertEquals(Object.class, field.getType());
    }
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "isUnshared",
          methodArgs = {}
        )
    })
    public void test_isUnshared() throws Exception {
        SerializableObject2 obj = new SerializableObject2();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        baos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SerializableObject2 newObj = (SerializableObject2) ois.readObject();
        
        ObjectInputStream.GetField getField = newObj.getGetField();
        ObjectStreamClass objectStreamClass = getField.getObjectStreamClass();
        
        assertTrue(objectStreamClass.getField("i").isUnshared());
        assertFalse(objectStreamClass.getField("d").isUnshared());
        assertTrue(objectStreamClass.getField("s").isUnshared());
        
        assertEquals(1000, getField.get("i", null));
        assertEquals(SerializableObject2.today, getField.get("d", null));
        assertEquals("Richard", getField.get("s", null));
        
        assertTrue(objectStreamClass.getField("s").getTypeString() == "Ljava/lang/String;");
        
        assertEquals(0, objectStreamClass.getField("d").getOffset());
        assertEquals(1, objectStreamClass.getField("i").getOffset());
        assertEquals(2, objectStreamClass.getField("s").getOffset());
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
        osc = ObjectStreamClass.lookup(DummyClass.class);
        bamField = osc.getField("bam");
        samField = osc.getField("sam");
        hamField = osc.getField("ham");
        holaField = osc.getField("hola");
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
    }
}

class SerializableObject implements Serializable {
    public ObjectInputStream.GetField getField = null;

    private static final long serialVersionUID = -2953957835918368056L;

    public Date d;

    public Integer i;
    
    public Exception e;
    
    public SerializableObject() {
        d = new Date();
        i = new Integer(1);
        e = new Exception("e");
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.putFields().put("d", new Date());
        o.putFields().put("i", new Integer(11));
        o.writeFields();
    }

    private void readObject(ObjectInputStream in) throws NotActiveException,
            IOException, ClassNotFoundException {
        getField = in.readFields();
        d = (Date) getField.get("d", null);
        i = (Integer) getField.get("i", null);
    }

    public ObjectStreamClass getObjectStreamClass() {
        return getField.getObjectStreamClass();
    }
}

class MockObjectInputStream extends ObjectInputStream {
    private ObjectStreamClass temp = null;

    public MockObjectInputStream() throws SecurityException, IOException {
        super();
    }

    public MockObjectInputStream(InputStream in)
            throws StreamCorruptedException, IOException {
        super(in);
    }

    public ObjectStreamClass readClassDescriptor() throws IOException,
            ClassNotFoundException {
        ObjectStreamClass osc = super.readClassDescriptor();
        //To get the ObjectStreamClass of SerializableObject
        if (osc.getSerialVersionUID() == -2953957835918368056L) {
            temp = osc;
        }
        return osc;
    }

    public ObjectStreamClass getObjectStreamClass() {
        return temp;
    }
}

class SerializableObject2 implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("i", Integer.class, true),
        new ObjectStreamField("d", Date.class, false),
        new ObjectStreamField("s", String.class, true),
    };
    
    private ObjectInputStream.GetField getField;
    
    public static Date today = new Date(1172632429156l);
    
    public ObjectInputStream.GetField getGetField() {
        return getField;
    }
    
    private void writeObject(ObjectOutputStream o) throws IOException {
        ObjectOutputStream.PutField putField = o.putFields();
        putField.put("i", new Integer(1000));
        putField.put("d", today);
        putField.put("s", "Richard");
        o.writeFields();
    }

    private void readObject(ObjectInputStream in) throws NotActiveException,
            IOException, ClassNotFoundException {
        getField = in.readFields();
    }
}
