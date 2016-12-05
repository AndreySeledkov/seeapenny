package com.seeapenny.client.http;

import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Serializable;

public class SerializableCookie implements Serializable {
   
   private String name;
   private String value;
   private int version;
   private String domain;
   private String path;
   
   public SerializableCookie() {
   }
   
   public String getName() {
      return name;
   }
   
   public String getValue() {
      return value;
   }
   
   public int getVersion() {
      return version;
   }
   
   public String getDomain() {
      return domain;
   }
   
   public String getPath() {
      return path;
   }
   
   public Cookie toCookie() {
      BasicClientCookie cookie = new BasicClientCookie(name, value);
      cookie.setVersion(version);
      cookie.setDomain(domain);
      cookie.setPath(path);
      cookie.setAttribute(ClientCookie.VERSION_ATTR, Integer.toString(version));
      cookie.setAttribute(ClientCookie.DOMAIN_ATTR, domain);
      cookie.setAttribute(ClientCookie.PATH_ATTR, path);
      return cookie;
   }
   
   
   
   public static SerializableCookie create(Cookie cookie) {
      SerializableCookie sc = new SerializableCookie();
      sc.name = cookie.getName();
      sc.value = cookie.getValue();
      sc.version = cookie.getVersion();
      sc.domain = cookie.getDomain();
      sc.path = cookie.getPath();
      return sc;
   }
   
}
