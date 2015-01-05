package com.android.launcher3.nameless.gestures;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.list.PinnedHeaderListAdapter;
import com.android.launcher3.nameless.actions.ActionProcessor;
import com.android.launcher3.settings.SettingsProvider;

public class GestureHeaderAdapter extends PinnedHeaderListAdapter {
    private Context mContext;

    public GestureHeaderAdapter(Context context) {
        super(context);
        mContext = context;
    }

    private AlertDialog mDialog;

    private String[] mHeaders;
    public int mPinnedHeaderCount;

    private final class ViewHolder {
        private final String type;
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

    public void setHeaders(String[] headers) {
        this.mHeaders = headers;
    }

    @Override
    protected View newHeaderView(Context context, int partition, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.settings_pane_list_header, parent, false);
    }

    @Override
    protected void bindHeaderView(View view, int partition, Cursor cursor) {
        final TextView textView = (TextView) view.findViewById(R.id.item_name);
        textView.setText(mHeaders[partition]);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }

    @Override
    protected View newView(Context context, int partition, Cursor cursor, int position,
            ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.settings_pane_list_item, parent, false);
    }

    @Override
    protected void bindView(View v, int partition, Cursor cursor, int position) {
        final int titleId = cursor.getInt(1);
        final String type = ActionProcessor.getType(titleId);
        final ViewHolder viewHolder = new ViewHolder(type, v);
        final Resources res = mContext.getResources();

        final Configuration config = res.getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            viewHolder.title.setGravity(Gravity.RIGHT);
        }

        viewHolder.title.setText(titleId);
        viewHolder.state.setText(ActionProcessor.getGestureById(
                SettingsProvider.getIntCustomDefault(mContext, type, 0)));

        v.setTag(viewHolder);
        v.setOnClickListener(mSettingsItemListener);
    }

    @Override
    public View getPinnedHeaderView(int viewIndex, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.settings_pane_list_header, parent, false);
        view.setFocusable(false);
        view.setEnabled(false);
        bindHeaderView(view, viewIndex, null);
        return view;
    }

    @Override
    public int getPinnedHeaderCount() {
        return mPinnedHeaderCount;
    }

    public void onPause() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private final OnClickListener mSettingsItemListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final ViewHolder viewHolder = (ViewHolder) v.getTag();
            final String type = viewHolder.type;

            final int selected = SettingsProvider.getIntCustomDefault(getContext(), type,
                    ActionProcessor.ACTION_NOTHING);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.gesture_choose);
            builder.setSingleChoiceItems(R.array.gesture_entries, selected,
                    new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                            SettingsProvider.putInt(getContext(), type, i);
                            GestureHelper.get(getContext()).updateActions(getContext());
                            viewHolder.state.setText(ActionProcessor.getGestureById(i));
                            dialogInterface.dismiss();
                        }
                    });

            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
            mDialog = builder.create();
            mDialog.show();
        }
    };

}
