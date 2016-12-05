package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONUtils;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NotificationsResponse extends PagedResponse<LongPollEvent> {
   
   // <response status="ok">
   // <notifications totalCount="5" fromIndex="0" count="50">
   // <systemMessage message="33" date="2012-01-13T14:41:47+0200"/>
   // <sentGift date="2012-01-13T14:41:47+0200">
   // <gift>
   // <user userId="29318" .../>
   // <image smallUrl="..." largeUrl="..."/>
   // </gift>
   // </sentGift>
   // <purchasePoints points="100" date="2012-01-13T14:41:31+0200"/>
   // <userFavMe date="2012-01-13T14:36:23+0200">
   // <user userId="29318" .../>
   // </userFavMe>
   // <userWatchMe date="2012-01-13T14:33:34+0200">
   // <user userId="29318" .../>
   // </userWatchMe>
   // </notifications>
   // </response>
   
   // {"@status":"ok","notifications":{"@count":"100","@fromIndex":"0","@totalCount":"2","userWatchMe":[{"@date":"2012-02-03T17:41:49+0400"},{"@date":"2012-02-03T17:41:32+0400"}]}}
   
   // {"@status":"ok","notifications":{"@count":"20","@fromIndex":"0","@totalCount":"2","infoMessage":[{"@message":"You earned 10 daily free points!","@date":"2012-04-04T11:34:20+0400"},{"@message":"You earned 10 daily free points!","@date":"2012-04-01T12:46:02+0400"}]}}
   
   @Override
   public List<LongPollEvent> getItems() {
      if (items == null) {
         items = new ArrayList<LongPollEvent>(0);
      }
      return items;
   }
   
   @Override
   public void fromJson(JSONObject rootDto) {
      JSONObject dto = fromJson(rootDto, "notifications");
      
      items = new ArrayList<LongPollEvent>();
      for (String type : LongPollEvent.NOTIFICATION_TYPES) {
         JSONUtils.arrayToList(dto, type, items, new LongPollEvent(type));
      }
   }
   
   @Override
   public void fromXml(Element root) {
      Element dto = fromXml(root, "notifications");
      if (dto != null) {
         items = new ArrayList<LongPollEvent>();
         NodeList childs = dto.getChildNodes();
         for (int i = 0; i < childs.getLength(); i++) {
            Node node = childs.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
               Element child = (Element) node;
               LongPollEvent event = new LongPollEvent();
               event.fromXml(child);
               items.add(event);
            }
         }
      }
   }
   
}
