package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLUtils;
import com.seeapenny.client.xml.XMLable;
import org.json.JSONObject;
import org.w3c.dom.Element;

public class Response implements JSONable, XMLable {
   
   private String status;
   private int code;
   private String message;
   private JSONObject errorDto;
   
   public boolean isError() {
      if (status == null) {
         return true;
      }
      return "error".equals(status.toLowerCase());
   }
   
   public int getCode() {
      return code;
   }
   
   public String getMessage() {
      return message;
   }
   
   public JSONObject getErrorDto() {
      return errorDto;
   }
   
   public void fromJson(JSONObject rootDto) {
      status = rootDto.optString("@status");
      code = rootDto.optInt("code");
      message = rootDto.optString("message");
      
      if (isError()) {
         errorDto = rootDto;
      }
   }
   
   // <response status="error">
   // <code>123</code>
   // <message>some message</message>
   // </response>
   public void fromXml(Element root) {
      status = root.getAttribute("status");
      code = XMLUtils.elementInt(root, "code");
      message = XMLUtils.elementString(root, "message");
   };
   
   @Override
   public final JSONable createForJsonArray() {
      return null;
   }

   public String getVersion() {
      return null;
   }

    public String getStatus() {
        return status;
    }
}
