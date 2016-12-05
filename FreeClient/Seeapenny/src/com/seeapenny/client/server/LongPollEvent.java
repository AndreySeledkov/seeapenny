package com.seeapenny.client.server;


import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.bean.GoodCategory;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.json.JSONable;
import com.seeapenny.client.xml.XMLable;

import org.json.JSONObject;
import org.w3c.dom.Element;

import java.util.Date;

public class LongPollEvent implements JSONable, XMLable {

    public static final String TYPE_ADD_GOOD = "addGood";
    public static final String TYPE_DELETE_GOOD = "deleteGood";
    public static final String TYPE_EDIT_GOOD = "editGood";

    public static final String TYPE_SHARE_LIST = "shareList";
    public static final String TYPE_EDIT_SHARE_LIST = "editSharedList";
    public static final String TYPE_UN_SHARE_LIST = "unshareList";

    public static final String TYPE_ADD_CATEGORY = "addCategory";
    public static final String TYPE_EDIT_CATEGORY = "editCategory";
    public static final String TYPE_REMOVE_CATEGORY = "deleteCategory";

    public static final String TYPE_USER_GET_ACCESS = "userGetAccess";
    public static final String TYPE_USER_LOSE_ACCESS = "userLoseAccess";

    //   public static final String TYPE_ANOTHER_LONG_POLL = "anotherLongPoll";
    public static final String TYPE_CLIENT_CLOSED = "clientClosed";
    public static final String TYPE_NO_SESSION = "noSession";
    public static final String TYPE_THROW_USER = "throwUser";
    public static final String TYPE_TIME_OUT = "timeOut";
    public static final String TYPE_SYSTEM_MESSAGE = "systemMessage";
    public static final String TYPE_UNDER_MAINTENANCE = "serverUnderMaintenance";
    public static final String TYPE_INFO_MESSAGE = "infoMessage";
    public static final String TYPE_POPUP_DIALOG = "dialog";
    public static final String TYPE_POPUP_NOTIFICATION = "popupNotification";

    public static final String[] TYPES = {TYPE_CLIENT_CLOSED, TYPE_NO_SESSION, TYPE_THROW_USER,
            TYPE_TIME_OUT, TYPE_SYSTEM_MESSAGE, TYPE_UNDER_MAINTENANCE, TYPE_INFO_MESSAGE,
            TYPE_POPUP_NOTIFICATION};

    public static final String[] NOTIFICATION_TYPES = {TYPE_SYSTEM_MESSAGE, TYPE_INFO_MESSAGE};

    private String type;

    private ShopListResponse shopListResponse;
    private GoodResponse goodResponse;


    private GoodCategory goodCategory;
    private User user;


    private String messageText; // for systemMessage


    private Date date; // for userFavMe, userWatchMe, purchasePoints, sentGift,

    private PopupDialogMessage popupDialogMessage;
    private PopupNotificationMessage popupNotificationMessage;

    public LongPollEvent() {

    }

    public LongPollEvent(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public PopupDialogMessage getPopupDialogMessage() {
        return popupDialogMessage;
    }

    public PopupNotificationMessage getPopupNotificationMessage() {
        return popupNotificationMessage;
    }

    @Override
    public void fromJson(JSONObject dto) {
        JSONObject eventDto = dto;
        if (type == null) {
            //todo edit share list inverse
            type = dto.names().optString(0);
            eventDto = dto.optJSONObject(type);
        }

        if (isShareList()) {
            shopListResponse = new ShopListResponse();
            JSONUtils.fillJSONable(eventDto, "list", shopListResponse);
        }

        if (isUnShareList()) {
            shopListResponse = new ShopListResponse();
            JSONUtils.fillJSONable(eventDto, "list", shopListResponse);
        }

        if (isEditShareList()) {
            shopListResponse = new ShopListResponse();
            JSONUtils.fillJSONable(eventDto, "list", shopListResponse);
        }


        if (isAddGood()) {
            goodResponse = new GoodResponse();
            JSONUtils.fillJSONable(eventDto, "good", goodResponse);
        }

        if (isEditGood()) {
            goodResponse = new GoodResponse();
            JSONUtils.fillJSONable(eventDto, "good", goodResponse);
        }

        if (isDeleteGood()) {
            goodResponse = new GoodResponse();
            JSONUtils.fillJSONable(eventDto, "good", goodResponse);
        }

        if (isGetUserAccess()) {
            user = new User();
            JSONUtils.fillJSONable(eventDto, "user", user);
        }

        if (isLoseUserAccess()) {
            user = new User();
            JSONUtils.fillJSONable(eventDto, "user", user);
        }


        if (isSystemMessage()) {
            messageText = eventDto.optString("@message");
            date = JSONUtils.fromJsonDate(eventDto, "@date", SeeapennyApp.DATE_TIME_FORMATTER);
            return;
        }

        if (isInfoMessage()) {
            messageText = eventDto.optString("@message");
            date = JSONUtils.fromJsonDate(eventDto, "@date", SeeapennyApp.DATE_TIME_FORMATTER);
            return;
        }

        if (isPopupDialog()) {
            popupDialogMessage = new PopupDialogMessage();
            popupDialogMessage.fromJson(eventDto);
            return;
        }

        if (isPopupNotifcation()) {
            popupNotificationMessage = new PopupNotificationMessage();
            popupNotificationMessage.fromJson(eventDto);
            return;
        }

    }

    @Override
    public LongPollEvent createForJsonArray() {
        return new LongPollEvent(type);
    }

    @Override
    public void fromXml(Element root) {
        type = root.getNodeName();

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShopListResponse getShopListResponse() {
        return shopListResponse;
    }

    public GoodResponse getGoodResponse() {
        return goodResponse;
    }

    public GoodCategory getGoodCategory() {
        return goodCategory;
    }

    public boolean isGetUserAccess() {
        return TYPE_USER_GET_ACCESS.equals(type);
    }

    public boolean isLoseUserAccess() {
        return TYPE_USER_LOSE_ACCESS.equals(type);
    }

    public boolean isAddCategory() {
        return TYPE_ADD_CATEGORY.equals(type);
    }

    public boolean isEditCategory() {
        return TYPE_EDIT_CATEGORY.equals(type);
    }

    public boolean isRemoveCategory() {
        return TYPE_REMOVE_CATEGORY.equals(type);
    }

    public boolean isShareList() {
        return TYPE_SHARE_LIST.equals(type);
    }

    public boolean isUnShareList() {
        return TYPE_UN_SHARE_LIST.equals(type);
    }

    public boolean isEditShareList() {
        return TYPE_EDIT_SHARE_LIST.equals(type);
    }

    public boolean isAddGood() {
        return TYPE_ADD_GOOD.equals(type);
    }

    public boolean isDeleteGood() {
        return TYPE_DELETE_GOOD.equals(type);
    }

    public boolean isEditGood() {
        return TYPE_EDIT_GOOD.equals(type);
    }


    public boolean isClientClosed() {
        return TYPE_CLIENT_CLOSED.equals(type);
    }

    public boolean isNoSession() {
        return TYPE_NO_SESSION.equals(type);
    }

    public boolean isThrowUser() {
        return TYPE_THROW_USER.equals(type);
    }

    public boolean isTimeOut() {
        return TYPE_TIME_OUT.equals(type);
    }

    public boolean isSystemMessage() {
        return TYPE_SYSTEM_MESSAGE.equals(type);
    }

    public boolean isUnderMaintenance() {
        return TYPE_UNDER_MAINTENANCE.equals(type);
    }

    public boolean isInfoMessage() {
        return TYPE_INFO_MESSAGE.equals(type);
    }

    public boolean isPopupDialog() {
        return TYPE_POPUP_DIALOG.equals(type);
    }

    public boolean isPopupNotifcation() {
        return TYPE_POPUP_NOTIFICATION.equals(type);
    }

}
