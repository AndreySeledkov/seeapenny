package com.seeapenny.client.server;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow.OnDismissListener;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.activity.SeeapennyActivity;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.widget.ClickableToast;

import java.util.ArrayList;
import java.util.List;

public class PopupNotificationManager implements OnClickListener, OnDismissListener {
   
   private List<PopupNotificationMessage> messages = new ArrayList<PopupNotificationMessage>(2);
   private PopupNotificationMessage current;
   private ClickableToast currentToast;
   private boolean currentClicked;
   
   private boolean currentInterrupted;
   private long currentBeforeInterruptedTime;
   private long currentStartShowTime;
   
   public PopupNotificationManager() {
   }
   
   public PopupNotificationMessage getCurrent() {
      return current;
   }
   
   public void show(PopupNotificationMessage notificationMessage, View anchor, int gravity, int xOffset, int yOffset) {
//      Log.d("Manager", "show current: " + current);
      if (current == null) {
         current = notificationMessage;
         
         initCurrentToast(anchor, gravity, current.getDuration() * 1000);
         currentBeforeInterruptedTime = 0;
      } else if (notificationMessage == current) {
         int duration = current.getDuration() * 1000 - (int) currentBeforeInterruptedTime;
//         Log.d("Manager", "interrupted duration: " + duration);
         if (duration > 0 ) {
            initCurrentToast(anchor, gravity, duration);
         }
      } else {
         messages.add(notificationMessage);
      }
   }
   
   private void initCurrentToast(View anchor, int gravity, int duration) {
      ClickableToast toast = new ClickableToast(anchor.getContext());
      toast.setText(current.getMessage());
      toast.setDuration(duration);
      toast.setOnClickListener(this);
      toast.setOnDismissListener(this);
      toast.show(anchor, anchor.getWidth(), anchor.getHeight(), gravity);
      currentToast = toast;
      currentClicked = false;
      
      currentInterrupted = false;
      currentStartShowTime = System.currentTimeMillis();
   }
   
   @Override
   public void onClick(View view) {
      if (current == null) return;
      SeeapennyActivity activity = SeeapennyApp.getInstance().getCurrentActivity();
      if (activity == null) return;
      
      currentClicked = true;
      
      activity.disableWaitForNextCommand();
      HttpCommand command = new HttpCommand(activity, new Response());
      command.addParam(SeeapennyActivity.ID_PARAM, current.getId());
      command.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.NOTIFICATION_CONFIRM_URL);
      
//      HrefAction.action(activity, current.getHref(), current.getContact());
   }
   
   @Override
   public void onDismiss() {
//      Log.d("Manager", "onDismiss numMessages: " + messages.size());
      if (currentInterrupted) return;
      
      current = null;
      currentToast = null;
      
      if (messages.size() > 0 && !currentClicked) {
         SeeapennyActivity activity = SeeapennyApp.getInstance().getCurrentActivity();
         if (activity != null) {
            PopupNotificationMessage message = messages.remove(0);
            activity.showPopupNotification(message);
         }
      }
   }
   
   public void hideCurrent() {
//      Log.d("Manager", "hideCurrent: " + current);
      if (currentToast != null && !currentInterrupted) {
         currentInterrupted = true;
         currentBeforeInterruptedTime += System.currentTimeMillis() - currentStartShowTime;
         
         currentToast.dismiss();
      }
   }
   
   public PopupNotificationMessage getNext() {
      PopupNotificationMessage message = null;
      if (current != null) {
         message = current;
      } else if (messages.size() > 0) {
         message = messages.remove(0);
      }
      return message;
   }
   
}
