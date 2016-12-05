package com.seeapenny.client.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public final class JSONUtils {
   
   private JSONUtils() {
      
   }
   
   public static void fillJSONable(JSONObject rootDto, String name, JSONable target) {
      JSONObject dto = rootDto.optJSONObject(name);
      if (dto != null) {
         target.fromJson(dto);
      }
   }
   
   public static <J extends JSONable> void fillJSONableList(JSONArray arrayDto, List<J> targets, J firstElement) {
      if (arrayDto != null) {
         J target = firstElement;
         for (int i = 0, size = arrayDto.length(); i < size; i++) {
            JSONObject objDto = arrayDto.optJSONObject(i);
            if (objDto != null) {
               target.fromJson(objDto);
               targets.add(target);
               target = target.createForJsonArray();
            }
         }
      }
   }
   
   public static <J extends JSONable> void arrayToList(JSONObject rootDto, String arrayName, List<J> targets,
         J firstElement) {
      if (rootDto != null) {
         JSONArray arrayDto = JSONUtils.array(rootDto, arrayName);
         JSONUtils.fillJSONableList(arrayDto, targets, firstElement);
      }
   }
   
   public static Date fromJsonDate(JSONObject dto, String name, DateFormat formatter) {
      String dateStr = dto.optString(name);
      Date date = null;
      if (dateStr != null && dateStr.length() > 0) {
         try {
            date = formatter.parse(dateStr);
         } catch (ParseException e) {
            e.printStackTrace();
         }
      }
      return date;
   }
   
   // public static Date fromJsonDate(JSONObject dto, String name, String
   // format) {
   // String dateStr = dto.optString(name);
   // Date date = null;
   // if (dateStr != null && dateStr.length() > 0) {
   // SimpleDateFormat formater = threadFormats.get();
   // if (formater == null) {
   // formater = new SimpleDateFormat(format);
   // threadFormats.set(formater);
   // }
   // try {
   // date = formater.parse(dateStr);
   // } catch (ParseException e) {
   // e.printStackTrace();
   // }
   // }
   // return date;
   // }
   
   public static JSONArray array(JSONObject rootDto, String name) {
      JSONArray arrayDto = rootDto.optJSONArray(name);
      if (arrayDto == null) {
         JSONObject objDto = rootDto.optJSONObject(name);
         if (objDto != null) {
            arrayDto = new JSONArray();
            arrayDto.put(objDto);
         }
      }
      
      return arrayDto;
   }
   
   public static JSONArray wrappedArray(JSONObject rootDto, String wrapperName, String name) {
      JSONArray arrayDto = null;
      JSONObject objDto = rootDto.optJSONObject(wrapperName);
      if (objDto != null) {
         arrayDto = array(objDto, name);
      }
      
      return arrayDto;
   }
   
}
