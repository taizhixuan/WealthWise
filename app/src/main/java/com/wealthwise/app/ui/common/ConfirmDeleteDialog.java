package com.wealthwise.app.ui.common;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wealthwise.app.R;

public final class ConfirmDeleteDialog {

    public interface OnConfirmListener {
        void onConfirm();
    }

    private ConfirmDeleteDialog() {}

    public static void show(Context context, OnConfirmListener listener) {
        show(context, context.getString(R.string.confirm_delete_message), listener);
    }

    public static void show(Context context, String message, OnConfirmListener listener) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete)
                .setMessage(message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (listener != null) {
                        listener.onConfirm();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
