package com.seeapenny.client.server;

import com.seeapenny.client.json.JSONUtils;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class LongPollResponse extends Response {
   
   private List<LongPollEvent> events;
   public List<LongPollEvent> getEvents() {
      return events;
   }

   @Override
   public void fromJson(JSONObject rootDto) {
      events = new ArrayList<LongPollEvent>();
      JSONUtils.arrayToList(rootDto, "events", events, new LongPollEvent());
      
      if (events.size() == 0) {
         for (String type : LongPollEvent.TYPES) {
            JSONUtils.arrayToList(rootDto, type, events, new LongPollEvent(type));
         }
      }
      
      if (events.size() == 0) {
         LongPollEvent event = new LongPollEvent();
         event.fromJson(rootDto);
         events.add(event);
      }
   }
   
   @Override
   public void fromXml(Element rootDto) {
      events = new ArrayList<LongPollEvent>();
      NodeList childs = rootDto.getChildNodes();
      for (int i = 0; i < childs.getLength(); i++) {
         Node node = childs.item(i);
         if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element child = (Element) node;
            LongPollEvent event = new LongPollEvent();
            event.fromXml(child);
            events.add(event);
         }
      }
   }
   
}
