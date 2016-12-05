package com.seeapenny.client.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;

import com.seeapenny.client.General;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
//import com.seeapenny.client.activity.CategoriesActivity;
import com.seeapenny.client.activity.GoodActivity;
import com.seeapenny.client.activity.SeeapennyActivity;
import com.seeapenny.client.activity.ShopListActivity;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpCommandListener;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.util.FieldGood;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LongPollManager implements HttpCommandListener {

    private boolean started;
    private boolean stopped;

    private ConcurrentLinkedQueue<LongPollEvent> eventQueue = new ConcurrentLinkedQueue<LongPollEvent>();

    public void start() {
        sendLongPollCommand();


        started = true;
        stopped = false;
    }

    public void restore() {
        if (checkConnection() && !started && !stopped) {
            start();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager connectivity = (ConnectivityManager) SeeapennyApp.getInstance().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.getState() == State.CONNECTED;
    }

    private void sendLongPollCommand() {
        HttpCommand command = new HttpCommand(this, new LongPollResponse());
        command.sendAsLongPoll(SeeapennyApp.getHttpHandler(), SeeapennyActivity.LONG_POLL_URL);
    }

    @Override
    public void onResponse(HttpCommand command, Response response) {
        started = false;

        LongPollResponse pollResponse = (LongPollResponse) response;
        boolean[] answers = processLongPollResponse(pollResponse);
        boolean continuePolling = answers[0];
        boolean toLogReg = answers[1];
        boolean toMaintenance = answers[2];
        if (continuePolling) {
            start();
        } else {
            stopped = true;

            SeeapennyApp app = SeeapennyApp.getInstance();

            SeeapennyActivity activity = app.getCurrentActivity();
            if (activity != null) {
                if (toMaintenance) {
//                    General.toMaintenance(activity, R.string.under_maintenance);
                } else if (toLogReg) {
                    app.setAuthorized(false);
                    General.toLogRegClear(activity);
                }
            }
        }
    }

    @Override
    public void onError(HttpCommand command, int code, String reason) {
        started = false;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionState sessionState = SeeapennyApp.getInstance().getSessionState();
                if (sessionState.isSessionValid()) {
                    restore();
                }
            }
        }, 30 * 1000);

    }

    @Override
    public void beforeSend(HttpCommand httpCommand) {
    }

    @Override
    public void onPartSent(HttpCommand httpCommand, int offset, int length) {
    }

    @Override
    public void afterReceive(HttpCommand httpCommand) {
    }

    private boolean[] processLongPollResponse(LongPollResponse response) {
        SeeapennyApp app = SeeapennyApp.getInstance();
        SessionState sessionState = app.getSessionState();
//        AppState appState = app.getAppState();

        boolean continuePolling = true;
        boolean toLog = false;
        boolean toMaintenance = false;

        List<LongPollEvent> events = response.getEvents();
        if (events != null) {
            for (LongPollEvent event : events) {
//                if (event.isAnotherLongPoll()) {
//                    continuePolling = false;
//                    toLogReg = false;
//                } else
                if (event.isClientClosed()) {
                    continuePolling = false;
                    toLog = false;
                } else if (event.isNoSession()) {
                    continuePolling = false;
                    toLog = false;
                } else if (event.isThrowUser()) {
                    continuePolling = false;
                    toLog = true;
                } else if (event.isTimeOut()) {
                    continuePolling = false;
                    toLog = true;
                } else if (event.isUnderMaintenance()) {
                    continuePolling = false;
                    toLog = false;
                    toMaintenance = true;
                } else if (event.isShareList()) {
                    ShopListResponse shopListResponse = event.getShopListResponse();

                    Date serverCreateTime = shopListResponse.getCreateTime();
                    Date localCreateTime = app.convertToLocalDate(serverCreateTime);

                    Date serverLastModifiedTime = shopListResponse.getLastModifiedTime();
                    Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

                    Services.getListService().addShareShopList(shopListResponse.getId(), shopListResponse.getOwnerId(), shopListResponse.getName(), app.formatDatetime(localCreateTime), app.formatDatetime(localLastModifiedTime), SynchAction.NO_CHANGES);
                } else if (event.isUnShareList()) {
                    ShopListResponse shopListResponse = event.getShopListResponse();
                    Services.getListService().remove(shopListResponse.getId(), shopListResponse.getOwnerId());
                } else if (event.isEditShareList()) {
                    ShopListResponse shopListResponse = event.getShopListResponse();
//                    Services.getListService().save(shopListResponse);//todo
                } else if (event.isAddGood()) {
                    GoodResponse eventGoodResponse = event.getGoodResponse();

                    Date serverCreateTime = eventGoodResponse.getCreateTime();
                    Date localCreateTime = app.convertToLocalDate(serverCreateTime);

                    Date serverLastModifiedTime = eventGoodResponse.getModifiedTime();
                    Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);


                    Good goodResponse = Services.getGoodService().insertShareGood(eventGoodResponse.getId(), eventGoodResponse.getListId(), eventGoodResponse.getName(), eventGoodResponse.getPrice(), eventGoodResponse.getQuantity(), eventGoodResponse.getMeasure(),
                            eventGoodResponse.isPriority(), eventGoodResponse.getNote(), eventGoodResponse.getCategoryId(), eventGoodResponse.getImageId(), eventGoodResponse.getState(), eventGoodResponse.getOwnerId(),
                            eventGoodResponse.getLastEditorId(), app.formatDatetime(localCreateTime), app.formatDatetime(localLastModifiedTime), SynchAction.NO_CHANGES);

                    if (eventGoodResponse != null) {
                        ShopList shopListResponse = Services.getListService().getShopList(goodResponse.getListId(), goodResponse.getOwnerId());
                        shopListResponse.setLastModifiedTime(localLastModifiedTime);
                        Services.getListService().save(shopListResponse);

                        app.getCurrentActivity().makeSoundAdd();
                    }
                } else if (event.isEditGood()) {
                    GoodResponse eventGoodResponse = event.getGoodResponse();

                    Good goodResponse = Services.getGoodService().getGoodById(eventGoodResponse.getListId(), eventGoodResponse.getId(), eventGoodResponse.getOwnerId());

                    EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();
                    goodResponse.changeName(eventGoodResponse.getName(), fieldGoods);
                    goodResponse.changeCategory(eventGoodResponse.getCategoryId(), fieldGoods);
                    goodResponse.changePrice(eventGoodResponse.getPrice(), fieldGoods);
                    goodResponse.changeState(eventGoodResponse.getState(), fieldGoods);
                    goodResponse.changeQuantity(eventGoodResponse.getQuantity(), fieldGoods);
                    goodResponse.changeMeasure(eventGoodResponse.getMeasure(), fieldGoods);
                    goodResponse.changePriority(eventGoodResponse.isPriority(), fieldGoods);
                    goodResponse.changeNote(eventGoodResponse.getNote(), fieldGoods);
                    goodResponse.changeLastEditor(eventGoodResponse.getLastEditorId(), fieldGoods);
                    goodResponse.changeImageID(eventGoodResponse.getImageId(), fieldGoods);

                    if (fieldGoods.size() > 0) {
                        Date serverLastModifiedTime = goodResponse.getModifiedTime();

                        Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);
                        goodResponse.setModifiedTime(localLastModifiedTime);
                        fieldGoods.add(FieldGood.MODIFIED_TIME);

                        boolean ok = Services.getGoodService().save(fieldGoods, goodResponse);
                        if (ok) {
                            ShopList shopListResponse = Services.getListService().getShopList(goodResponse.getListId(), goodResponse.getOwnerId());
                            shopListResponse.setLastModifiedTime(localLastModifiedTime);

                            Services.getListService().save(shopListResponse);

                            app.getCurrentActivity().makeSoundAdd();
                        }
                    }
                } else if (event.isDeleteGood()) {
                    GoodResponse goodResponse = event.getGoodResponse();
                    boolean ok = Services.getGoodService().remove(goodResponse.getId(), goodResponse.getListId(), goodResponse.getOwnerId());
                    if (ok) {
//                        Services.getListService().save(shopList);
                        app.getCurrentActivity().makeSoundRemove();
                    }
                }else if (event.isGetUserAccess()) {
                    User user = event.getUser();
//                    Services.getAccessService().addAccess();
                } else if (event.isLoseUserAccess()) {
                    User user = event.getUser();

                } else {
                    SeeapennyActivity activity = app.getCurrentActivity();
                    processActivityRequiredEvent(activity, event);
                }
            }

//            if (app.getCurrentActivity() instanceof CategoriesActivity) {
////                ((CategoriesActivity) app.getCurrentActivity()).refreshGoodItems();
//            }
            if (!toLog) {

                if (app.getCurrentActivity() instanceof GoodActivity) {
                    ((GoodActivity) app.getCurrentActivity()).refreshGoodItems();
                }

                if (app.getCurrentActivity() instanceof ShopListActivity) {
                    ((ShopListActivity) app.getCurrentActivity()).refreshLists();
                }
            }

        }

        boolean[] answers = new boolean[]{continuePolling, toLog, toMaintenance};
        return answers;
    }

    private void processActivityRequiredEvent(SeeapennyActivity activity, LongPollEvent event) {
        if (activity == null) {
            eventQueue.add(event);
            return;
        }

//        if (event.isNewMessage()) {
//            activity.onNewMessage(event);
//        } else
        if (event.isSystemMessage()) {
//            activity.onNewNotification(event);
        }
//        else if (event.isGiftReceived()) {
//            activity.onNewNotification(event);
//        } else if (event.isPointPurchased()) {
//            activity.onNewNotification(event);
//        } else if (event.isFavoriteMe()) {
//            activity.onNewNotification(event);
//        } else if (event.isWatchMe()) {
//            activity.onNewNotification(event);
//        } else if (event.isInfoMessage()) {
//            activity.onNewNotification(event);
//        }
        else if (event.isPopupDialog()) {
//            PopupDialogMessage dialogMessage = event.getPopupDialogMessage();
//            if (HrefAction.isCorrectScheme(dialogMessage.getHref())) {
//                activity.showPopupDialog(dialogMessage);
//            }
        } else if (event.isPopupNotifcation()) {
//            PopupNotificationMessage notificationMessage = event.getPopupNotificationMessage();
//            if (HrefAction.isCorrectScheme(notificationMessage.getHref())) {
//                activity.showPopupNotification(notificationMessage);
//            }
        }
//        else if (event.isNewBuzz()) {
//            activity.onNewBuzzEventsCount(event.getBuzzEventsCount());
//        }
    }

    public void processEventQueue() {
        EventQueueProccessor queueeProccessor = new EventQueueProccessor();
        queueeProccessor.start();
    }


    private class EventQueueProccessor implements Runnable, Handler.Callback {

        private final Handler handler;

        public EventQueueProccessor() {    //must be called from UI thread
            this.handler = new Handler(this);
        }

        public void start() {
            Thread thread = new Thread(this, EventQueueProccessor.class.getSimpleName());
            thread.start();
        }

        @Override
        public void run() {
            LongPollEvent event = null;
            while ((event = eventQueue.poll()) != null) {
                Message msg = handler.obtainMessage(1, event);
                msg.sendToTarget();
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            LongPollEvent event = (LongPollEvent) msg.obj;
            SeeapennyActivity activity = SeeapennyApp.getInstance().getCurrentActivity();
            processActivityRequiredEvent(activity, event);
            return true;
        }

    }
}
