package com.seeapenny.client.http;

import android.os.AsyncTask;
import android.os.Build;
import com.seeapenny.client.CacheManager;
import com.seeapenny.client.CachedBitmapDrawable;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SkipperInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class ImageDownloadCommand extends AsyncTask<Void, byte[], byte[]> {
   
   private static final String LOG_TAG = ImageDownloadCommand.class.getSimpleName();
   
   private static final int DEFAULT_BUF_SIZE = 4 * 1024;
   
   private final String imageId;
   private final ImageDownloader manager;
   
   private boolean downloadNotified;
   
   // private static long minDownloadTime = Long.MAX_VALUE;
   // private static long maxDownloadTime = 0;
   // private static long sumDownloadTime;
   // private static int downloadCount;
   
   // private static long minSaveTime = Long.MAX_VALUE;
   // private static long maxSaveTime = 0;
   // private static long sumSaveTime;
   // private static int saveCount;
   
   static {
      if (Build.VERSION.SDK_INT >= 8) {
         System.setProperty("http.keepAlive", "true");
      } else {
         System.setProperty("http.keepAlive", "false");
      }
   }
   
   public ImageDownloadCommand(ImageDownloader manager, String imageId) {
      this.manager = manager;
      this.imageId = imageId;
   }
   
   public String getImageId() {
      return imageId;
   }
   
   @Override
   protected byte[] doInBackground(Void... nothing) {
      CacheManager cache = SeeapennyApp.getCacheManager();
      
      byte[] data = cache.loadFromFileSystem(imageId);
      if (data == null) {
         // long downloadTime = System.currentTimeMillis();
         
         data = download(imageId, cache);
         
         // downloadTime = System.currentTimeMillis() - downloadTime;
         // printDownloadTimes(downloadTime);
         
         publishProgress(data);
         
         if (data != null) {
            // long saveTime = System.currentTimeMillis();
            
            cache.save(imageId, data);
            
            // saveTime = System.currentTimeMillis() - saveTime;
            // printSaveTimes(saveTime);
         }
      }
      
      return data;
   }
   
   private byte[] download(String urlStr, CacheManager cache) {
      byte[] data = null;
      ByteArrayOutputStream baos = null;
      
      HttpURLConnection conn = null;
      try {
         URL url = new URL(urlStr);
         conn = (HttpURLConnection) url.openConnection();
         
         conn.setConnectTimeout(3000);
         conn.setReadTimeout(6000);
         
         conn.setDoOutput(false);
         conn.setRequestMethod("GET");
         conn.setAllowUserInteraction(false);
         conn.setUseCaches(false);
         
         conn.setRequestProperty("User-Agent", SeeapennyApp.userAgent());
         conn.setRequestProperty("Accept-Encoding", "identity");
         String[] cookies = SeeapennyApp.getHttpHandler().getAllCookies();
         for (String cookie : cookies) {
            conn.setRequestProperty("Cookie", cookie);
         }
         
         InputStream in = conn.getInputStream();
         if (Build.VERSION.SDK_INT < 8) {
            in = new SkipperInputStream(in);
         }
         final int contentLength = conn.getContentLength();
         if (contentLength > -1) {
            data = new byte[contentLength];
            
            int readed;
            int sumReaded = 0;
            while ((readed = in.read(data, sumReaded, data.length - sumReaded)) != -1) {
               sumReaded += readed;
            }
         } else {
            baos = new ByteArrayOutputStream(2 * DEFAULT_BUF_SIZE);
            byte[] buffer = new byte[DEFAULT_BUF_SIZE];
            
            int readed;
            while ((readed = in.read(buffer)) != -1) {
               baos.write(buffer, 0, readed);
            }
            
            data = baos.toByteArray();
         }
         in.close();
      } catch (SocketTimeoutException stex) {
         data = null;
      } catch (IOException ioex) {
         data = null;
      } finally {
         if (baos != null) {
            try {
               baos.close();
            } catch (IOException e) {
            }
         }
         if (conn != null) {
            conn.disconnect();
         }
      }
      
      return data;
   }
   
   // private synchronized void printDownloadTimes(long time) {
   // minDownloadTime = Math.min(minDownloadTime, time);
   // maxDownloadTime = Math.max(maxDownloadTime, time);
   // sumDownloadTime += time;
   // downloadCount++;
   //
   // if (downloadCount >= 1000) {
   // long avgTime = sumDownloadTime / downloadCount;
   // Log.d("Image", "download avg: " + avgTime + " min: " + minDownloadTime + " max: " + maxDownloadTime);
   // }
   // }
   
   // private synchronized void printSaveTimes(long time) {
   // minSaveTime = Math.min(minSaveTime, time);
   // maxSaveTime = Math.max(maxSaveTime, time);
   // sumSaveTime += time;
   // saveCount++;
   //
   // if (saveCount >= 1000) {
   // long avgTime = sumSaveTime / saveCount;
   // Log.d("Image", "save avg: " + avgTime + " min: " + minSaveTime + " max: " + maxSaveTime);
   // }
   // }
   
   @Override
   protected void onProgressUpdate(byte[]... values) {
      byte[] data = values[0];
      downloadComplete(data);
      downloadNotified = true;
   }
   
   protected void downloadComplete(byte[] data) {
      CachedBitmapDrawable drawable = null;
      if (data != null) {
         drawable = CachedBitmapDrawable.create(SeeapennyApp.getAppResources(), data, SeeapennyApp.getMaxImageSquare());
         if (!drawable.isValid()) {       //--- SkImageDecoder::Factory returned null
            drawable = null;
         }
      }
      
      manager.downloaded(this, drawable);
   }
   
   @Override
   protected void onPostExecute(byte[] data) {
      if (!downloadNotified) {
         downloadComplete(data);
      }
   }
   
}