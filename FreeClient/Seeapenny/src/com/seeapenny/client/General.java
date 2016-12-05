package com.seeapenny.client;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.seeapenny.client.activity.LoginActivity;
import com.seeapenny.client.activity.LoginManager;
import com.seeapenny.client.activity.SeeapennyActivity;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 31.01.13
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public class General {


    public static void setBitmap(View view, CachedBitmapDrawable drawable) {
        Object tag = null; //view.getTag(R.id.wait_tag);
        if (tag instanceof ProgressBar) {
            ProgressBar wait = (ProgressBar) tag;
            wait.setVisibility(View.INVISIBLE);

            view.setTag(0, null);   //R.id.wait_tag,
        }

        if (drawable == null) {
            return;
        }

        ((ImageView) view).setImageDrawable(drawable);
    }

    public static void tryDownloadImage(View view, String imageUrl) {
        if (imageUrl != null && imageUrl.length() > 0) {
            SeeapennyApp.getCacheManager().setBitmapTo(imageUrl, view);
        }
    }

    public static void tryDownloadWaitableImage(View imageView, View waitView, String imageUrl) {
        if (imageUrl != null && imageUrl.length() > 0) {
            ProgressBar wait = (ProgressBar) waitView.findViewById(R.id.wait);
            wait.setVisibility(View.VISIBLE);
            imageView.setTag(R.id.wait_tag, wait);

            SeeapennyApp.getCacheManager().setBitmapTo(imageUrl, imageView);
        } else {
            if (waitView != null) {
                waitView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static <P> AsyncTask<P, ?, ?> executeAsyncTask(AsyncTask<P, ?, ?> task, P... params) {
        if (Build.VERSION.SDK_INT >= 14) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            return task.execute(params);
        }

    }

    public static <P> void executeTask(AsyncTask<P, ?, ?> task, P... params) {
//      if (Build.VERSION.SDK_INT >= 14) {
//         task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
//      } else {
//        task.execute(params);
//      }
        task.execute(params);
    }


//    public static void toMaintenance(Context context, int messageResId) {
//        Intent intent = new Intent(context, Maintenance.class);
//        intent.putExtra(Maintenance.MESSAGE, messageResId);
//        context.startActivity(intent);
//    }

//    public static void toLogReg(Context context) {
//        Intent intent = new Intent(context, LoginActivity.class);
//        context.startActivity(intent);
//    }

    public static void toLogReg(Context context) {
//        if (context instanceof Logo) {
//            ((Logo) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        }

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void toLogRegClear(Context context) {
//        if (context instanceof Logo) {
//            ((Logo) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        }

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

//    public static void toMain(Context context) {
//        Intent intent = new Intent(context, ShopListActivity.class);
//        context.startActivity(intent);
//    }

    public static void silentLogin(SeeapennyActivity activity, String login, String passw, boolean blockUI,
                                   boolean changeScreen, boolean changeScreenOnError) {

        System.out.println("#### silentLogin ####");
        LoginManager loginManager = new LoginManager(activity, blockUI, changeScreen, changeScreenOnError);
        loginManager.login(login, passw);
    }

    public static void silentFacebookLogin(SeeapennyActivity activity, boolean blockUI, boolean changeScreen,
                                           boolean changeScreenOnError) {
        LoginManager loginManager = new LoginManager(activity, blockUI, changeScreen, changeScreenOnError);
        loginManager.facebookLogin(SeeapennyApp.getFacebook());
    }

    public static void silentVkLogin(SeeapennyActivity activity, boolean blockUI, boolean changeScreen,
                                     boolean changeScreenOnError, String url) {
        LoginManager loginManager = new LoginManager(activity, blockUI, changeScreen, changeScreenOnError);
        loginManager.vkLogin(url);
    }

    public static void silentGoogleLogin(SeeapennyActivity activity, boolean blockUI, boolean changeScreen,
                                     boolean changeScreenOnError, String url) {
        LoginManager loginManager = new LoginManager(activity, blockUI, changeScreen, changeScreenOnError);
        loginManager.googleLogin(url);
    }

    public static void silentGmailLogin(SeeapennyActivity activity, boolean blockUI, boolean changeScreen,
                                        boolean changeScreenOnError) {
        LoginManager loginManager = new LoginManager(activity, blockUI, changeScreen, changeScreenOnError);
//        loginManager.gmailLogin(EconomyApp.getVkontakte());
    }


    public static int dpToPx(Resources res, int dp) {
        return (int) (res.getDisplayMetrics().density * dp + 0.5f);
    }

    public static void showNotification(Context context, int notificationId, String title, String message, PendingIntent pendingIntent, int smallIconId, Bitmap largeIconBitmap) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(message);
        if (smallIconId != 0) {
            builder.setSmallIcon(smallIconId);
        }
        if (largeIconBitmap != null) {
            builder.setLargeIcon(largeIconBitmap);
        }
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, builder.build());
    }

    public static void commit(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
