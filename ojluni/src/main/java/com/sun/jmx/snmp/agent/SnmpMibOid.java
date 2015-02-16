/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
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
 */


package com.sun.jmx.snmp.agent;



// java imports
//
import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;

// jmx imports
//
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;

/**
 * Represents a node in an SNMP MIB which is neither a group nor a variable.
 * This class defines a list of sub-nodes and the methods that allow to
 * manipulate the sub-nodes.
 * <P>
 * This class is used internally and by the class generated by
 * <CODE>mibgen</CODE>.
 * You should not need to use this class directly.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 */

public class SnmpMibOid extends SnmpMibNode implements Serializable {
    private static final long serialVersionUID = 5012254771107446812L;

    /**
     * Default constructor.
     */
    public SnmpMibOid() {
    }

    // PUBLIC METHODS
    //---------------

    /**
     * Generic handling of the <CODE>get</CODE> operation.
     *
     * <p> This method should be overridden in subclasses.
     * <p>
     *
     * @param req   The sub-request that must be handled by this node.
     *
     * @param depth The depth reached in the OID tree.
     *
     * @exception SnmpStatusException The default implementation (if not
     *            overridden) is to generate a SnmpStatusException.
     */
    public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        for (Enumeration e= req.getElements(); e.hasMoreElements();) {
            SnmpVarBind var= (SnmpVarBind) e.nextElement();
            SnmpStatusException x =
                new SnmpStatusException(SnmpStatusException.noSuchObject);
            req.registerGetException(var,x);
        }
    }

    /**
     * Generic handling of the <CODE>set</CODE> operation.
     *
     * <p> This method should be overridden in subclasses.
     * <p>
     *
     * @param req   The sub-request that must be handled by this node.
     *
     * @param depth The depth reached in the OID tree.
     *
     * @exception SnmpStatusException The default implementation (if not
     *            overridden) is to generate a SnmpStatusException.
     */
    public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        for (Enumeration e= req.getElements(); e.hasMoreElements();) {
            SnmpVarBind var= (SnmpVarBind) e.nextElement();
            SnmpStatusException x =
                new SnmpStatusException(SnmpStatusException.noAccess);
            req.registerSetException(var,x);
        }
    }

    /**
     * Generic handling of the <CODE>check</CODE> operation.
     *
     * <p> This method should be overridden in subclasses.
     * <p>
     *
     * @param req   The sub-request that must be handled by this node.
     *
     * @param depth The depth reached in the OID tree.
     *
     * @exception SnmpStatusException The default implementation (if not
     *            overriden) is to generate a SnmpStatusException.
     */
    public void check(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        for (Enumeration e= req.getElements(); e.hasMoreElements();) {
            SnmpVarBind var= (SnmpVarBind) e.nextElement();
            SnmpStatusException x =
                new SnmpStatusException(SnmpStatusException.noAccess);
            req.registerCheckException(var,x);
        }
    }



    // ---------------------------------------------------------------------
    //
    // Implements the method defined in SnmpMibNode.
    //
    // ---------------------------------------------------------------------
    //
    void findHandlingNode(SnmpVarBind varbind,
                          long[] oid, int depth,
                          SnmpRequestTree handlers)
        throws SnmpStatusException {


        final int length = oid.length;
        SnmpMibNode node = null;

        if (handlers == null)
            throw new SnmpStatusException(SnmpStatusException.snmpRspGenErr);

        if (depth > length) {
            // Nothing is left... the oid is not valid
            throw noSuchObjectException;

        } else if (depth == length) {
            // The oid is not complete...
            throw noSuchInstanceException;

        } else {
            // Some children variable or subobject is being querried
            // getChild() will raise an exception if no child is found.
            //
            final SnmpMibNode child= getChild(oid[depth]);

            // XXXX zzzz : what about null children?
            //             (variables for nested groups)
            // if child==null, then we're dealing with a variable or
            // a table: we register this node.
            // This behaviour should be overriden in subclasses,
            // in particular in group meta classes: the group
            // meta classes that hold tables should take care
            // of forwarding this call to all the tables involved.
            //
            if (child == null)
                handlers.add(this,depth,varbind);
            else
                child.findHandlingNode(varbind,oid,depth+1,handlers);
        }
    }

    // ---------------------------------------------------------------------
    //
    // Implements the method defined in SnmpMibNode.
    //
    // ---------------------------------------------------------------------
    //
    long[] findNextHandlingNode(SnmpVarBind varbind,
                                long[] oid, int pos, int depth,
                                SnmpRequestTree handlers,
                                AcmChecker checker)
        throws SnmpStatusException {


        final int length = oid.length;
        SnmpMibNode node = null;
        long[] result = null;
        if (handlers == null)
            // This should be considered as a genErr, but we do not want to
            // abort the whole request, so we're going to throw
            // a noSuchObject...
            //
            throw noSuchObjectException;

        final Object data = handlers.getUserData();
        final int pduVersion = handlers.getRequestPduVersion();

        if (pos >= length) {
            long[] newOid= new long[1];
            newOid[0]=  getNextVarId(-1,data,pduVersion);
            result = findNextHandlingNode(varbind,newOid,0,depth,handlers,
                                          checker);
            return result;
        }

        // search the element specified in the oid
        //
        long[] newOid= new long[1];
        long index= oid[pos];

        while (true) {

            try {
                final SnmpMibNode child = getChild(index);
                // SnmpOid result = null;
                if (child == null) {
                    // shouldn't happen
                    throw noSuchObjectException;
                    // validateVarId(index);
                    // handlers.add(this,varbind,depth);
                    // result = new SnmpOid(0);
                } else {
                    checker.add(depth, index);
                    try {
                        result = child.findNextHandlingNode(varbind,oid,pos+1,
                                                            depth+1,handlers,
                                                            checker);
                    } finally {
                        checker.remove(depth);
                    }
                }

                // Build up the leaf OID
                result[depth] = index;
                return result;

            } catch(SnmpStatusException e) {
                // If there is no such element go one level up ...
                //
                index= getNextVarId(index,data,pduVersion);

                // There is no need to carry the original oid ...
                newOid[0]=index;
                pos= 1;
                oid=newOid;
            }
        }
    }


    /**
     * Computes the root OID of the MIB.
     */
    public void getRootOid(Vector<Integer> result) {

        // If a node has several children, let assume that we are one step to
        // far in order to get the MIB root.
        //
        if (nbChildren != 1)
            return;

        result.addElement(varList[0]);

        // Now query our child.
        //
        children.firstElement().getRootOid(result);

    }

    /**
     * Registers a specific node in the tree.
     */
    public void registerNode(String oidString ,SnmpMibNode node)
        throws IllegalAccessException {
        SnmpOid oid= new SnmpOid(oidString);
        registerNode(oid.longValue(), 0, node);
    }

    // PROTECTED METHODS
    //------------------

    /**
     * Registers a specific node in the tree.
     */
    void registerNode(long[] oid, int cursor ,SnmpMibNode node)
        throws IllegalAccessException {

        if (cursor >= oid.length)
            throw new IllegalAccessException();

        // Check if the node is already defined
        //
        long var= oid[cursor];

        //System.out.println("entering registration for val="
        // + String.valueOf(var) + " position= " + cursor);

        int pos = retrieveIndex(var);
        if (pos  == nbChildren) {
            nbChildren++;
            varList= new int[nbChildren];
            varList[0]= (int) var;
            pos =0;
            if ( (cursor + 1) == oid.length) {
                // That 's the end of the trip.
                // Do not forward the registration

                //System.out.println("End of trip for val="
                //      + String.valueOf(var) + " position= " + cursor);
                children.insertElementAt(node,pos);
                return;
            }

            //System.out.println("Create node for val="
            //       + String.valueOf(var) + " position= " + cursor);
            SnmpMibOid child= new SnmpMibOid();
            children.insertElementAt(child, pos);
            child.registerNode(oid, cursor + 1, node);
            return;
        }
        if (pos == -1) {
            // The node is not yet registered
            //
            int[] tmp= new int[nbChildren + 1];
            tmp[nbChildren]= (int) var;
            System.arraycopy(varList, 0, tmp, 0, nbChildren);
            varList= tmp;
            nbChildren++;
            SnmpMibNode.sort(varList);
            int newPos = retrieveIndex(var);
            varList[newPos]= (int) var;
            if ( (cursor + 1) == oid.length) {
                // That 's the end of the trip.
                // Do not forward the registration

                //System.out.println("End of trip for val="
                //     + String.valueOf(var) + " position= " + cursor);
                children.insertElementAt(node, newPos);
                return;
            }
            SnmpMibOid child= new SnmpMibOid();
            // System.out.println("Create node for val=" +
            //     String.valueOf(var) + " position= " + cursor);
            children.insertElementAt(child, newPos);
            child.registerNode(oid, cursor + 1, node);
            return;
        }
        else {
            // The node is already registered
            //
            SnmpMibNode child= children.elementAt(pos);
            if ( (cursor + 1) == oid.length ) {
                //System.out.println("Node already registered val=" +
                //          String.valueOf(var) + " position= " + cursor);
                if (child == node) return;
                if (child != null && node != null) {
                    // Now we're going to patch the tree the following way:
                    //   if a subgroup has been registered before its father,
                    //   we're going to replace the father OID node with
                    //   the actual group-node and export the children from
                    //   the temporary OID node to the actual group node.
                    //

                    if (node instanceof SnmpMibGroup) {
                        // `node' is a group => replace `child' with `node'
                        // export the child's subtree to `node'.
                        //
                        ((SnmpMibOid)child).exportChildren((SnmpMibOid)node);
                        children.setElementAt(node,pos);
                        return;

                    } else if ((node instanceof SnmpMibOid) &&
                             (child instanceof SnmpMibGroup)) {
                        // `node' is a temporary node, and `child' is a
                        //  group => keep child and export the node's
                        //  subtree to `child'.
                        //
                        ((SnmpMibOid)node).exportChildren((SnmpMibOid)child);
                        return;
                    } else if (node instanceof SnmpMibOid) {
                        // `node' and `child' are both temporary OID nodes
                        // => replace `child' with `node' and export child's
                        // subtree to `node'.
                        //
                        ((SnmpMibOid)child).exportChildren((SnmpMibOid)node);
                        children.setElementAt(node,pos);
                        return;
                    }
                }
                children.setElementAt(node,pos);
                return;
            } else {
                if (child == null)
                    throw new IllegalAccessException();
                ((SnmpMibOid)child).registerNode(oid, cursor + 1, node);
            }
        }
    }

    /**
     * Export this node's children to a brother node that will replace
     * this node in the OID tree.
     * This method is a patch that fixes the problem of registering
     * a subnode before its father node.
     *
     **/
    void exportChildren(SnmpMibOid brother)
        throws IllegalAccessException {

        if (brother == null) return;
        final long[] oid = new long[1];
        for (int i=0; i<nbChildren; i++) {
            final SnmpMibNode child = children.elementAt(i);
            if (child == null) continue;
            oid[0] = varList[i];
            brother.registerNode(oid,0,child);
        }
    }

    // PRIVATE METHODS
    //----------------

    SnmpMibNode getChild(long id) throws SnmpStatusException {

        // first we need to retrieve the identifier in the list of children
        //
        final int pos= getInsertAt(id);
        if (pos >= nbChildren)
            throw noSuchObjectException;

        if (varList[pos] != (int) id)
            throw noSuchObjectException;

        // Access the node
        //
        SnmpMibNode child = null;
        try {
            child = children.elementAtNonSync(pos);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw noSuchObjectException;
        }
        if (child == null)
            throw noSuchInstanceException;
        return child;
    }

    private int retrieveIndex(long val) {

        int low= 0;
        int cursor= (int) val;
        if (varList == null || varList.length < 1)
            return nbChildren;

        int max= varList.length -1 ;
        int curr= low + (max-low)/2;
        int elmt= 0;
        while (low <= max) {
            elmt= varList[curr];
            if (cursor == elmt) {
                // We need to get the next index ...
                //
                return curr;
            }
            if (elmt < cursor) {
                low= curr +1;
            } else {
                max= curr -1;
            }
            curr= low + (max-low)/2;
        }
        return -1;
    }

    private int getInsertAt(long val) {

        int low= 0;
        final int index= (int) val;
        if (varList == null)
            return -1;
        int max= varList.length -1 ;
        int elmt=0;
        //final int[] v = varList;

        //if (index > a[max])
        //return max +1;


        int curr= low + (max-low)/2;
        while (low <= max) {

            elmt= varList[curr];

            // never know ...we might find something ...
            //
            if (index == elmt)
                return curr;

            if (elmt < index) {
                low= curr +1;
            } else {
                max= curr -1;
            }
            curr= low + (max-low)/2;
        }

        return curr;
    }

    // PRIVATE VARIABLES
    //------------------

    /**
     * Contains the list of sub nodes.
     */
    private NonSyncVector<SnmpMibNode> children = new NonSyncVector<SnmpMibNode>(1);

    /**
     * The number of sub nodes.
     */
    private int nbChildren= 0;


    // All the methods of the Vector class are synchronized.
    // Synchronization is a very expensive operation. In our case it is
    // not always required...
    //
    @SuppressWarnings("serial")  // We will never serialize this
    class NonSyncVector<E> extends Vector<E> {

        public NonSyncVector(int size) {
            super(size);
        }

        final void addNonSyncElement(E obj) {
            ensureCapacity(elementCount + 1);
            elementData[elementCount++] = obj;
        }

        @SuppressWarnings("unchecked")  // cast to E
        final E elementAtNonSync(int index) {
            return (E) elementData[index];
        }

    }
}
