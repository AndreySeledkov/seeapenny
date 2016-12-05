package com.seeapenny.client;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CachedBitmapDrawable extends Drawable {
   
   private final byte[] data;
   private final Options decodeOpts;
   private final Resources res;
   
   private BitmapDrawable drawable;
   private Rect lastKnownBounds;
   
   private CachedBitmapDrawable(Resources res, byte[] data, Options decodeOpts) {
      this.res = res;
      this.data = data;
      this.decodeOpts = decodeOpts;
      
      initDrawable();
   }
   
   private void initDrawable() {
      Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOpts);
      drawable = new BitmapDrawable(res, bitmap);
   }
   
   public boolean isValid() {
      return (drawable.getBitmap() != null);
   }
   
   public int sizeOf() {
      int w = 0;
      int h = 0;
      if (drawable != null) {
         Bitmap bitmap = drawable.getBitmap();
         if (bitmap != null) {
            w = bitmap.getWidth();
            h = bitmap.getHeight();
         }
      }
      return w * h * 4 + data.length;
   }
   
   private void recreateIfNeeded() {
      if (drawable == null || drawable.getBitmap() == null || drawable.getBitmap().isRecycled()) {
         initDrawable();
         
         if (lastKnownBounds != null) {
            drawable.setBounds(lastKnownBounds);
         }
      }
   }
   
   @Override
   public void draw(Canvas canvas) {
      recreateIfNeeded();
      
      drawable.draw(canvas);
   }
   
   public void recycle() {
      if (drawable != null) {
         lastKnownBounds = drawable.getBounds();
         
         Bitmap bitmap = drawable.getBitmap();
         if (bitmap != null) {
            bitmap.recycle();
         }
         
         drawable = null;
      }
   }
   
   @Override
   public int getOpacity() {
      if (drawable != null) {
         return drawable.getOpacity();
      }
      return PixelFormat.TRANSPARENT;
   }
   
   @Override
   public void setAlpha(int alpha) {
      if (drawable != null) {
         drawable.setAlpha(alpha);
      }
   }
   
   @Override
   public void setColorFilter(ColorFilter cf) {
      if (drawable != null) {
         drawable.setColorFilter(cf);
      }
   }
   
   @Override
   public void setBounds(int left, int top, int right, int bottom) {
      if (drawable != null) {
         drawable.setBounds(left, top, right, bottom);
      }
   }
   
   @Override
   public void setBounds(Rect bounds) {
      if (drawable != null) {
         drawable.setBounds(bounds);
      }
   }
   
   @Override
   public int getIntrinsicWidth() {
      if (drawable != null) {
         return drawable.getIntrinsicWidth();
      }
      return -1;
   }
   
   @Override
   public int getIntrinsicHeight() {
      if (drawable != null) {
         return drawable.getIntrinsicHeight();
      }
      return -1;
   }
   
   public void setDrawableCallback(Callback callback) {
      if (drawable != null) {
         drawable.setCallback(callback);
      }
   }



   public static CachedBitmapDrawable create(Resources res, byte[] data, long maxBitmapSquare) {
      Options boundsOpts = new Options();
      boundsOpts.inJustDecodeBounds = true;
      BitmapFactory.decodeByteArray(data, 0, data.length, boundsOpts);
      int width = boundsOpts.outWidth;
      int height = boundsOpts.outHeight;

      Options decodeOpts = new Options();
      decodeOpts.inPurgeable = true;
      decodeOpts.inInputShareable = true;
      int scale = 1;
      if (maxBitmapSquare > 0) {
         while (width * height > maxBitmapSquare) {
            width /= 2;
            height /= 2;
            scale *= 2;
         }
      }
      decodeOpts.inSampleSize = scale;

      return new CachedBitmapDrawable(res, data, decodeOpts);
   }

   public static CachedBitmapDrawable create(Context context, Uri uri, int limitHeight) throws IOException {
      ContentResolver contentResolver = context.getContentResolver();
      InputStream input = contentResolver.openInputStream(uri);

      Options boundsOpts = new Options();
      boundsOpts.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(input, null, boundsOpts);
      input.close();

      double ratio = boundsOpts.outHeight / limitHeight;

      Options decodeOpts = new Options();
      decodeOpts.inPurgeable = true;
      decodeOpts.inInputShareable = true;
      decodeOpts.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
      
      input = contentResolver.openInputStream(uri);
      byte[] data = load(input);
      input.close();
      
      return new CachedBitmapDrawable(context.getResources(), data, decodeOpts);
   }
   
   private static byte[] load(InputStream input) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(4 * 1024);
      byte[] buffer = new byte[4 * 1024];
      int readed;
      try {
         while ((readed = input.read(buffer)) != -1) {
            baos.write(buffer, 0, readed);
         }
      } catch (IOException e) {
      }
      return baos.toByteArray();
   }
   
   private static int getPowerOfTwoForSampleRatio(double ratio) {
      int k = Integer.highestOneBit((int) Math.floor(ratio));
      if (k == 0) {
         return 1;
      }
      return k;
   }
   
}
