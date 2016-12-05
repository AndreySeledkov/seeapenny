package com.seeapenny.client;

import com.seeapenny.client.server.Image;
import com.seeapenny.client.server.User;
import com.seeapenny.client.server.requests.LoginRequest;

import java.io.Serializable;

public class AppState implements Serializable {
   
   private User contact;
   private Image contactPhoto;
   private LoginRequest loginRequest;
   
   private boolean captchaSubmitted;
   
   private int lookAtMeTopRating;
   
   private boolean refreshFavourites;
   private boolean refreshNotifications;
   private boolean refreshLookAtMe;
   private boolean refreshFlirts;
   private boolean refreshChats;
   private boolean refreshChat;
   
   private boolean unreadMessagesNotified;
   
   public AppState() {
      
   }
   
   public void resetFlags() {
      refreshFavourites = false;
      refreshNotifications = false;
      refreshLookAtMe = false;
      refreshFlirts = false;
      refreshChats = false;
      refreshChat = false;
      
      unreadMessagesNotified = false;
   }
   
   public void setContact(User contact) {
      this.contact = contact;
   }
   
   public User getContact() {
      return contact;
   }
   
   public void setContactPhoto(Image contactPhoto) {
      this.contactPhoto = contactPhoto;
   }
   
   public Image getContactPhoto() {
      return contactPhoto;
   }
   
   public void setLoginRequest(LoginRequest loginRequest) {
      this.loginRequest = loginRequest;
   }
   
   public LoginRequest getLoginRequest() {
      return loginRequest;
   }
   
   public void setCaptchaSubmitted(boolean captchaSubmitted) {
      this.captchaSubmitted = captchaSubmitted;
   }
   
   public boolean isCaptchaSubmitted() {
      return captchaSubmitted;
   }
   
   public void setLookAtMeTopRating(int lookAtMeTopRating) {
      this.lookAtMeTopRating = lookAtMeTopRating;
   }
   
   public int getLookAtMeTopRating() {
      return lookAtMeTopRating;
   }
   
   public void setRefreshFavourites(boolean refreshFavourites) {
      this.refreshFavourites = refreshFavourites;
   }
   
   public boolean isRefreshFavourites() {
      return refreshFavourites;
   }
   
   public void setRefreshNotifications(boolean refreshNotification) {
      this.refreshNotifications = refreshNotification;
   }
   
   public boolean isRefreshNotifications() {
      return refreshNotifications;
   }
   
   public void setRefreshLookAtMe(boolean refreshLookAtMe) {
      this.refreshLookAtMe = refreshLookAtMe;
   }
   
   public boolean isRefreshLookAtMe() {
      return refreshLookAtMe;
   }
   
   public void setRefreshFlirts(boolean refreshFlirts) {
      this.refreshFlirts = refreshFlirts;
   }
   
   public boolean isRefreshFlirts() {
      return refreshFlirts;
   }
   
   public void setRefreshChats(boolean refreshChats) {
      this.refreshChats = refreshChats;
   }
   
   public boolean isRefreshChats() {
      return refreshChats;
   }
   
   public void setRefreshChat(boolean refreshChat) {
      this.refreshChat = refreshChat;
   }
   
   public boolean isRefreshChat() {
      return refreshChat;
   }
   
   public void setUnreadMessagesNotified(boolean unreadMessagesNotified) {
      this.unreadMessagesNotified = unreadMessagesNotified;
   }
   
   public boolean isUnreadMessagesNotified() {
      return unreadMessagesNotified;
   }
   
}
