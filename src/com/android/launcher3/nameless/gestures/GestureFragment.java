/*
 * <!--
 *    Copyright (C) 2014 - 2015 The NamelessRom Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -->
 */
package com.android.launcher3.nameless.gestures;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;

public class GestureFragment extends Fragment {
    public static final String TAG = "GESTURE_FRAGMENT";

    public static final String TYPE_SWIPE_DOWN_LEFT = "type_swipe_down_left";
    public static final String TYPE_SWIPE_DOWN_MIDDLE = "type_swipe_down_middle";
    public static final String TYPE_SWIPE_DOWN_RIGHT = "type_swipe_down_right";
    public static final String TYPE_SWIPE_UP_LEFT = "type_swipe_up_left";
    public static final String TYPE_SWIPE_UP_MIDDLE = "type_swipe_up_middle";
    public static final String TYPE_SWIPE_UP_RIGHT = "type_swipe_up_right";
    public static final String TYPE_DOUBLE_TAP = "type_double_tap";

    private static final int POS_SWIPE_DOWN = 0;
    private static final int POS_SWIPE_UP = 1;
    private static final int POS_SPECIAL = 2;

    private GestureHeaderAdapter mGestureHeaderAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.settings_gestures_screen, container, false);

        final ListView listView = (ListView) v.findViewById(R.id.settings_gestures_list);
        initializeAdapter(listView);

        final LinearLayout titleLayout = (LinearLayout) v.findViewById(R.id.gesture_title);
        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGestureDone();
            }
        });

        return v;
    }

    public void initializeAdapter(final ListView listView) {
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        final Resources res = getResources();

        final String[] headers = new String[]{
                res.getString(R.string.gesture_swipe_down),
                res.getString(R.string.gesture_swipe_up),
                res.getString(R.string.gesture_special) };

        final int[] swipeDown = new int[]{
                R.string.gesture_swipe_down_left,
                R.string.gesture_swipe_down_middle,
                R.string.gesture_swipe_down_right
        };

        final int[] swipeUp = new int[]{
                R.string.gesture_swipe_up_left,
                R.string.gesture_swipe_up_middle,
                R.string.gesture_swipe_up_right
        };

        final int[] special = new int[]{
                R.string.gesture_double_tap
        };


        mGestureHeaderAdapter = new GestureHeaderAdapter(getActivity());
        mGestureHeaderAdapter.setHeaders(headers);
        mGestureHeaderAdapter.addPartition(false, true);
        mGestureHeaderAdapter.addPartition(false, true);
        mGestureHeaderAdapter.addPartition(false, true);
        mGestureHeaderAdapter.mPinnedHeaderCount = headers.length;

        mGestureHeaderAdapter.changeCursor(POS_SWIPE_DOWN, createCursor(headers[0], swipeDown));
        mGestureHeaderAdapter.changeCursor(POS_SWIPE_UP, createCursor(headers[1], swipeUp));
        mGestureHeaderAdapter.changeCursor(POS_SPECIAL, createCursor(headers[2], special));
        listView.setAdapter(mGestureHeaderAdapter);
    }

    private Cursor createCursor(String header, int[] values) {
        MatrixCursor cursor = new MatrixCursor(new String[]{ "_id", header });
        int count = values.length;
        for (int i = 0; i < count; i++) {
            cursor.addRow(new Object[]{ i, values[i] });
        }
        return cursor;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (enter) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            final ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationX", width, 0);

            final View darkPanel = ((Launcher) getActivity()).getDarkPanel();
            darkPanel.setVisibility(View.VISIBLE);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(
                    darkPanel, "alpha", 0.0f, 0.3f);
            anim2.start();

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator arg0) {}

                @Override
                public void onAnimationRepeat(Animator arg0) {}

                @Override
                public void onAnimationEnd(Animator arg0) {
                    darkPanel.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator arg0) {}
            });

            return anim;
        }
        return super.onCreateAnimator(transit, false, nextAnim);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGestureHeaderAdapter != null) {
            mGestureHeaderAdapter.onPause();
        }
    }

    public void setGestureDone() {
        ((Launcher) getActivity()).setGestureDone();
    }

}
