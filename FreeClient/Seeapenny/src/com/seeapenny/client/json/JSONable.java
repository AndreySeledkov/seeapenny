package com.seeapenny.client.json;

import org.json.JSONObject;

public interface JSONable {
   
   public void fromJson(JSONObject dto);
   
   public <J extends JSONable> J createForJsonArray();
   
}
