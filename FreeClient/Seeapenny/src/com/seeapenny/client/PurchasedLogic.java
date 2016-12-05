package com.seeapenny.client;

import android.app.Activity;

import com.seeapenny.client.activity.SeeapennyActivity;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpCommandListener;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.responses.AddPurchasedResponse;

import java.util.List;

public class PurchasedLogic implements HttpCommandListener {
   
   private final OrderInfo orderInfo;
   
   public PurchasedLogic(OrderInfo orderInfo) {
      this.orderInfo = orderInfo;
   }
   
   public void sendAddPointsCommand() {
      HttpCommand command = new HttpCommand(this, new AddPurchasedResponse());
      command.addParam(SeeapennyActivity.PRODUCT_PARAM, orderInfo.getProductId());
      command.addParam(SeeapennyActivity.ORDER_NUMBER_PARAM, orderInfo.getOrderId());
      command.addParam(SeeapennyActivity.USER_PARAM, orderInfo.getUserId());
      command.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.ADD_PURCHASED_URL.toString());
   }
   
   @Override
   public void afterReceive(HttpCommand httpCommand) {
   }
   
   @Override
   public void onPartSent(HttpCommand httpCommand, int offset, int length) {
   }
   
   @Override
   public void beforeSend(HttpCommand httpCommand) {
   }
   
   @Override
   public void onResponse(HttpCommand command, Response response) {
       AddPurchasedResponse addResponse = (AddPurchasedResponse) response;
      if ("accepted".equals(addResponse.getStatus())) {
//         MeetApp app = MeetApp.getInstance();
//         app.purchased(orderInfo.getOrderId());
//         if (addResponse.isBalanceAvailable()) {
//            int points = addResponse.getPoints();
//            app.getSessionState().setPoints(points);
//
//            Activity activity = app.getCurrentActivity();
//            if (activity instanceof Money) {
//               ((Money) activity).updateAccount(points);
//            }
//         }
      }
   }
   
   @Override
   public void onError(HttpCommand command, int code, String reason) {
   }
   
   public static void checkPurchasedOrders() {
//      MeetApp app = MeetApp.getInstance();
//      List<OrderInfo> purchases = app.getPurchasedOrders();
//      for (OrderInfo orderInfo: purchases) {
//         PurchasedLogic pl = new PurchasedLogic(orderInfo);
//         pl.sendAddPointsCommand();
//      }
   }
   
}
