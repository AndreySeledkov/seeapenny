package com.seeapenny.client.server;

import com.seeapenny.client.CachedBitmapDrawable;
import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLUtils;
import com.seeapenny.client.xml.XMLable;
import org.json.JSONObject;
import org.w3c.dom.Element;

import java.io.Serializable;

public class Image implements JSONable, XMLable, Serializable {
   
   private long id;
   private String largeUrl;
   private String smallUrl;
   private boolean notModerated;
   private int numComments;
   
   private transient CachedBitmapDrawable drawable;
   
   public Image() {
      
   }
   
   public Image(CachedBitmapDrawable drawable) {
      this.drawable = drawable;
   }
   
   public void copyFields(Image that) {
      this.id = that.id;
      this.largeUrl = that.largeUrl;
      this.smallUrl = that.smallUrl;
      this.notModerated = that.notModerated;
      this.numComments = that.numComments;
   }
   
   public long getId() {
      return id;
   }
   
   public void setId(long id) {
      this.id = id;
   }
   
   public String getLargeUrl() {
      return largeUrl;
   }
   
   public void setLargeUrl(String largeUrl) {
      this.largeUrl = largeUrl;
   }
   
   public String getSmallUrl() {
      return smallUrl;
   }
   
   public void setSmallUrl(String smallUrl) {
      this.smallUrl = smallUrl;
   }
   
   public int getNumComments() {
      return numComments;
   }
   
   public CachedBitmapDrawable getBitmap() {
      return drawable;
   }
   
   public boolean isNotModerated() {
      return notModerated;
   }
   
   public void fromJson(JSONObject dto) {
      id = dto.optLong("@id");
      smallUrl = dto.optString("@smallUrl");
      largeUrl = dto.optString("@largeUrl");
      notModerated = dto.optBoolean("@notModerated");
      numComments = dto.optInt("@commentsCount");
   }
   
   @Override
   public JSONable createForJsonArray() {
      return new Image();
   }
   
   @Override
   public void fromXml(Element dto) {
      id = XMLUtils.attributeLong(dto, "id");
      smallUrl = dto.getAttribute("smallUrl");
      largeUrl = dto.getAttribute("largeUrl");
      notModerated = XMLUtils.attributeBoolean(dto, "notModerated");
      numComments = XMLUtils.attributeInt(dto, "commentsCount");
   }
   
}
