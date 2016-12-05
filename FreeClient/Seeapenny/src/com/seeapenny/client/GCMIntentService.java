package com.seeapenny.client;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.seeapenny.client.activity.LoginActivity;
import com.seeapenny.client.activity.SeeapennyActivity;
import com.seeapenny.client.activity.ShopListActivity;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpCommandListener;
import com.seeapenny.client.server.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class GCMIntentService extends GCMBaseIntentService implements HttpCommandListener {

    public GCMIntentService() {
        super(SeeapennyActivity.C2DM_SENDER);
    }

    private static final String LOG_TAG = GCMIntentService.class.getSimpleName();

    @Override
    protected void onRegistered(Context context, String registrationId) {
        String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        SeeapennyApp app = SeeapennyApp.getInstance();
        if (!app.loadInstall()) {
            String imei = app.imei();
            String uid = app.uid();
            String referrer = app.loadMarketReferrer();
            sendInstallRegistrationCommand(registrationId, deviceId, imei, uid, referrer, SeeapennyApp.getAppLocale());

            app.saveInstall();
        } else {
            sendUpdateRegistrationCommand(registrationId, deviceId);
        }
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String payload = intent.getStringExtra("payload");
//         Bundle bundle = intent.getExtras();
//         for (String key: bundle.keySet()) {
//            Log.d(LOG_TAG, "Extra '" + key + "': " + bundle.get(key));
//         }
//      }

        String title = null;
        String message = null;
        String targetStr = null;
        long notificationId = 0;
        String imageUrl = null;
        try {
            JSONObject notificationDto = new JSONObject(payload);
            title = notificationDto.optString("title", null);
            message = notificationDto.optString("message");
            targetStr = notificationDto.optString("target");
            notificationId = notificationDto.optLong("collapseKey", 0);
            imageUrl = notificationDto.optString("imageUrl", null);
        } catch (JSONException jsonex) {
            Log.e("C2DMReceiver", "Parsing JSON", jsonex);
        }

        AppState appState = SeeapennyApp.getInstance().getAppState();
        Class<? extends SeeapennyActivity> targetClass = ShopListActivity.class;

//      if ("contacts".equals(targetStr)) {
//         targetClass = Chats.class;
//      } else if ("gifts".equals(targetStr)) {
//         targetClass = ViewGifts.class;
//      } else if ("money".equals(targetStr)) {
//         targetClass = Money.class;
//      } else if ("profile".equals(targetStr)) {
//         targetClass = Profile.class;
//      } else if ("notifications".equals(targetStr)) {
//         targetClass = Notifications.class;
//         appState.setRefreshNotifications(true);
//      } else if ("lookatme".equals(targetStr)) {
//         targetClass = LookAtMe.class;
//         appState.setRefreshLookAtMe(true);
//      } else if ("favourites".equals(targetStr)) {
//         targetClass = Favorites.class;
//         appState.setRefreshFavourites(true);
//      } else if ("flirts".equals(targetStr)) {
//         targetClass = Flirts.class;
//         appState.setRefreshFlirts(true);
//      } else {
//         targetClass = f.class;
//      }

        addMessagesNofication(context, title, message, targetClass, (int) notificationId, imageUrl);
    }

    @Override
    protected void onError(Context context, String errorId) {
    }

    private void sendUpdateRegistrationCommand(String registrationId, String deviceId) {
        HttpCommand command = new HttpCommand(this, new Response());
        command.addParam(SeeapennyActivity.ID_PARAM, registrationId);
        command.addParam("gcm", "true");
        command.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.C2DM_UPDATE_URL.toString());
    }

    private void sendInstallRegistrationCommand(String registrationId, String deviceId, String imei, String uid,
                                                String referrer, String appLocale) {
        HttpCommand command = new HttpCommand(this, new Response());
        command.addParam(SeeapennyActivity.ID_PARAM, registrationId);
        command.addParam(SeeapennyActivity.IMEI_PARAM, imei);
        command.addParam(SeeapennyActivity.UID_PARAM, uid);
        command.addParam(SeeapennyActivity.DEVICE_PARAM, deviceId);
        command.addParam(SeeapennyActivity.PARTNER_PARAM, referrer);
        command.addParam(SeeapennyActivity.APP_LOCALE_PARAM, appLocale);
        command.addParam("gcm", "true");
        command.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.C2DM_INSTALL_URL.toString());
    }

    public void addMessagesNofication(Context context, String title, String message, Class<? extends SeeapennyActivity> target, int notificationId, String largeIconUrl) {
        if (TextUtils.isEmpty(title)) {
            title = getResources().getString(R.string.app_name);
        }

        Intent notificationIntent = null;
        if (target != LoginActivity.class) {
            notificationIntent = new Intent(context, ShopListActivity.class);
        } else {
            notificationIntent = new Intent(context, target);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        if (TextUtils.isEmpty(largeIconUrl) || Build.VERSION.SDK_INT < 11) {
            General.showNotification(getApplicationContext(), notificationId, title, message, pendingIntent, R.drawable.unread_icon, null);
        } else {
            NotificationBitmapCommand command = new NotificationBitmapCommand(getApplicationContext(), notificationId, title, message, pendingIntent, R.drawable.unread_icon, largeIconUrl);
            General.executeAsyncTask(command, new Void[0]);
        }
    }

    @Override
    public void onResponse(HttpCommand command, Response response) {
    }

    @Override
    public void onError(HttpCommand command, int code, String reason) {
    }

    @Override
    public void beforeSend(HttpCommand httpCommand) {
    }

    @Override
    public void onPartSent(HttpCommand httpCommand, int offset, int length) {
    }

    @Override
    public void afterReceive(HttpCommand httpCommand) {
    }

}
