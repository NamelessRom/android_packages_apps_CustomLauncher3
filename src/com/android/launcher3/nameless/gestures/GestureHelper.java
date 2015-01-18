/*
 * <!--
 *    Copyright (C) 2015 The NamelessROM Project
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
package com.android.launcher3.nameless.gestures;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.android.launcher3.nameless.actions.ActionProcessor;
import com.android.launcher3.nameless.actions.BaseActionListener;
import com.android.launcher3.settings.SettingsProvider;

public class GestureHelper {
    private static final String TAG = "GestureHelper";
    private static final boolean DEBUG = false;
    private static GestureHelper sInstance;

    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public enum Gesture {
        DOWN_LEFT,
        DOWN_MIDDLE,
        DOWN_RIGHT,
        UP_LEFT,
        UP_MIDDLE,
        UP_RIGHT,
        NONE
    }

    private static int sSector;
    private static int sTypeDoubleTap;
    private static int sTypeSwipeDownLeft;
    private static int sTypeSwipeDownMiddle;
    private static int sTypeSwipeDownRight;
    private static int sTypeSwipeUpLeft;
    private static int sTypeSwipeUpMiddle;
    private static int sTypeSwipeUpRight;

    private GestureHelper(final Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        sSector = width / 3;

        updateActions(context);
    }

    public static GestureHelper get(final Context context) {
        if (sInstance == null) {
            sInstance = new GestureHelper(context);
        }
        return sInstance;
    }

    public void updateActions(final Context context) {
        log("updateActions");
        sTypeSwipeDownLeft = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_SWIPE_DOWN_LEFT, ActionProcessor.ACTION_EXPAND_STATUSBAR);
        sTypeSwipeDownMiddle = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_SWIPE_DOWN_MIDDLE, ActionProcessor.ACTION_EXPAND_STATUSBAR);
        sTypeSwipeDownRight = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_SWIPE_DOWN_RIGHT, ActionProcessor.ACTION_EXPAND_STATUSBAR);

        sTypeSwipeUpLeft = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_SWIPE_UP_LEFT, ActionProcessor.ACTION_NOTHING);
        sTypeSwipeUpMiddle = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_SWIPE_UP_MIDDLE, ActionProcessor.ACTION_NOTHING);
        sTypeSwipeUpRight = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_SWIPE_UP_RIGHT, ActionProcessor.ACTION_NOTHING);

        sTypeDoubleTap = SettingsProvider.getIntCustomDefault(context,
                GestureFragment.TYPE_DOUBLE_TAP, ActionProcessor.ACTION_TURN_SCREEN_OFF);
    }

    public Gesture getGesture(float upX, float upY, float downX, float downY) {
        if (isSwipeDown(upY, downY)) {
            log("Swipe Down!");
            if (isSwipeLeft(downX)) {
                log("Swipe Down Left!");
                return Gesture.DOWN_LEFT;
            } else if (isSwipeRight(downX)) {
                log("Swipe Down Right!");
                return Gesture.DOWN_RIGHT;
            } else if (isSwipeMiddle(downX)) {
                log("Swipe Down Middle!");
                return Gesture.DOWN_MIDDLE;
            }
        } else if (isSwipeUp(upY, downY)) {
            log("Swipe Up!");
            if (isSwipeLeft(downX)) {
                log("Swipe Up Left!");
                return Gesture.UP_LEFT;
            } else if (isSwipeRight(downX)) {
                log("Swipe Up Right!");
                return Gesture.UP_RIGHT;
            } else if (isSwipeMiddle(downX)) {
                log("Swipe Up Middle!");
                return Gesture.UP_MIDDLE;
            }
        }

        return Gesture.NONE;
    }

    public boolean isSwipeDown(float upY, float downY) {
        return (upY - downY) > SWIPE_MAX_OFF_PATH;
    }

    public boolean isSwipeUp(float upY, float downY) {
        return ((upY - downY) < -SWIPE_MAX_OFF_PATH);
    }

    public boolean isSwipeLeft(float downX) {
        return downX < sSector;
    }

    public boolean isSwipeMiddle(float downX) {
        return (downX > sSector) && (downX < (sSector * 2));
    }

    public boolean isSwipeRight(float downX) {
        return downX > (sSector * 2);
    }

    private void log(final String msg) {
        if (DEBUG) Log.d(TAG, String.format("--> %s", msg));
    }

    public static class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final Context mContext;
        private final BaseActionListener mListener;

        public CustomGestureListener(final Context context, final BaseActionListener listener) {
            mContext = context;
            mListener = listener;
            GestureHelper.get(context).updateActions(context);
        }

        @Override
        public boolean onDown(final MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            ActionProcessor.processAction(mListener, sTypeDoubleTap);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }
            if (Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                return false;
            }

            final Gesture gesture = get(mContext).getGesture(e2.getRawX(), e2.getRawY(),
                    e1.getRawX(), e1.getRawY());
            switch (gesture) {
                case DOWN_LEFT: {
                    ActionProcessor.processAction(mListener, sTypeSwipeDownLeft);
                    break;
                }
                case DOWN_MIDDLE: {
                    ActionProcessor.processAction(mListener, sTypeSwipeDownMiddle);
                    break;
                }
                case DOWN_RIGHT: {
                    ActionProcessor.processAction(mListener, sTypeSwipeDownRight);
                    break;
                }
                case UP_LEFT: {
                    ActionProcessor.processAction(mListener, sTypeSwipeUpLeft);
                    break;
                }
                case UP_MIDDLE: {
                    ActionProcessor.processAction(mListener, sTypeSwipeUpMiddle);
                    break;
                }
                case UP_RIGHT: {
                    ActionProcessor.processAction(mListener, sTypeSwipeUpRight);
                    break;
                }
            }

            return true;
        }

    }

}
