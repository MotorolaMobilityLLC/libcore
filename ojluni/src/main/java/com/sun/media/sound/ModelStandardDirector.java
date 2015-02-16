/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.media.sound;

/**
 * A standard director who chooses performers
 * by there keyfrom,keyto,velfrom,velto properties.
 *
 * @author Karl Helgason
 */
public final class ModelStandardDirector implements ModelDirector {

    ModelPerformer[] performers;
    ModelDirectedPlayer player;
    boolean noteOnUsed = false;
    boolean noteOffUsed = false;

    public ModelStandardDirector(ModelPerformer[] performers,
            ModelDirectedPlayer player) {
        this.performers = performers;
        this.player = player;
        for (int i = 0; i < performers.length; i++) {
            ModelPerformer p = performers[i];
            if (p.isReleaseTriggered()) {
                noteOffUsed = true;
            } else {
                noteOnUsed = true;
            }
        }
    }

    public void close() {
    }

    public void noteOff(int noteNumber, int velocity) {
        if (!noteOffUsed)
            return;
        for (int i = 0; i < performers.length; i++) {
            ModelPerformer p = performers[i];
            if (p.getKeyFrom() <= noteNumber && p.getKeyTo() >= noteNumber) {
                if (p.getVelFrom() <= velocity && p.getVelTo() >= velocity) {
                    if (p.isReleaseTriggered()) {
                        player.play(i, null);
                    }
                }
            }
        }
    }

    public void noteOn(int noteNumber, int velocity) {
        if (!noteOnUsed)
            return;
        for (int i = 0; i < performers.length; i++) {
            ModelPerformer p = performers[i];
            if (p.getKeyFrom() <= noteNumber && p.getKeyTo() >= noteNumber) {
                if (p.getVelFrom() <= velocity && p.getVelTo() >= velocity) {
                    if (!p.isReleaseTriggered()) {
                        player.play(i, null);
                    }
                }
            }
        }
    }
}
