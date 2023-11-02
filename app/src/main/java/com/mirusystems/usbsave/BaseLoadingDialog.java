package com.mirusystems.usbsave;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mirusystems.usbsave.R;

public class BaseLoadingDialog extends AlertDialog {

    public BaseLoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_loading);

        // dim 제거
        getWindow().clearFlags((WindowManager.LayoutParams.FLAG_DIM_BEHIND));
    }

}
