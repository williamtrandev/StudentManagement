package com.trantanthanh.student_management.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.trantanthanh.student_management.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }
    public void dismiss() {
        dialog.dismiss();
    }
}
