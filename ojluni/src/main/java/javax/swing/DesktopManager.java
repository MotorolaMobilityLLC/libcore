/*
 * Copyright (c) 1997, 2002, Oracle and/or its affiliates. All rights reserved.
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

package javax.swing;

/** DesktopManager objects are owned by a JDesktopPane object. They are responsible
  * for implementing L&F specific behaviors for the JDesktopPane. JInternalFrame
  * implementations should delegate specific behaviors to the DesktopManager. For
  * instance, if a JInternalFrame was asked to iconify, it should try:
  * <PRE>
  *    getDesktopPane().getDesktopManager().iconifyFrame(frame);
  * </PRE>
  * This delegation allows each L&F to provide custom behaviors for desktop-specific
  * actions. (For example, how and where the internal frame's icon would appear.)
  * <p>This class provides a policy for the various JInternalFrame methods, it is not
  * meant to be called directly rather the various JInternalFrame methods will call
  * into the DesktopManager.</p>
  *
  * @see JDesktopPane
  * @see JInternalFrame
  * @see JInternalFrame.JDesktopIcon
  *
  * @author David Kloba
  */
public interface DesktopManager
{
    /** If possible, display this frame in an appropriate location.
      * Normally, this is not called, as the creator of the JInternalFrame
      * will add the frame to the appropriate parent.
      */
    void openFrame(JInternalFrame f);

    /** Generally, this call should remove the frame from it's parent. */
    void closeFrame(JInternalFrame f);

    /** Generally, the frame should be resized to match it's parents bounds. */
    void maximizeFrame(JInternalFrame f);
    /** Generally, this indicates that the frame should be restored to it's
      * size and position prior to a maximizeFrame() call.
      */
    void minimizeFrame(JInternalFrame f);
    /** Generally, remove this frame from it's parent and add an iconic representation. */
    void iconifyFrame(JInternalFrame f);
    /** Generally, remove any iconic representation that is present and restore the
      * frame to it's original size and location.
      */
    void deiconifyFrame(JInternalFrame f);

    /**
     * Generally, indicate that this frame has focus. This is usually called after
     * the JInternalFrame's IS_SELECTED_PROPERTY has been set to true.
     */
    void activateFrame(JInternalFrame f);

    /**
     * Generally, indicate that this frame has lost focus. This is usually called
     * after the JInternalFrame's IS_SELECTED_PROPERTY has been set to false.
     */
    void deactivateFrame(JInternalFrame f);

    /** This method is normally called when the user has indicated that
      * they will begin dragging a component around. This method should be called
      * prior to any dragFrame() calls to allow the DesktopManager to prepare any
      * necessary state. Normally <b>f</b> will be a JInternalFrame.
      */
    void beginDraggingFrame(JComponent f);

    /** The user has moved the frame. Calls to this method will be preceded by calls
      * to beginDraggingFrame().
      *  Normally <b>f</b> will be a JInternalFrame.
      */
    void dragFrame(JComponent f, int newX, int newY);
    /** This method signals the end of the dragging session. Any state maintained by
      * the DesktopManager can be removed here.  Normally <b>f</b> will be a JInternalFrame.
      */
    void endDraggingFrame(JComponent f);

    /** This methods is normally called when the user has indicated that
      * they will begin resizing the frame. This method should be called
      * prior to any resizeFrame() calls to allow the DesktopManager to prepare any
      * necessary state.  Normally <b>f</b> will be a JInternalFrame.
      */
    void beginResizingFrame(JComponent f, int direction);
    /** The user has resized the component. Calls to this method will be preceded by calls
      * to beginResizingFrame().
      *  Normally <b>f</b> will be a JInternalFrame.
      */
    void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight);
    /** This method signals the end of the resize session. Any state maintained by
      * the DesktopManager can be removed here.  Normally <b>f</b> will be a JInternalFrame.
      */
    void endResizingFrame(JComponent f);

    /** This is a primitive reshape method.*/
    void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight);
}
