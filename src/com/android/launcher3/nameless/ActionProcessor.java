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
package com.android.launcher3.nameless;

import android.app.StatusBarManager;
import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;

public class ActionProcessor {

    public static final int ACTION_NOTHING          = 0;
    public static final int ACTION_TURN_SCREEN_OFF  = 1;
    public static final int ACTION_EXPAND_STATUSBAR = 2;

    public static void processAction(final ActionListener actionListener, final int type) {
        switch (type) {
            default:
            case ACTION_NOTHING:
                return;
            case ACTION_TURN_SCREEN_OFF:
                actionListener.turnScreenOff();
                break;
            case ACTION_EXPAND_STATUSBAR:
                actionListener.collapseStatusBar();
                break;
        }
    }

    public interface ActionListener {
        public void turnScreenOff();

        public void collapseStatusBar();
    }

    public static void turnScreenOff(final Context context) {
        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.goToSleep(SystemClock.uptimeMillis());
    }

    public static void collapseStatusBar(final Context context) {
        final StatusBarManager sb = (StatusBarManager) context
                .getSystemService(Context.STATUS_BAR_SERVICE);
        sb.expandNotificationsPanel();
    }

}
