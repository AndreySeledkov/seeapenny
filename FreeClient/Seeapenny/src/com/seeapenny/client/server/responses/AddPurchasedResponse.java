package com.seeapenny.client.server.responses;

import com.seeapenny.client.server.Response;

import org.json.JSONObject;

public class AddPurchasedResponse extends Response {
   
   // <response status="ok">
   // <addMoneyAnswer status="tryLater" />
   // </response>
   
   // {"@status":"ok","addMoneyAnswer":{"@status":"accepted"},"balance":{"@points":"1850"}}
   
   private String status;
   
   private boolean balanceAvailable = true;
   private int points;
   
   public String getStatus() {
      return status;
   }
   
   public boolean isBalanceAvailable() {
      return balanceAvailable;
   }
   
   public int getPoints() {
      return points;
   }
   
   @Override
   public void fromJson(JSONObject rootDto) {
      super.fromJson(rootDto);
      
      JSONObject answerDto = rootDto.optJSONObject("addMoneyAnswer");
      if (answerDto != null) {
         status = answerDto.optString("@status");
      }
      
      JSONObject balanceDto = rootDto.optJSONObject("balance");
      if (balanceDto != null) {
         balanceAvailable = !balanceDto.optBoolean("@unavailable");
         points = balanceDto.optInt("@points");
      }
   }
   
}
