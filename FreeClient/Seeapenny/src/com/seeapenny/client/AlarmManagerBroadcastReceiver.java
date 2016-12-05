package com.seeapenny.client;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.service.ShopListService;
import com.seeapenny.client.util.ExtrasConst;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    private ShopListService shopListService = Services.getListService();


    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Bundle bundle = intent.getExtras();

        long id = bundle.getLong(ExtrasConst.LIST_ID);
        long ownerID = bundle.getLong(ExtrasConst.OWNER_ID);

        ShopList shopList = shopListService.getShopList(id, ownerID);

        if (shopList != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(context.getResources().getString(R.string.notificationTitleShopList));
            builder.setContentText(context.getResources().getString(R.string.notificationTextShopList, shopList.getName()));
            builder.setSmallIcon(R.drawable.icon);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setContentIntent(contentIntent);
            builder.setWhen(System.currentTimeMillis());

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify((int) id, builder.build());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id,
                    new Intent(context, AlarmManagerBroadcastReceiver.class),
                    PendingIntent.FLAG_NO_CREATE);

            if (pendingIntent != null) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    public void setOnetimeTimer(Context context, long time, long id, long ownerID) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ExtrasConst.LIST_ID, id);
        intent.putExtra(ExtrasConst.OWNER_ID, ownerID);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, time, pi);


    }
}