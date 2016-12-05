package com.seeapenny.client.server;

import com.seeapenny.client.xml.XMLUtils;
import org.json.JSONObject;
import org.w3c.dom.Element;

import java.util.List;

public class PagedResponse<I> extends Response {
   
   private int pages;
   private int perPage;
   private int page;
   private int totalCount;
   
   private int count;
   private int fromIndex;
   
   protected List<I> items;
   
   public int getPages() {
      return pages;
   }
   
   public int getPerPage() {
      return perPage;
   }
   
   public int getPage() {
      return page;
   }
   
   public int getTotalCount() {
      return totalCount;
   }
   
   public int getCount() {
      return count;
   }
   
   public int getFromIndex() {
      return fromIndex;
   }
   
   public List<I> getItems() {
      return items;
   }
   
   public final JSONObject fromJson(JSONObject rootDto, String name) {
      super.fromJson(rootDto);
      JSONObject dto = rootDto.optJSONObject(name);
      if (dto != null) {
         pages = dto.optInt("@pages");
         perPage = dto.optInt("@perPage");
         page = dto.optInt("@page");
         totalCount = dto.optInt("@totalCount");
         
         count = dto.optInt("@count");
         fromIndex = dto.optInt("@fromIndex");
      }
      return dto;
   }
   
   public final Element fromXml(Element root, String name) {
      super.fromXml(root);
      Element dto = XMLUtils.element(root, name);
      if (dto != null) {
         pages = XMLUtils.attributeInt(dto, "pages");
         perPage = XMLUtils.attributeInt(dto, "perPage");
         page = XMLUtils.attributeInt(dto, "page");
         totalCount = XMLUtils.attributeInt(dto, "totalCount");
         
         count = XMLUtils.attributeInt(dto, "count");
         fromIndex = XMLUtils.attributeInt(dto, "fromIndex");
      }
      return dto;
   }
   
}
