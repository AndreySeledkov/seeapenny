package com.seeapenny.client.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public final class XMLUtils {
   
   private XMLUtils() {
      
   }
   
   public static Element element(Element root, String name) {
      Element value = null;
      
      // Log.d("XML element", "root: " + root.getNodeName());
      // NodeList childs = root.getChildNodes();
      // for (int i = 0; i < childs.getLength(); i++) {
      // Element child = (Element) childs.item(i);
      // Log.d("XML element", "   child: " + child.getNodeName());
      // }
      
      NodeList nodes = root.getElementsByTagName(name);
      if (nodes.getLength() > 0) {
         value = (Element) nodes.item(0);
      }
      return value;
   }
   
   public static String elementString(Element root, String name) {
      String value = null;
      Element element = element(root, name);
      if (element != null && element.hasChildNodes()) {
         value = element.getFirstChild().getNodeValue();
      }
      return value;
   }
   
   public static int elementInt(Element root, String name) {
      int value = 0;
      String valueStr = elementString(root, name);
      if (valueStr != null && valueStr.length() > 0) {
         value = Integer.parseInt(valueStr);
      }
      return value;
   }
   
   public static long elementLong(Element root, String name) {
      long value = 0;
      String valueStr = elementString(root, name);
      if (valueStr != null && valueStr.length() > 0) {
         value = Long.parseLong(valueStr);
      }
      return value;
   }
   
   public static int attributeInt(Element root, String name) {
      int value = 0;
      String valueStr = root.getAttribute(name);
      if (valueStr != null && valueStr.length() > 0) {
         value = Integer.parseInt(valueStr);
      }
      return value;
   }
   
   public static long attributeLong(Element root, String name) {
      long value = 0;
      String valueStr = root.getAttribute(name);
      if (valueStr != null && valueStr.length() > 0) {
         value = Long.parseLong(valueStr);
      }
      return value;
   }
   
   public static double attributeDouble(Element root, String name) {
      double value = 0;
      String valueStr = root.getAttribute(name);
      if (valueStr != null && valueStr.length() > 0) {
         value = Double.parseDouble(valueStr);
      }
      return value;
   }
   
   public static boolean attributeBoolean(Element root, String name) {
      boolean value = false;
      String valueStr = root.getAttribute(name);
      if (valueStr != null && valueStr.length() > 0) {
         value = Boolean.parseBoolean(valueStr);
      }
      return value;
   }
   
   public static Date attributeDate(Element root, String name, DateFormat formatter) {
      String dateStr = root.getAttribute(name);
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
   
   public static void fillXMLable(Element root, String name, XMLable target) {
      Element child = element(root, name);
      if (child != null) {
         target.fromXml(child);
      }
   }
   
}
