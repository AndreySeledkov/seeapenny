package com.seeapenny.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.seeapenny.client.activity.SeeapennyActivity;

public class ConnectionDialog implements DialogInterface.OnClickListener {

    private SeeapennyActivity activity;
    private boolean allowRedirectToSettings;

    public ConnectionDialog(SeeapennyActivity activity, boolean allowRedirectToSettings) {
        this.activity = activity;
        this.allowRedirectToSettings = allowRedirectToSettings;
    }

    public void show() {
        if (allowRedirectToSettings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle(R.string.no_internet_title);
            builder.setMessage(R.string.no_internet);
            builder.setPositiveButton(android.R.string.yes, this);
            builder.setNegativeButton(android.R.string.no, this);
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle(R.string.no_internet_title);
            builder.setMessage(R.string.no_internet_warn);

//            builder.setNeutralButton(android.R.string.ok, this);

            builder.setPositiveButton(R.string.use_offline, this);
            builder.setNegativeButton(R.string.menuLeftLogout, this);


            builder.setCancelable(false);
            builder.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == Dialog.BUTTON_POSITIVE) {
            General.toLogReg(activity);
        } else if (which == Dialog.BUTTON_NEGATIVE) {
            General.toLogRegClear(activity);
        }


//        if (which == Dialog.BUTTON_POSITIVE) {
//            Intent intent = null;
//            if (Build.VERSION.SDK_INT >= 14) {
//                intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//            } else {
//                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//            }
//            activity.startActivity(intent);
//            dialog.dismiss();
//        } else {
//            General.toLogReg(activity);
//        }
    }

}
