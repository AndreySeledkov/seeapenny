package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLUtils;
import com.seeapenny.client.xml.XMLable;
import org.json.JSONObject;
import org.w3c.dom.Element;

public class PopupDialogMessage implements JSONable, XMLable {
   
   private static final String FREE_POINTS_SKIN = "freePoints";
   
   private String id;
   private String message;
   private String href;
   private String yesText;
   private String noText;
   
   private String skin;
   
   private User contact;
   
   public PopupDialogMessage() {
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
   
   public String getYesText() {
      return yesText;
   }
   
   public String getNoText() {
      return noText;
   }
   
   public User getContact() {
      return contact;
   }
   
   public boolean isFreePointsSkin() {
      return FREE_POINTS_SKIN.equals(skin);
   }
   
   @Override
   public void fromJson(JSONObject root) {
      id = root.optString("@id");
      message = root.optString("@message");
      href = root.optString("@href");
      yesText = root.optString("@yestText");
      noText = root.optString("@noText");
      skin = root.optString("@skin");
      
      JSONObject contactDto = root.optJSONObject("user");
      if (contactDto != null) {
         contact = new User();
         contact.fromJson(contactDto);
      }
   }
   
   @Override
   public void fromXml(Element root) {
      id = root.getAttribute("id");
      message = root.getAttribute("message");
      href = root.getAttribute("href");
      yesText = root.getAttribute("yesText");
      noText = root.getAttribute("noText");
      skin = root.getAttribute("skin");
      
      Element contactDto = XMLUtils.element(root, "user");
      if (contactDto != null) {
         contact = new User();
         contact.fromXml(contactDto);
      }
   }
   
   @Override
   public PopupDialogMessage createForJsonArray() {
      return new PopupDialogMessage();
   }
   
}
