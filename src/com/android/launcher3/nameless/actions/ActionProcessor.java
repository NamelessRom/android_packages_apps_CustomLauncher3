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

import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;

import com.android.launcher3.LauncherApplication;
import com.android.launcher3.R;
import com.android.launcher3.nameless.Utils;
import com.android.launcher3.nameless.gestures.GestureFragment;
import com.android.launcher3.settings.SettingsProvider;

public class ActionProcessor {
    private static final String TAG = "ActionProcessor";

    public static final int ACTION_NOTHING = 0;
    public static final int ACTION_TURN_SCREEN_OFF = 1;
    public static final int ACTION_EXPAND_STATUSBAR = 2;
    public static final int ACTION_TOGGLE_TORCH = 3;
    public static final int ACTION_TOGGLE_SILENT_MODE = 4;
    public static final int ACTION_MUSIC_PLAY_PAUSE = 5;
    public static final int ACTION_MUSIC_PREVIOUS = 6;
    public static final int ACTION_MUSIC_NEXT = 7;
    public static final int Action_TOGGLE_LAST_APP = 8;

    public static void processAction(final BaseActionListener actionListener, final int type) {
        switch (type) {
            default:
            case ACTION_NOTHING:
                return;
            case ACTION_TURN_SCREEN_OFF:
                actionListener.turnScreenOff();
                break;
            case ACTION_EXPAND_STATUSBAR:
                actionListener.expandStatusBar();
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
            case Action_TOGGLE_LAST_APP:
                actionListener.toggleLastApp();
                break;
        }
    }

    public static String getType(final int title) {
        switch (title) {
            // swipe down
            case R.string.gesture_swipe_down_left:
                return GestureFragment.TYPE_SWIPE_DOWN_LEFT;
            case R.string.gesture_swipe_down_middle:
                return GestureFragment.TYPE_SWIPE_DOWN_MIDDLE;
            case R.string.gesture_swipe_down_right:
                return GestureFragment.TYPE_SWIPE_DOWN_RIGHT;
            // swipe up
            case R.string.gesture_swipe_up_left:
                return GestureFragment.TYPE_SWIPE_UP_LEFT;
            case R.string.gesture_swipe_up_middle:
                return GestureFragment.TYPE_SWIPE_UP_MIDDLE;
            case R.string.gesture_swipe_up_right:
                return GestureFragment.TYPE_SWIPE_UP_RIGHT;
            // special
            case R.string.gesture_double_tap:
                return GestureFragment.TYPE_DOUBLE_TAP;
            // nothing
            default:
                return LauncherApplication.getStr(R.string.gesture_nothing);
        }
    }

    public static String getGestureById(final int gestureId) {
        switch (gestureId) {
            default:
            case ActionProcessor.ACTION_NOTHING:
                return LauncherApplication.getStr(R.string.gesture_nothing);
            case ActionProcessor.ACTION_TURN_SCREEN_OFF:
                return LauncherApplication.getStr(R.string.gesture_turn_screen_off);
            case ActionProcessor.Action_TOGGLE_LAST_APP:
                return LauncherApplication.getStr(R.string.gesture_toggle_last_app);
            case ActionProcessor.ACTION_EXPAND_STATUSBAR:
                return LauncherApplication.getStr(R.string.gesture_expand_status_bar);
            case ActionProcessor.ACTION_TOGGLE_TORCH:
                return LauncherApplication.getStr(R.string.gesture_toggle_torch);
            case ActionProcessor.ACTION_TOGGLE_SILENT_MODE:
                return LauncherApplication.getStr(R.string.gesture_toggle_silent_mode);
            case ActionProcessor.ACTION_MUSIC_PLAY_PAUSE:
                return LauncherApplication.getStr(R.string.gesture_music_play_pause);
            case ActionProcessor.ACTION_MUSIC_PREVIOUS:
                return LauncherApplication.getStr(R.string.gesture_music_previous);
            case ActionProcessor.ACTION_MUSIC_NEXT:
                return LauncherApplication.getStr(R.string.gesture_music_next);
        }
    }

    public static int getValueById(final Context context, final int type) {
        final int value;
        switch (type) {
            // swipe down
            case R.string.gesture_swipe_down_left:
                value = ActionProcessor.ACTION_EXPAND_STATUSBAR;
                break;
            case R.string.gesture_swipe_down_middle:
                value = ActionProcessor.ACTION_EXPAND_STATUSBAR;
                break;
            case R.string.gesture_swipe_down_right:
                value = ActionProcessor.ACTION_EXPAND_STATUSBAR;
                break;
            // swipe up
            case R.string.gesture_swipe_up_left:
                value = ActionProcessor.ACTION_NOTHING;
                break;
            case R.string.gesture_swipe_up_middle:
                value = ActionProcessor.ACTION_NOTHING;
                break;
            case R.string.gesture_swipe_up_right:
                value = ActionProcessor.ACTION_NOTHING;
                break;
            // special
            case R.string.gesture_double_tap:
                value = ActionProcessor.ACTION_TURN_SCREEN_OFF;
                break;
            // nothing
            default:
                value = ActionProcessor.ACTION_NOTHING;
                break;
        }

        return SettingsProvider.getIntCustomDefault(context, getType(type), value);
    }

    public static void turnScreenOff(final Context context) {
        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.goToSleep(SystemClock.uptimeMillis());
    }

    public static void expandStatusBar(final Context context) {
        final StatusBarManager sb = (StatusBarManager) context.getSystemService(
                Context.STATUS_BAR_SERVICE);
        sb.expandNotificationsPanel();
    }

    public static void toggleTorch(final Context context) {
        final Intent torchIntent = new Intent(Intent.ACTION_TOGGLE_FLASHLIGHT);
        torchIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(torchIntent);
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

    public static void toggleLastApp(final Context context) {
        try {
            Utils.switchToLastApp(context);
        } catch (RemoteException exc) {
            Log.e(TAG, "could not switch to last app", exc);
        }
    }

}
