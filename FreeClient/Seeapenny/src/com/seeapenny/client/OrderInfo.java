package com.seeapenny.client;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderInfo {
   
   private static final String ORDER_ID = "orderId";
   private static final String PRODUCT_ID = "productId";
   private static final String USER_ID = "userId";
   
   private final String orderId;
   private final String productId;
   private final String userId;
   
   public OrderInfo(String orderId, String productId, String userId) {
      this.orderId = orderId;
      this.productId = productId;
      this.userId = userId;
   }
   
   public String getOrderId() {
      return orderId;
   }
   
   public String getProductId() {
      return productId;
   }
   
   public String getUserId() {
      return userId;
   }
   
   public JSONObject toJson() throws JSONException {
      JSONObject root = new JSONObject();
      
      root.put(ORDER_ID, orderId);
      root.put(PRODUCT_ID, productId);
      root.put(USER_ID, userId);
      
      return root;
   }
   
   
   
   public static OrderInfo fromJson(String jsonStr) throws JSONException {
      JSONObject json = new JSONObject(jsonStr);
      String orderId = json.optString(ORDER_ID);
      String productId = json.optString(PRODUCT_ID);
      String userId = json.optString(USER_ID);
      OrderInfo orderInfo = new OrderInfo(orderId, productId, userId);
      
      return orderInfo;
   }
   
}
