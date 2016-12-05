/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seeapenny.client;

import java.io.File;
import java.io.IOException;

public final class FileUtils {
   
   public static File createDir(File root, String dirName) {
      File dir = new File(root, dirName);
      if (!dir.exists()) {
         dir.mkdir();
      }
      return dir;
   }
   
   public static boolean delete(File file) {
      if (file.isDirectory()) {
         for (File child : file.listFiles()) {
            delete(child);
         }
      }
      return file.delete();
   }
   
   // skip gallery media scanning for directory, on Android devices
   public static void skipMedia(File dir) {
      if (dir != null && dir.isDirectory()) {
         File nomedia = new File(dir, ".nomedia");
         if (!nomedia.exists()) {
            try {
               nomedia.createNewFile();
            } catch (IOException ex) {
            }
         }
      }
   }
   
}
