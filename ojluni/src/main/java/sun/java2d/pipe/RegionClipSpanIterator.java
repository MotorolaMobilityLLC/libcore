/*
 * Copyright (c) 1999, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d.pipe;

import java.awt.geom.PathIterator;
import java.awt.Rectangle;

/**
 * This class clips a SpanIterator to a Region and outputs the
 * resulting spans as another SpanIterator.
 *
 * Spans are output in the usual y/x order, unless the input span
 * iterator doesn't conform to this order, or the iterator's span
 * straddle more than one band of the Region used for clipping.
 *
 * Principle of operation:
 *
 * The iterator maintains a several cursors onto the RegionIterator
 * in order to avoid having to buffer spans from the SpanIterator.
 * They are:
 *  resetState    The initial state of the RegionIterator
 *  lwm             Low Water Mark, a running start point for
 *                  processing each band. Usually goes down, but
 *                  can be reset to resetState if a span has a lower
 *                  start coordinate than the previous one.
 *  row             The start of the current band of the RegionIterator
 *  box             The current span of the current row
 *
 * The main nextSpan() loop implements a coroutine like structure, with
 * three producers to get the next span, row and box calling each other
 * to iterate through the span iterator and region.
 *
 * REMIND: Needs a native implementation!
 */
public class RegionClipSpanIterator implements SpanIterator {

    // The inputs to the filter
    Region rgn;
    SpanIterator spanIter;

    // The cursors that track the progress through the region
    RegionIterator resetState;
    RegionIterator lwm;
    RegionIterator row;
    RegionIterator box;

    // The bounds of the current span iterator span
    int spanlox, spanhix, spanloy, spanhiy;

    // The extent of the region band marking the low water mark
    int lwmloy, lwmhiy;

    // The bounds of the current region box
    int rgnlox, rgnloy, rgnhix, rgnhiy;

    // The bounding box of the input Region. Used for click
    // rejection of iterator spans
    int rgnbndslox, rgnbndsloy, rgnbndshix, rgnbndshiy;

    // The array used to hold coordinates from the region iterator
    int rgnbox[] = new int[4];

    // The array used to hold coordinates from the span iterator
    int spanbox[] = new int[4];

    // True if the next iterator span should be read on the next
    // iteration of the main nextSpan() loop
    boolean doNextSpan;

    // True if the next region box should be read on the next
    // iteration of the main nextSpan() loop
    boolean doNextBox;

    // True if there are no more spans or the Region is empty
    boolean done = false;

    /*
     * Creates an instance that filters the spans generated by
     * spanIter through the region described by rgn.
     */
    public RegionClipSpanIterator(Region rgn, SpanIterator spanIter) {

        this.spanIter = spanIter;

        resetState = rgn.getIterator();
        lwm = resetState.createCopy();

        if (!lwm.nextYRange(rgnbox)) {
            done = true;
            return;
        }

        rgnloy = lwmloy = rgnbox[1];
        rgnhiy = lwmhiy = rgnbox[3];

        rgn.getBounds(rgnbox);
        rgnbndslox = rgnbox[0];
        rgnbndsloy = rgnbox[1];
        rgnbndshix = rgnbox[2];
        rgnbndshiy = rgnbox[3];
        if (rgnbndslox >= rgnbndshix ||
            rgnbndsloy >= rgnbndshiy) {
            done = true;
            return;
        }

        this.rgn = rgn;


        row = lwm.createCopy();
        box = row.createCopy();
        doNextSpan = true;
        doNextBox = false;
    }

    /*
     * Gets the bbox of the available path segments, clipped to the
     * Region.
     */
    public void getPathBox(int pathbox[]) {
        int[] rgnbox = new int[4];
        rgn.getBounds(rgnbox);
        spanIter.getPathBox(pathbox);

        if (pathbox[0] < rgnbox[0]) {
            pathbox[0] = rgnbox[0];
        }

        if (pathbox[1] < rgnbox[1]) {
            pathbox[1] = rgnbox[1];
        }

        if (pathbox[2] > rgnbox[2]) {
            pathbox[2] = rgnbox[2];
        }

        if (pathbox[3] > rgnbox[3]) {
            pathbox[3] = rgnbox[3];
        }
}

    /*
     * Intersects the path box with the given bbox.
     * Returned spans are clipped to this region, or discarded
     * altogether if they lie outside it.
     */
    public void intersectClipBox(int lox, int loy, int hix, int hiy) {
        spanIter.intersectClipBox(lox, loy, hix, hiy);
    }


    /*
     * Fetches the next span that needs to be operated on.
     * If the return value is false then there are no more spans.
     */
    public boolean nextSpan(int resultbox[]) {
        if (done) {
            return false;
        }

        int resultlox, resultloy, resulthix, resulthiy;
        boolean doNextRow = false;

        // REMIND: Cache the coordinate inst vars used in this loop
        // in locals vars.
        while (true) {
            // We've exhausted the current span so get the next one
            if (doNextSpan) {
                if (!spanIter.nextSpan(spanbox)) {
                    done = true;
                    return false;
                } else {
                    spanlox = spanbox[0];
                    // Clip out spans that lie outside of the rgn's bounds
                    if (spanlox >= rgnbndshix) {
                        continue;
                    }

                    spanloy = spanbox[1];
                    if (spanloy >= rgnbndshiy) {
                        continue;
                    }

                    spanhix = spanbox[2];
                    if (spanhix <= rgnbndslox) {
                        continue;
                    }

                    spanhiy = spanbox[3];
                    if (spanhiy <= rgnbndsloy) {
                        continue;
                    }
                }
                // If the span starts higher up than the low-water mark,
                // reset the lwm. This can only happen if spans aren't
                // returned in strict y/x order, or the first time through.
                if (lwmloy > spanloy) {
                    lwm.copyStateFrom(resetState);
                    lwm.nextYRange(rgnbox);
                    lwmloy = rgnbox[1];
                    lwmhiy = rgnbox[3];
                }
                // Skip to the first rgn row whose bottom edge is
                // below the top of the current span. This will only
                // execute >0 times when the current span starts in a
                // lower region row than the previous one, or possibly the
                // first time through.
                while (lwmhiy <= spanloy) {
                    if (!lwm.nextYRange(rgnbox))
                        break;
                    lwmloy = rgnbox[1];
                    lwmhiy = rgnbox[3];
                }
                // If the row overlaps the span, process it, otherwise
                // fetch another span
                if (lwmhiy > spanloy && lwmloy < spanhiy) {
                    // Update the current row if it's different from the
                    // new lwm
                    if (rgnloy != lwmloy) {
                        row.copyStateFrom(lwm);
                        rgnloy = lwmloy;
                        rgnhiy = lwmhiy;
                    }
                    box.copyStateFrom(row);
                    doNextBox = true;
                    doNextSpan = false;
                }
                continue;
            }

            // The current row's spans are exhausted, do the next one
            if (doNextRow) {
                // Next time we either do the next span or the next box
                doNextRow = false;
                // Get the next row
                boolean ok = row.nextYRange(rgnbox);
                // If there was one, update the bounds
                if (ok) {
                    rgnloy = rgnbox[1];
                    rgnhiy = rgnbox[3];
                }
                if (!ok || rgnloy >= spanhiy) {
                    // If we've exhausted the rows or this one is below the span,
                    // go onto the next span
                    doNextSpan = true;
                }
                else {
                    // Otherwise get the first box on this row
                    box.copyStateFrom(row);
                    doNextBox = true;
                }
                continue;
            }

            // Process the next box in the current row
            if (doNextBox) {
                boolean ok = box.nextXBand(rgnbox);
                if (ok) {
                    rgnlox = rgnbox[0];
                    rgnhix = rgnbox[2];
                }
                if (!ok || rgnlox >= spanhix) {
                    // If there was no next rgn span or it's beyond the
                    // source span, go onto the next row or span
                    doNextBox = false;
                    if (rgnhiy >= spanhiy) {
                        // If the current row totally overlaps the span,
                        // go onto the next span
                        doNextSpan = true;
                    } else {
                        // otherwise go onto the next rgn row
                        doNextRow = true;
                    }
                } else {
                    // Otherwise, if the new rgn span overlaps the
                    // spanbox, no need to get another box
                    doNextBox = rgnhix <= spanlox;
                }
                continue;
            }

            // Prepare to do the next box either on this call or
            // or the subsequent one
            doNextBox = true;

            // Clip the current span against the current box
            if (spanlox > rgnlox) {
                resultlox = spanlox;
            }
            else {
                resultlox = rgnlox;
            }

            if (spanloy > rgnloy) {
                resultloy = spanloy;
            }
            else {
                resultloy = rgnloy;
            }

            if (spanhix < rgnhix) {
                resulthix = spanhix;
            }
            else {
                resulthix = rgnhix;
            }

            if (spanhiy < rgnhiy) {
                resulthiy = spanhiy;
            }
            else {
                resulthiy = rgnhiy;
            }

            // If the result is empty, try then next box
            // otherwise return the box.
            // REMIND: I think by definition it's non-empty
            // if we're here. Need to think about this some more.
            if (resultlox >= resulthix ||
                resultloy >= resulthiy) {
                    continue;
            }
            else {
                    break;
            }
        }

        resultbox[0] = resultlox;
        resultbox[1] = resultloy;
        resultbox[2] = resulthix;
        resultbox[3] = resulthiy;
        return true;

    }


    /**
     * This method tells the iterator that it may skip all spans
     * whose Y range is completely above the indicated Y coordinate.
     */
    public void skipDownTo(int y) {
        spanIter.skipDownTo(y);
    }

    /**
     * This method returns a native pointer to a function block that
     * can be used by a native method to perform the same iteration
     * cycle that the above methods provide while avoiding upcalls to
     * the Java object.
     * The definition of the structure whose pointer is returned by
     * this method is defined in:
     * <pre>
     *     src/share/native/sun/java2d/pipe/SpanIterator.h
     * </pre>
     */
    public long getNativeIterator() {
        return 0;
    }

    /*
     * Cleans out all internal data structures.
     */
    //public native void dispose();

    protected void finalize() {
        //dispose();
    }

}
