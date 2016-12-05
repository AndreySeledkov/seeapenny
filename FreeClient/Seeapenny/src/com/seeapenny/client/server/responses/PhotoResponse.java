package com.seeapenny.client.server.responses;

import com.seeapenny.client.server.Image;
import com.seeapenny.client.server.Response;
import org.json.JSONObject;

public class PhotoResponse extends Response {
   
   // {"@status":"ok","photo":{"@largeUrl":"http://192.168.0.35:9013/app/getimage?id=46&s=2&big","@smallUrl":"http://192.168.0.35:9013/app/getimage?id=46&s=2","@id":"46"}}
   
   private Image photo;
   
   public Image getPhoto() {
      return photo;
   }
   
   @Override
   public void fromJson(JSONObject rootDto) {
      super.fromJson(rootDto);
      JSONObject dto = rootDto.optJSONObject("photo");
      if (dto == null) {
         dto = rootDto.optJSONObject("file");
      }
      if (dto != null) {
         photo = new Image();
         photo.fromJson(dto);
      }
   }
}
