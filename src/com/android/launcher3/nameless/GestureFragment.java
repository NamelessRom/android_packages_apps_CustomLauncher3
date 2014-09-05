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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.settings.SettingsProvider;

public class GestureFragment extends Fragment {

    public static final String TAG = "GESTURE_FRAGMENT";

    public static final String TYPE_DOUBLE_TAP = "type_double_tap";
    public static final String TYPE_LONG_PRESS = "type_long_press";
    public static final String TYPE_SWIPE_DOWN = "type_swipe_down";
    public static final String TYPE_SWIPE_UP   = "type_swipe_up";

    private static final Integer[] GESTURES = new Integer[]{
            R.string.gesture_double_tap,
            R.string.gesture_long_press,
            R.string.gesture_swipe_down,
            R.string.gesture_swipe_up
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.settings_gestures_screen, container, false);

        final ListView listView = (ListView) v.findViewById(R.id.settings_gestures_list);
        listView.setAdapter(new GesturesArrayAdapter(getActivity()));

        final LinearLayout titleLayout = (LinearLayout) v.findViewById(R.id.gesture_title);
        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGestureDone();
            }
        });

        return v;
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
        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    public void setGestureDone() {
        ((Launcher) getActivity()).setGestureDone();
    }

    private class GesturesArrayAdapter extends ArrayAdapter<Integer> {
        private final Context   mContext;
        private final Integer[] titles;

        public GesturesArrayAdapter(final Context context) {
            super(context, R.layout.settings_pane_list_item, GESTURES);

            mContext = context;
            titles = GESTURES;
        }

        private final class ViewHolder {
            private final String   type;
            private final TextView title;
            private final TextView state;

            public ViewHolder(final String type, final View view) {
                this.type = type;
                this.title = (TextView) view.findViewById(R.id.item_name);
                this.state = (TextView) view.findViewById(R.id.item_state);
            }

            public String getType() {
                return this.type;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final int titleId = titles[position];
            final String type = getType(titleId);

            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.settings_pane_list_item, parent, false);
                viewHolder = new ViewHolder(type, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.title.setText(getString(titleId));
            viewHolder.state.setText(getGestureById(
                    SettingsProvider.getIntCustomDefault(getActivity(), type, 0)));

            convertView.setOnClickListener(mSettingsItemListener);
            return convertView;
        }

        private final View.OnClickListener mSettingsItemListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ViewHolder viewHolder = ((ViewHolder) view.getTag());
                final String type = viewHolder.getType();
                final int selected = SettingsProvider.getIntCustomDefault(getContext(), type,
                        ActionProcessor.ACTION_NOTHING);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.gesture_choose);
                builder.setSingleChoiceItems(R.array.gesture_entries, selected,
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                SettingsProvider.putInt(getContext(), type, i);
                                viewHolder.state.setText(getGestureById(i));
                                dialogInterface.dismiss();
                            }
                        });

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        private String getType(final int title) {
            switch (title) {
                case R.string.gesture_double_tap:
                    return TYPE_DOUBLE_TAP;
                case R.string.gesture_long_press:
                    return TYPE_LONG_PRESS;
                case R.string.gesture_swipe_down:
                    return TYPE_SWIPE_DOWN;
                case R.string.gesture_swipe_up:
                    return TYPE_SWIPE_UP;
                default:
                    return getString(R.string.gesture_nothing);
            }
        }

        private String getGestureById(final int gestureId) {
            switch (gestureId) {
                default:
                case ActionProcessor.ACTION_NOTHING:
                    return getString(R.string.gesture_nothing);
                case ActionProcessor.ACTION_TURN_SCREEN_OFF:
                    return getString(R.string.gesture_turn_screen_off);
                case ActionProcessor.ACTION_EXPAND_STATUSBAR:
                    return getString(R.string.gesture_expand_statusbar);
            }
        }
    }

}
