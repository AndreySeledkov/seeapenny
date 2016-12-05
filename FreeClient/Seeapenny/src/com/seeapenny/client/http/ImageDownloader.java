package com.seeapenny.client.http;


import com.seeapenny.client.CacheManager;
import com.seeapenny.client.CachedBitmapDrawable;
import com.seeapenny.client.General;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ImageDownloader {
   
   private final ConcurrentLinkedQueue<ImageDownloadCommand> pendingCommands = new ConcurrentLinkedQueue<ImageDownloadCommand>();
   
   private final CacheManager cache;
   private final int maxRunningCommands;
   
   private int numRunningCommands;
   
   public ImageDownloader(CacheManager cache, int maxRunningCommands) {
      this.cache = cache;
      this.maxRunningCommands = maxRunningCommands;
   }
   
   public void download(String imageId) {
      ImageDownloadCommand command = new ImageDownloadCommand(this, imageId);
      pendingCommands.add(command);
      checkPendingCommands();
   }
   
   public void downloaded(ImageDownloadCommand command, CachedBitmapDrawable drawable) {
      String imageId = command.getImageId();
      numRunningCommands--;
      checkPendingCommands();
      
      cache.downloaded(imageId, drawable);
   }
   
   private void checkPendingCommands() {
      if (numRunningCommands < maxRunningCommands) {
         ImageDownloadCommand command = pendingCommands.poll();
         if (command != null) {
            General.executeAsyncTask(command, new Void[0]);
            numRunningCommands++;
         }
      }
   }
   
}