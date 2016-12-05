package com.seeapenny.client;

import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, CachedBitmapDrawable> {
   
   private boolean evictAll;
   
   public BitmapLruCache(int size) {
      super(size);
   }
   
   public void setEvictAll(boolean evictAll) {
      this.evictAll = evictAll;
   }
   
   protected int sizeOf(String key, CachedBitmapDrawable bitmap) {
      return bitmap.sizeOf();
   }
   
   @Override
   protected void entryRemoved(boolean evicted, String key, CachedBitmapDrawable oldValue, CachedBitmapDrawable newValue) {
      if (!evictAll && key.contains("static/presents")) {
         put(key, oldValue);
         return;
      }
      
      if (oldValue != null) {
         oldValue.recycle();
      }
   }
   
}
