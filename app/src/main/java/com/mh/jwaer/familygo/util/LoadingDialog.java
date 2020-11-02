package com.mh.jwaer.familygo.util;

import android.app.Activity;
import android.app.AlertDialog;

import com.mh.jwaer.familygo.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }

    public void startAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.custom_loading_dialog);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }
    public void dissmissDialog(){
        dialog.dismiss();
    }
}
