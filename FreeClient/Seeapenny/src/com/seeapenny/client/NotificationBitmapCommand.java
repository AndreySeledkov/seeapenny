package com.seeapenny.client;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import com.seeapenny.client.http.ImageDownloadCommand;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NotificationBitmapCommand extends ImageDownloadCommand {
   
   private Context context;
   private int notificationId;
   private String title;
   private String message;
   private PendingIntent pendingIntent;
   private int smallIconId;

   public NotificationBitmapCommand(Context context, int notificationId, String title, String message, PendingIntent pendingIntent, int smallIconId, String largeIconUrl) {
      super(null, largeIconUrl);
      this.context = context;
      this.notificationId = notificationId;
      this.title = title;
      this.message = message;
      this.pendingIntent = pendingIntent;
      this.smallIconId = smallIconId;
   }
   
   @Override
   protected void downloadComplete(byte[] data) {
      Bitmap bitmap = null;
      if (data != null) {
         bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
         if (bitmap != null) {
            int srcWidth = bitmap.getWidth();
            int srcHeight = bitmap.getHeight();
            
            Resources res = context.getResources();
            int width;
            int height;
            if (Build.VERSION.SDK_INT > 10) {
               width = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
               height = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
            } else {
               width = General.dpToPx(res, 64);
               height = General.dpToPx(res, 64);
            }
            
            float scaleW = width / (float) srcWidth;
            float scaleH = height / (float) srcHeight;
            float scale = Math.max(scaleW, scaleH);
            bitmap = Bitmap.createScaledBitmap(bitmap,(int) (srcWidth * scale), (int) (srcHeight * scale), true);
         }
      }
      
      General.showNotification(context, notificationId, title, message, pendingIntent, smallIconId, bitmap);      
   }
   
}
