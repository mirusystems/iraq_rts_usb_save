package com.mirusystems.usbsave;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;



public class BaseDialog extends DialogFragment {
    private static final String TAG = "BaseDialog";

    public static final String DIALOG_TITLE_ID = "dialog_title_id";
    public static final String DIALOG_MESSAGE_ID = "dialog_message_id";
    public static final String DIALOG_POSITIVE_ID = "dialog_positive_id";
    public static final String DIALOG_NEGATIVE_ID = "dialog_negative_id";

    public static final String DIALOG_TITLE = "dialog_title";
    public static final String DIALOG_MESSAGE = "dialog_message";
    public static final String DIALOG_POSITIVE = "dialog_positive";
    public static final String DIALOG_NEGATIVE = "dialog_negative";
    public static final String DIALOG_CANCELABLE = "dialog_cancelable";
    public static final String DIALOG_CUSTOM = "dialog_custom";

    private int titleId = -1;
    private int messageId = -1;
    private int positiveId = -1;
    private int negativeId = -1;

    private String title = null;
    private String message = null;
    private String positive = null;
    private String negative = null;
    private boolean cancelable = true;
    private boolean custom = false;
    private OnDialogClickListener clickListener;

    private static BaseLoadingDialog baseLoadingDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(DIALOG_TITLE_ID)) {
                titleId = bundle.getInt(DIALOG_TITLE_ID);
            }
            if (bundle.containsKey(DIALOG_MESSAGE_ID)) {
                messageId = bundle.getInt(DIALOG_MESSAGE_ID);
            }
            if (bundle.containsKey(DIALOG_POSITIVE_ID)) {
                positiveId = bundle.getInt(DIALOG_POSITIVE_ID);
            }
            if (bundle.containsKey(DIALOG_NEGATIVE_ID)) {
                negativeId = bundle.getInt(DIALOG_NEGATIVE);
            }
            if (bundle.containsKey(DIALOG_TITLE)) {
                title = bundle.getString(DIALOG_TITLE);
            }
            if (bundle.containsKey(DIALOG_MESSAGE)) {
                message = bundle.getString(DIALOG_MESSAGE);
            }
            if (bundle.containsKey(DIALOG_POSITIVE)) {
                positive = bundle.getString(DIALOG_POSITIVE);
            }
            if (bundle.containsKey(DIALOG_NEGATIVE)) {
                negative = bundle.getString(DIALOG_NEGATIVE);
            }
            if (bundle.containsKey(DIALOG_CANCELABLE)) {
                cancelable = bundle.getBoolean(DIALOG_CANCELABLE);
            }
            if (bundle.containsKey(DIALOG_CUSTOM)) {
                custom = bundle.getBoolean(DIALOG_CUSTOM);
            }
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (titleId != -1) {
            builder.setTitle(titleId);
        } else if (title != null) {
            builder.setTitle(title);
        }
        if (custom) {
            View messageLayout = getLayoutInflater().inflate(R.layout.dialog_custom, null);
            TextView messageText = messageLayout.findViewById(R.id.messageText);
            if (messageId != -1) {
                messageText.setText(messageId);
            } else if (message != null) {
                messageText.setText(message);
            }
            builder.setView(messageLayout);
        } else {
            if (messageId != -1) {
                builder.setMessage(messageId);
            } else if (message != null) {
                builder.setMessage(message);
            }
        }
        if (positiveId != -1) {
            builder.setPositiveButton(positiveId, (dialog, id) -> {
                onPositiveButtonClicked();
                dialog.dismiss();
            });
        } else if (positive != null) {
            builder.setPositiveButton(positive, (dialog, id) -> {
                onPositiveButtonClicked();
                dialog.dismiss();
            });
        }
        if (negativeId != -1) {
            builder.setNegativeButton(negativeId, (dialog, id) -> {
                onNegativeButtonClicked();
                dialog.dismiss();
            });
        } else if (negative != null) {
            builder.setNegativeButton(negative, (dialog, id) -> {
                onNegativeButtonClicked();
                dialog.dismiss();
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(cancelable);
        return dialog;
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        clickListener = listener;
    }

    private void onPositiveButtonClicked() {
        if (clickListener != null) {
            clickListener.onPositiveButtonClicked();
        }
    }

    private void onNegativeButtonClicked() {
        if (clickListener != null) {
            clickListener.onNegativeButtonClicked();
        }
    }

    private static BaseDialog dialog;

    public static void showDialog(AppCompatActivity activity) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new BaseDialog();

        Bundle args = new Bundle();
        args.putString(BaseDialog.DIALOG_TITLE, "My Custom Title");
        args.putString(BaseDialog.DIALOG_MESSAGE, "My Custom Message");
        args.putString(BaseDialog.DIALOG_POSITIVE, "YES");
        args.putString(BaseDialog.DIALOG_NEGATIVE, "no");
        args.putBoolean(BaseDialog.DIALOG_CANCELABLE, false);
        dialog.setArguments(args);
        dialog.setDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                Log.v(TAG, "onPositiveButtonClicked: ");
            }

            @Override
            public void onNegativeButtonClicked() {
                Log.v(TAG, "onNegativeButtonClicked: ");
            }
        });

        dialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(FragmentActivity activity, int titleId, int messageId, int positiveId, int negativeId, OnDialogClickListener clickListener) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new BaseDialog();

        Bundle args = new Bundle();
        args.putInt(BaseDialog.DIALOG_TITLE_ID, titleId);
        args.putInt(BaseDialog.DIALOG_MESSAGE_ID, messageId);
        args.putInt(BaseDialog.DIALOG_POSITIVE_ID, positiveId);
        args.putInt(BaseDialog.DIALOG_NEGATIVE_ID, negativeId);
        args.putBoolean(BaseDialog.DIALOG_CANCELABLE, false);
        dialog.setArguments(args);
        dialog.setDialogClickListener(clickListener);

        dialog.show(dialog.getActivity().getSupportFragmentManager(), "dialog");
    }

    public static void showDialog(FragmentActivity activity, String title, String message, String positive, String negative, OnDialogClickListener clickListener) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new BaseDialog();

        Bundle args = new Bundle();
        args.putString(BaseDialog.DIALOG_TITLE, title);
        args.putString(BaseDialog.DIALOG_MESSAGE, message);
        args.putString(BaseDialog.DIALOG_POSITIVE, positive);
        args.putString(BaseDialog.DIALOG_NEGATIVE, negative);
        args.putBoolean(BaseDialog.DIALOG_CANCELABLE, false);
        dialog.setArguments(args);
        dialog.setDialogClickListener(clickListener);

        dialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    public static void showCustomDialog(FragmentActivity activity, String title, String message, String positive, String negative, OnDialogClickListener clickListener) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new BaseDialog();

        Bundle args = new Bundle();
        args.putString(BaseDialog.DIALOG_TITLE, title);
        args.putString(BaseDialog.DIALOG_MESSAGE, message);
        args.putString(BaseDialog.DIALOG_POSITIVE, positive);
        args.putString(BaseDialog.DIALOG_NEGATIVE, negative);
        args.putBoolean(BaseDialog.DIALOG_CANCELABLE, false);
        args.putBoolean(BaseDialog.DIALOG_CUSTOM, true);
        dialog.setArguments(args);
        dialog.setDialogClickListener(clickListener);

        dialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    public static void showLoadingDialog(FragmentActivity activity) {
        if (baseLoadingDialog != null) {
            baseLoadingDialog.dismiss();
            baseLoadingDialog = null;
        }
        baseLoadingDialog = new BaseLoadingDialog(activity);
        baseLoadingDialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static void loaddismissDialog() {
        if (baseLoadingDialog != null) {
            baseLoadingDialog.dismiss();
            baseLoadingDialog = null;
        }
    }
}