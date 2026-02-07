package com.wealthwise.app.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wealthwise.app.R;

public class EmptyStateView extends LinearLayout {

    private TextView tvMessage;

    public EmptyStateView(Context context) {
        super(context);
        init(context);
    }

    public EmptyStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(android.view.Gravity.CENTER);

        tvMessage = new TextView(context);
        tvMessage.setText(R.string.empty_state_message);
        tvMessage.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        tvMessage.setTextSize(16f);
        addView(tvMessage);
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }

    public void setMessage(int resId) {
        tvMessage.setText(resId);
    }
}
