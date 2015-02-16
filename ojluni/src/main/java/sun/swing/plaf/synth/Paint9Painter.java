/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
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
package sun.swing.plaf.synth;

import java.awt.*;
import java.awt.image.BufferedImage;
import sun.swing.CachedPainter;

/**
 * Paint9Painter is used for painting images for both Synth and GTK's
 * pixmap/blueprint engines.
 *
 */
public class Paint9Painter extends CachedPainter {
    /**
     * Enumeration for the types of painting this class can handle.
     */
    public enum PaintType {
        /**
         * Painting type indicating the image should be centered in
         * the space provided.  When used the <code>mask</code> is ignored.
         */
        CENTER,

        /**
         * Painting type indicating the image should be tiled across the
         * specified width and height.  When used the <code>mask</code> is
         * ignored.
         */
        TILE,

        /**
         * Painting type indicating the image should be split into nine
         * regions with the top, left, bottom and right areas stretched.
         */
        PAINT9_STRETCH,

        /**
         * Painting type indicating the image should be split into nine
         * regions with the top, left, bottom and right areas tiled.
         */
        PAINT9_TILE
    };

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    public static final int PAINT_TOP_LEFT = 1;
    public static final int PAINT_TOP = 2;
    public static final int PAINT_TOP_RIGHT = 4;
    public static final int PAINT_LEFT = 8;
    public static final int PAINT_CENTER = 16;
    public static final int PAINT_RIGHT = 32;
    public static final int PAINT_BOTTOM_RIGHT = 64;
    public static final int PAINT_BOTTOM = 128;
    public static final int PAINT_BOTTOM_LEFT = 256;
    /**
     * Specifies that all regions should be painted.  If this is set any
     * other regions specified will not be painted.  For example
     * PAINT_ALL | PAINT_CENTER will paint all but the center.
     */
    public static final int PAINT_ALL = 512;

    /**
     * Conveniance method for testing the validity of an image.
     *
     * @param image Image to check.
     * @return true if <code>image</code> is non-null and has a positive
     *         size.
     */
    public static boolean validImage(Image image) {
        return (image != null && image.getWidth(null) > 0 &&
                image.getHeight(null) > 0);
    }


    public Paint9Painter(int cacheCount) {
        super(cacheCount);
    }

    /**
     * Paints using the algorightm specified by <code>paintType</code>.
     * NOTE that this just invokes super.paint(...) with the same
     * argument ordering as this method.
     *
     * @param c Component rendering to
     * @param g Graphics to render to
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param w Width to render to
     * @param h Height to render to
     * @param source Image to render from, if <code>null</code> this method
     *               will do nothing
     * @param sInsets Insets specifying the portion of the image that
     *                will be stretched or tiled, if <code>null</code> empty
     *                <code>Insets</code> will be used.
     * @param dInsets Destination insets specifying the portion of the image
     *                will be stretched or tiled, if <code>null</code> empty
     *                <code>Insets</code> will be used.
     * @param paintType Specifies what type of algorithm to use in painting
     * @param mask Specifies portion of image to render, if
     *             <code>PAINT_ALL</code> is specified, any other regions
     *             specified will not be painted, for example
     *             PAINT_ALL | PAINT_CENTER paints everything but the center.
     */
    public void paint(Component c, Graphics g, int x,
                      int y, int w, int h, Image source, Insets sInsets,
                      Insets dInsets,
                      PaintType type, int mask) {
        if (source == null) {
            return;
        }
        super.paint(c, g, x, y, w, h, source, sInsets, dInsets, type, mask);
    }

    protected void paintToImage(Component c, Image destImage, Graphics g,
                                int w, int h, Object[] args) {
        int argIndex = 0;
        while (argIndex < args.length) {
            Image image = (Image)args[argIndex++];
            Insets sInsets = (Insets)args[argIndex++];
            Insets dInsets = (Insets)args[argIndex++];
            PaintType type = (PaintType)args[argIndex++];
            int mask = (Integer)args[argIndex++];
            paint9(g, 0, 0, w, h, image, sInsets, dInsets, type, mask);
        }
    }

    protected void paint9(Graphics g, int x, int y, int w, int h,
                          Image image, Insets sInsets,
                          Insets dInsets, PaintType type, int componentMask) {
        if (!validImage(image)) {
            return;
        }
        if (sInsets == null) {
            sInsets = EMPTY_INSETS;
        }
        if (dInsets == null) {
            dInsets = EMPTY_INSETS;
        }
        int iw = image.getWidth(null);
        int ih = image.getHeight(null);

        if (type == PaintType.CENTER) {
            // Center the image
            g.drawImage(image, x + (w - iw) / 2,
                        y + (h - ih) / 2, null);
        }
        else if (type == PaintType.TILE) {
            // Tile the image
            int lastIY = 0;
            for (int yCounter = y, maxY = y + h; yCounter < maxY;
                     yCounter += (ih - lastIY), lastIY = 0) {
                int lastIX = 0;
                for (int xCounter = x, maxX = x + w; xCounter < maxX;
                             xCounter += (iw - lastIX), lastIX = 0) {
                    int dx2 = Math.min(maxX, xCounter + iw - lastIX);
                    int dy2 = Math.min(maxY, yCounter + ih - lastIY);
                    g.drawImage(image, xCounter, yCounter, dx2, dy2,
                                lastIX, lastIY, lastIX + dx2 - xCounter,
                                lastIY + dy2 - yCounter, null);
                }
            }
        }
        else {
            int st = sInsets.top;
            int sl = sInsets.left;
            int sb = sInsets.bottom;
            int sr = sInsets.right;

            int dt = dInsets.top;
            int dl = dInsets.left;
            int db = dInsets.bottom;
            int dr = dInsets.right;

            // Constrain the insets to the size of the image
            if (st + sb > ih) {
                db = dt = sb = st = Math.max(0, ih / 2);
            }
            if (sl + sr > iw) {
                dl = dr = sl = sr = Math.max(0, iw / 2);
            }

            // Constrain the insets to the size of the region we're painting
            // in.
            if (dt + db > h) {
                dt = db = Math.max(0, h / 2 - 1);
            }
            if (dl + dr > w) {
                dl = dr = Math.max(0, w / 2 - 1);
            }

            boolean stretch = (type == PaintType.PAINT9_STRETCH);
            if ((componentMask & PAINT_ALL) != 0) {
                componentMask = (PAINT_ALL - 1) & ~componentMask;
            }

            if ((componentMask & PAINT_LEFT) != 0) {
                drawChunk(image, g, stretch, x, y + dt, x + dl, y + h - db,
                          0, st, sl, ih - sb, false);
            }
            if ((componentMask & PAINT_TOP_LEFT) != 0) {
                drawImage(image, g, x, y, x + dl, y + dt,
                          0, 0, sl, st);
            }
            if ((componentMask & PAINT_TOP) != 0) {
                drawChunk(image, g, stretch, x + dl, y, x + w - dr, y + dt,
                          sl, 0, iw - sr, st, true);
            }
            if ((componentMask & PAINT_TOP_RIGHT) != 0) {
                drawImage(image, g, x + w - dr, y, x + w, y + dt,
                            iw - sr, 0, iw, st);
            }
            if ((componentMask & PAINT_RIGHT) != 0) {
                drawChunk(image, g, stretch,
                          x + w - dr, y + dt, x + w, y + h - db,
                          iw - sr, st, iw, ih - sb, false);
            }
            if ((componentMask & PAINT_BOTTOM_RIGHT) != 0) {
                drawImage(image, g, x + w - dr, y + h - db, x + w, y + h,
                            iw - sr, ih - sb, iw, ih);
            }
            if ((componentMask & PAINT_BOTTOM) != 0) {
                drawChunk(image, g, stretch,
                          x + dl, y + h - db, x + w - dr, y + h,
                          sl, ih - sb, iw - sr, ih, true);
            }
            if ((componentMask & PAINT_BOTTOM_LEFT) != 0) {
                drawImage(image, g, x, y + h - db, x + dl, y + h,
                          0, ih - sb, sl, ih);
            }
            if ((componentMask & PAINT_CENTER) != 0) {
                drawImage(image, g, x + dl, y + dt, x + w - dr, y + h - db,
                          sl, st, iw - sr, ih - sb);
            }
        }
    }

    private void drawImage(Image image, Graphics g,
                           int dx1, int dy1, int dx2, int dy2, int sx1,
                           int sy1, int sx2, int sy2) {
        // PENDING: is this necessary, will G2D do it for me?
        if (dx2 - dx1 <= 0 || dy2 - dy1 <= 0 || sx2 - sx1 <= 0 ||
                              sy2 - sy1 <= 0) {
            // Bogus location, nothing to paint
            return;
        }
        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    /**
     * Draws a portion of an image, stretched or tiled.
     *
     * @param image Image to render.
     * @param g Graphics to render to
     * @param stretch Whether the image should be stretched or timed in the
     *                provided space.
     * @param dx1 X origin to draw to
     * @param dy1 Y origin to draw to
     * @param dx2 End x location to draw to
     * @param dy2 End y location to draw to
     * @param sx1 X origin to draw from
     * @param sy1 Y origin to draw from
     * @param sx2 Max x location to draw from
     * @param sy2 Max y location to draw from
     * @param xDirection Used if the image is not stretched. If true it
     *        indicates the image should be tiled along the x axis.
     */
    private void drawChunk(Image image, Graphics g, boolean stretch,
                           int dx1, int dy1, int dx2, int dy2, int sx1,
                           int sy1, int sx2, int sy2,
                           boolean xDirection) {
        if (dx2 - dx1 <= 0 || dy2 - dy1 <= 0 || sx2 - sx1 <= 0 ||
                              sy2 - sy1 <= 0) {
            // Bogus location, nothing to paint
            return;
        }
        if (stretch) {
            g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
        else {
            int xSize = sx2 - sx1;
            int ySize = sy2 - sy1;
            int deltaX;
            int deltaY;

            if (xDirection) {
                deltaX = xSize;
                deltaY = 0;
            }
            else {
                deltaX = 0;
                deltaY = ySize;
            }
            while (dx1 < dx2 && dy1 < dy2) {
                int newDX2 = Math.min(dx2, dx1 + xSize);
                int newDY2 = Math.min(dy2, dy1 + ySize);

                g.drawImage(image, dx1, dy1, newDX2, newDY2,
                            sx1, sy1, sx1 + newDX2 - dx1,
                            sy1 + newDY2 - dy1, null);
                dx1 += deltaX;
                dy1 += deltaY;
            }
        }
    }

    /**
     * Subclassed to always create a translucent image.
     */
    protected Image createImage(Component c, int w, int h,
                                GraphicsConfiguration config,
                                Object[] args) {
        if (config == null) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
        return config.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
    }
}
