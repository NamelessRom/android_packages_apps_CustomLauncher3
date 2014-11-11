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
import android.content.Intent;
import android.media.AudioManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.KeyEvent;

import com.android.internal.util.cm.TorchConstants;

public class ActionProcessor {

    public static final int ACTION_NOTHING            = 0;
    public static final int ACTION_TURN_SCREEN_OFF    = 1;
    public static final int ACTION_EXPAND_STATUSBAR   = 2;
    public static final int ACTION_TOGGLE_TORCH       = 3;
    public static final int ACTION_TOGGLE_SILENT_MODE = 4;
    public static final int ACTION_MUSIC_PLAY_PAUSE   = 5;
    public static final int ACTION_MUSIC_PREVIOUS     = 6;
    public static final int ACTION_MUSIC_NEXT         = 7;

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
            case ACTION_TOGGLE_TORCH:
                actionListener.toggleTorch();
                break;
            case ACTION_TOGGLE_SILENT_MODE:
                actionListener.toggleSilentMode();
                break;
            case ACTION_MUSIC_PLAY_PAUSE:
                actionListener.musicPlayPause();
                break;
            case ACTION_MUSIC_PREVIOUS:
                actionListener.musicPrevious();
                break;
            case ACTION_MUSIC_NEXT:
                actionListener.musicNext();
                break;
        }
    }

    public interface ActionListener {
        public void turnScreenOff();

        public void collapseStatusBar();

        public void toggleTorch();

        public void toggleSilentMode();

        public void musicPlayPause();

        public void musicPrevious();

        public void musicNext();
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

    public static void toggleTorch(final Context context) {
        context.sendBroadcast(new Intent(TorchConstants.ACTION_TOGGLE_STATE));
    }

    public static void musicPlayPause(final Context context) {
        sendMediaButtonEvent(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    public static void musicPrevious(final Context context) {
        sendMediaButtonEvent(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    public static void musicNext(final Context context) {
        sendMediaButtonEvent(context, KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    private static void sendMediaButtonEvent(final Context context, final int code) {
        final long time = SystemClock.uptimeMillis();

        final Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        final KeyEvent downEvent = new KeyEvent(time, time, KeyEvent.ACTION_DOWN, code, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);

        final Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        final KeyEvent upEvent = new KeyEvent(time, time, KeyEvent.ACTION_UP, code, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        context.sendOrderedBroadcast(upIntent, null);
    }

    public static void toggleSilentMode(final Context context) {
        final AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        final boolean hasVib = vib != null && vib.hasVibrator();
        if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            am.setRingerMode(hasVib
                    ? AudioManager.RINGER_MODE_VIBRATE
                    : AudioManager.RINGER_MODE_SILENT);
        } else {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

}