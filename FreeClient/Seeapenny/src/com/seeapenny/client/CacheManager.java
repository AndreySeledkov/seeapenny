package com.seeapenny.client;

import android.view.View;
import com.seeapenny.client.http.ImageDownloader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManager {
   
   private File cachedImagesRoot;
   private long totalImagesCacheSize;
   
   private final ImageDownloader imageDownloader;
   private final BitmapLruCache lruCache;
   
   private Map<String, List<View>> requestedMap = new HashMap<String, List<View>>();
   private Map<String, List<Integer>> requestedPositionMap = new HashMap<String, List<Integer>>();
   
   public CacheManager(int cacheSize, int maxNumImageDownloaders) {
      lruCache = new BitmapLruCache(cacheSize);
      imageDownloader = new ImageDownloader(this, maxNumImageDownloaders);
   }
   
   public void init() {
      cachedImagesRoot = createCacheDir("Images");
   }
   
   private File createCacheDir(String name) {
      File dir = SeeapennyApp.getInstance().getCacheDir();
      
      FileUtils.skipMedia(dir);
      
      return dir;
   }
   
   public void clear() {
      lruCache.setEvictAll(true);
      lruCache.evictAll();
      lruCache.setEvictAll(false);
   }
   
   public CachedBitmapDrawable getDrawable(String imageId) {
      return lruCache.get(imageId);
   }
   
   public void setBitmapTo(String imageId, View view) {
      CachedBitmapDrawable drawable = lruCache.get(imageId);
      if (drawable != null) {
         General.setBitmap(view, drawable);
         return;
      }
      
      boolean notRequested = false;
      List<View> requestedViews = requestedMap.get(imageId);
      List<Integer> requestedPositions = requestedPositionMap.get(imageId);
      if (requestedViews == null) {
         requestedViews = new ArrayList<View>(2);
         requestedMap.put(imageId, requestedViews);
         
         requestedPositions = new ArrayList<Integer>(2);
         requestedPositionMap.put(imageId, requestedPositions);
         
         notRequested = true;
      }
      requestedViews.add(view);
      requestedPositions.add(taggedPosition(view));
      
      if (notRequested) {
         imageDownloader.download(imageId);
      }
   }
   
   private Integer taggedPosition(View view) {
      Object tag = view.getTag(R.id.position_tag);
      if (tag instanceof Integer) {
         return (Integer) tag;
      }
      return Integer.valueOf(-1);
   }
   
   public void downloaded(String imageId, CachedBitmapDrawable drawable) {
      if (drawable == null) {
         imageDownloader.download(imageId);
         return;
      }
      
      lruCache.put(imageId, drawable);
      
      List<View> requestedViews = requestedMap.remove(imageId);
      List<Integer> requestedPositions = requestedPositionMap.remove(imageId);
      for (int i = 0; i < requestedViews.size(); i++) {
         View view = requestedViews.get(i);
         Integer reqPos = requestedPositions.get(i);
         Integer foundPos = taggedPosition(view);
         if (reqPos.equals(foundPos)) {
            General.setBitmap(view, drawable);
         }
      }
   }
   
   public void removeEntry(String imageId) {
      lruCache.remove(imageId);
      File file = imageIdToFile(imageId);
      file.delete();
   }
   
   public synchronized byte[] loadFromFileSystem(String imageId) {
      byte[] data = null;
      File file = imageIdToFile(imageId);
      if (file.exists() && file.length() > 0) {
         data = load(file);
      }
      return data;
   }
   
   private byte[] load(File file) {
      byte[] data = null;
      InputStream in = null;
      try {
         in = new BufferedInputStream(new FileInputStream(file));
         data = new byte[(int) file.length()];
         int offset = 0;
         int readed = 0;
         while (offset < data.length && (readed = in.read(data, offset, data.length - offset)) != -1) {
            offset += readed;
         }
      } catch (IOException e) {
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
            }
         }
      }
      return data;
   }
   
   private File imageIdToFile(String imageId) {
      String name = Integer.toHexString(imageId.hashCode());
      return new File(cachedImagesRoot, name);
   }
   
   private void checkFileSystemSize() {
      if (totalImagesCacheSize == 0) {
         for (File file : cachedImagesRoot.listFiles()) {
            totalImagesCacheSize += file.length();
         }
      }
      if (totalImagesCacheSize > 10 * 1024 * 1024) {
         FileUtils.delete(cachedImagesRoot);
         cachedImagesRoot = createCacheDir("Images");
         totalImagesCacheSize = 0;
      }
   }
   
   private void updateFileSystemSize(long add) {
      totalImagesCacheSize += add;
   }
   
   public synchronized void save(String imageId, byte[] data) {
      checkFileSystemSize();
      
      File file = imageIdToFile(imageId);
      save(file, data);
      
      updateFileSystemSize(data.length);
   }
   
   private void save(File file, byte[] data) {
      OutputStream out = null;
      try {
         out = new BufferedOutputStream(new FileOutputStream(file));
         out.write(data);
         out.flush();
      } catch (IOException e) {
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (IOException e) {
            }
         }
      }
   }
   
}