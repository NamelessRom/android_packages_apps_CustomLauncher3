/*
 * <!--
 *    Copyright (C) 2014 The NamelessROM Project
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * -->
 */
package com.android.launcher3.nameless.actions;

import android.content.Context;

public abstract class BaseActionListener {
    public abstract Context getContext();

    public void turnScreenOff() {
        ActionProcessor.turnScreenOff(getContext());
    }

    public void expandStatusBar() {
        ActionProcessor.expandStatusBar(getContext());
    }

    public void toggleTorch() {
        ActionProcessor.toggleTorch(getContext());
    }

    public void toggleSilentMode() {
        ActionProcessor.toggleSilentMode(getContext());
    }

    public void musicPlayPause() {
        ActionProcessor.musicPlayPause(getContext());
    }

    public void musicPrevious() {
        ActionProcessor.musicPrevious(getContext());
    }

    public void musicNext() {
        ActionProcessor.musicNext(getContext());
    }

    public void toggleLastApp() {
        ActionProcessor.toggleLastApp(getContext());
    }
}
