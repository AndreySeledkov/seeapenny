package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLUtils;
import com.seeapenny.client.xml.XMLable;
import org.json.JSONObject;
import org.w3c.dom.Element;

public class PopupNotificationMessage implements JSONable, XMLable {
   
   private String id;
   private String message;
   private String href;
   private int duration;
   
   private User contact;
   
   public PopupNotificationMessage() {
   }
   
   public String getId() {
      return id;
   }
   
   public String getMessage() {
      return message;
   }
   
   public String getHref() {
      return href;
   }
   
   public int getDuration() {
      return duration;
   }
   
   public User getContact() {
      return contact;
   }
   
   @Override
   public void fromJson(JSONObject root) {
      id = root.optString("@id");
      message = root.optString("@message");
      href = root.optString("@href");
      duration = root.optInt("@duration");
      
      JSONObject contactDto = root.optJSONObject("user");
      if (contactDto != null) {
         contact = new User();
         contact.fromJson(contactDto);
      }
   }
   
   @Override
   public PopupNotificationMessage createForJsonArray() {
      return new PopupNotificationMessage();
   }
   
   @Override
   public void fromXml(Element root) {
      id = root.getAttribute("id");
      message = root.getAttribute("message");
      href = root.getAttribute("href");
      duration = XMLUtils.attributeInt(root, "duration");
      
      Element contactDto = XMLUtils.element(root, "user");
      if (contactDto != null) {
         contact = new User();
         contact.fromXml(contactDto);
      }
   }
   
}
