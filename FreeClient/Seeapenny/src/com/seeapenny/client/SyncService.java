package com.seeapenny.client;

import android.app.Activity;
import android.os.AsyncTask;

import com.seeapenny.client.activity.SeeapennyActivity;
import com.seeapenny.client.activity.ShopListActivity;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.http.HttpCommand;
import com.seeapenny.client.http.HttpCommandListener;
import com.seeapenny.client.http.HttpHandler;
import com.seeapenny.client.http.HttpUploadCommand;
import com.seeapenny.client.server.BussinessError;
import com.seeapenny.client.server.GoodResponse;
import com.seeapenny.client.server.Image;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.ShopListResponse;
import com.seeapenny.client.server.requests.SyncGoodRequest;
import com.seeapenny.client.server.requests.SyncShopListRequest;
import com.seeapenny.client.server.responses.*;
import com.seeapenny.client.service.CategoryService;
import com.seeapenny.client.service.GoodService;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.service.ShopListService;
import com.seeapenny.client.util.FieldGood;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncService extends AsyncTask<Void, Void, String> {

    private SimpleDateFormat format;
    private SeeapennyApp app;

    private RecoveryListHttpCommandListener recoveryListHttpCommandListener;
    private RecoveryGoodHttpCommandListener recoveryGoodHttpCommandListener;

    protected ShopListService shopListService = Services.getListService();
    protected GoodService goodService = Services.getGoodService();
    protected CategoryService categoryService = Services.getCategoryService();

    public SyncService() {
        this.format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        this.app = SeeapennyApp.getInstance();
        recoveryListHttpCommandListener = new RecoveryListHttpCommandListener();
        recoveryGoodHttpCommandListener = new RecoveryGoodHttpCommandListener();
    }

    @Override
    protected String doInBackground(Void... voids) {
        final List<Good> goodList = goodService.getAllGood();


        final AtomicInteger atomicInteger = new AtomicInteger();
        for (final Good good : goodList) {

            String fileName = format.format(good.getCreateTime());
            File file = new File(app.getStorageDir(), fileName + ".jpg");
            if (file.exists()) {
                HttpUploadCommand photoUploadCommand = new HttpUploadCommand(new HttpCommandListener() {
                    @Override
                    public void onResponse(HttpCommand command, Response response) {

                        PhotoResponse photoResponse = (PhotoResponse) response;
                        Image photo = photoResponse.getPhoto();
                        good.setImageId(photo.getId());

                        EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();
                        fieldGoods.add(FieldGood.IMAGE);

                        goodService.save(fieldGoods, good);

                        atomicInteger.incrementAndGet();
                        if (atomicInteger.get() == goodList.size()) {
//                            if (clearedData) {
//                                shopListLastID();
//                            } else {
                            sendListShopData();
//                            }
                        }
                    }

                    @Override
                    public void onError(HttpCommand command, int code, String reason) {
                        SyncService.this.onError(command, code, reason);
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
                }, new PhotoResponse());
                photoUploadCommand.sendUpload(SeeapennyActivity.UPLOAD_PHOTO_GOOD, file.toString(), "image/jpeg", HttpHandler.MIME_JSON);
            } else {
                atomicInteger.incrementAndGet();
                if (atomicInteger.get() == goodList.size()) {
                    sendListShopData();
                }
            }
        }

        if (goodList.isEmpty()) {
            sendListShopData();
        }

        return null;
    }

    private void afterSendListShopData() {

        Services.getListService().resetLastId();

        List<ShopList> list = shopListService.getAllListForSynch(ShopListService.includeFilter);

        SyncShopListRequest shopListRequest = new SyncShopListRequest();
        shopListRequest.setShopLists(list);

        try {
            JSONObject loginJson = shopListRequest.toJson();
            HttpCommand recoveryHttpCommand = new HttpCommand(recoveryListHttpCommandListener, new RecoveryShopListResponse());
            recoveryHttpCommand.sendAsJson(SeeapennyApp.getHttpHandler(), SeeapennyActivity.RECOVERY_SHOP_LIST_URL, loginJson.toString());
        } catch (JSONException jsex) {
//            activity.sendLogError("LOGIN", jsex);
        }

    }

    private void sendListShopData() {
        final List<ShopList> list = shopListService.getAllListForSynch(ShopListService.includeFilter);

        if (list.size() == 0) {
            afterSendListShopData();
            return;
        }

        final int size = list.size();

        HttpCommandListener listener = new HttpCommandListener() {

            private volatile long counter = size;

            @Override
            public void onResponse(HttpCommand command, Response response) {
                SynchronizedShopListResponse synchronizedShopListResponse = (SynchronizedShopListResponse) response;
                ShopListResponse shopListResponse = synchronizedShopListResponse.getShopListResponse();

                switch (synchronizedShopListResponse.getSynchrAction()) {
                    case INS:
                        if (shopListResponse.getOldId() > 0) {
                            Services.getListService().remove(shopListResponse.getOldId(), shopListResponse.getOwnerId());
                            Services.getGoodService().updateId(shopListResponse.getOldId(), shopListResponse.getId(), shopListResponse.getOwnerId());

                            Services.getListService().addShopList(shopListResponse.getId(), shopListResponse.getOwnerId(), shopListResponse.getName(),
                                    app.formatDatetime(shopListResponse.getCreateTime()), app.formatDatetime(shopListResponse.getLastModifiedTime()), SynchAction.NO_CHANGES);
                        } else {
                            Services.getListService().save(shopListResponse.getId(), shopListResponse.getOwnerId(), shopListResponse.getName(),
                                    app.formatDatetime(shopListResponse.getLastModifiedTime()), SynchAction.NO_CHANGES);
                        }

                        break;
                    case UPD:
                        Services.getListService().save(shopListResponse.getId(), shopListResponse.getOwnerId(), shopListResponse.getName(), app.formatDatetime(shopListResponse.getLastModifiedTime()), SynchAction.NO_CHANGES);
                        break;
                    case NIH:
                        break;
                    case DEL:
                        goodService.removeAllGoods(synchronizedShopListResponse.getListID(), synchronizedShopListResponse.getOwnerID());
                        shopListService.remove(synchronizedShopListResponse.getListID(), synchronizedShopListResponse.getOwnerID());

                        break;
                }

                counter--;

                if (counter <= 0) {
                    afterSendListShopData();
                }
            }

            @Override
            public void onError(HttpCommand command, int code, String reason) {
                SyncService.this.onError(command, code, reason);
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
        };

        for (int i = 0; i < size; i++) {
            ShopList item = list.get(i);

            switch (item.getSynchAction()) {
                case NO_CHANGES:
                    HttpCommand sendSynchronizedListNoChanges = new HttpCommand(listener, new SynchronizedShopListResponse());

                    sendSynchronizedListNoChanges.addParam(SeeapennyActivity.LIST_OWNER_ID, item.getOwnerId());
                    sendSynchronizedListNoChanges.addParam(SeeapennyActivity.LIST_ID_PARAM, item.getId());
                    sendSynchronizedListNoChanges.addParam(SeeapennyActivity.LIST_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(item.getLastModifiedTime())));

                    sendSynchronizedListNoChanges.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_SHOP_LIST_NO_CHANGES);
                    break;
                case INSERT:
                    HttpCommand sendSynchronizedListInsert = new HttpCommand(listener, new SynchronizedShopListResponse());

                    sendSynchronizedListInsert.addParam(SeeapennyActivity.LIST_NAME, item.getName());
                    sendSynchronizedListInsert.addParam(SeeapennyActivity.LIST_ID_PARAM, item.getId());
                    sendSynchronizedListInsert.addParam(SeeapennyActivity.LIST_CREATE_TIME, app.formatDatetime(app.convertToServerDate(item.getCreateTime())));
                    sendSynchronizedListInsert.addParam(SeeapennyActivity.LIST_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(item.getLastModifiedTime())));
                    sendSynchronizedListInsert.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_SHOP_LIST_INSERT);

                    break;
                case UPDATE:

                    HttpCommand sendSynchronizedListUpdate = new HttpCommand(listener, new SynchronizedShopListResponse());

                    sendSynchronizedListUpdate.addParam(SeeapennyActivity.LIST_OWNER_ID, item.getOwnerId());
                    sendSynchronizedListUpdate.addParam(SeeapennyActivity.LIST_ID_PARAM, item.getId());
                    sendSynchronizedListUpdate.addParam(SeeapennyActivity.LIST_NAME, item.getName());
                    sendSynchronizedListUpdate.addParam(SeeapennyActivity.LIST_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(item.getLastModifiedTime())));

                    sendSynchronizedListUpdate.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_SHOP_LIST_UPDATE);
                    break;
                case DELETE:
                    HttpCommand sendSynchronizedListDelete = new HttpCommand(listener, new SynchronizedShopListResponse());

                    sendSynchronizedListDelete.addParam(SeeapennyActivity.LIST_OWNER_ID, item.getOwnerId());
                    sendSynchronizedListDelete.addParam(SeeapennyActivity.LIST_ID_PARAM, item.getId());

                    sendSynchronizedListDelete.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_SHOP_LIST_DELETE);
                    break;
            }
        }
    }

    private void afterSendGoodData() {
        List<ShopList> list = shopListService.getAllListForSynch(ShopListService.includeFilter);

        SyncGoodRequest shopListRequest = new SyncGoodRequest();
        shopListRequest.setShopListResponses(list);

        try {
            JSONObject loginJson = shopListRequest.toJson();
            HttpCommand recoveryHttpCommand = new HttpCommand(recoveryGoodHttpCommandListener, new RecoveryGoodResponse());
            recoveryHttpCommand.sendAsJson(SeeapennyApp.getHttpHandler(), SeeapennyActivity.RECOVERY_GOOD_URL, loginJson.toString());
        } catch (JSONException jsex) {
//            activity.sendLogError("LOGIN", jsex);
        }
    }

    private class RecoveryListHttpCommandListener implements HttpCommandListener {

        @Override
        public void onResponse(HttpCommand command, Response response) {
            RecoveryShopListResponse recoveryShopListResponse = (RecoveryShopListResponse) response;

            List<ShopListResponse> lists = recoveryShopListResponse.getShopListResponses();
            if (lists != null) {
                for (ShopListResponse shopListResponse : lists) {

                    Date serverCreateTime = shopListResponse.getCreateTime();
                    Date serverLastModifiedTime = shopListResponse.getLastModifiedTime();

                    Date localCreateTime = app.convertToLocalDate(serverCreateTime);
                    Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

                    shopListService.addShopListRecovery(shopListResponse.getId(), shopListResponse.getOwnerId(), shopListResponse.getName(), app.formatDatetime(localCreateTime), app.formatDatetime(localLastModifiedTime), SynchAction.NO_CHANGES);
                }
            }
            sendGoodData();
        }

        @Override
        public void onError(HttpCommand command, int code, String reason) {
            SyncService.this.onError(command, code, reason);
        }

        @Override
        public void beforeSend(HttpCommand httpCommand) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onPartSent(HttpCommand httpCommand, int offset, int length) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void afterReceive(HttpCommand httpCommand) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class RecoveryGoodHttpCommandListener implements HttpCommandListener {

        @Override
        public void onResponse(HttpCommand command, Response response) {
            RecoveryGoodResponse recoveryGoodResponse = (RecoveryGoodResponse) response;

            List<GoodResponse> goodResponseList = recoveryGoodResponse.getGoodResponseList();
            if (goodResponseList != null) {
                for (GoodResponse goodResponse : goodResponseList) {

                    Date serverCreateTime = goodResponse.getCreateTime();
                    Date serverLastModifiedTime = goodResponse.getModifiedTime();

                    Date localCreateTime = app.convertToLocalDate(serverCreateTime);
                    Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);

                    goodService.insert(goodResponse.getId(), goodResponse.getListId(), goodResponse.getName(), goodResponse.getPrice(), goodResponse.getQuantity(), goodResponse.getMeasure(),
                            goodResponse.isPriority(), goodResponse.getNote(), goodResponse.getCategoryId(), goodResponse.getImageId(), goodResponse.getState(), goodResponse.getOwnerId(),
                            goodResponse.getLastEditorId(), app.formatDatetime(localCreateTime), app.formatDatetime(localLastModifiedTime), SynchAction.NO_CHANGES);
                }
            }


            app.setLastSynchTime(app.formatDate(new Date()));
            app.removeSyncIcon();

            if (app.getCurrentActivity() instanceof ShopListActivity) {
                ((ShopListActivity) app.getCurrentActivity()).refreshMenu();
            }
        }

        @Override
        public void onError(HttpCommand command, int code, String reason) {
            SyncService.this.onError(command, code, reason);
        }

        @Override
        public void beforeSend(HttpCommand httpCommand) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onPartSent(HttpCommand httpCommand, int offset, int length) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void afterReceive(HttpCommand httpCommand) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private void sendGoodData() {
        final List<Good> goodList = goodService.getAllGood();

        final int size = goodList.size();

        if (size == 0) {
            afterSendGoodData();
            return;
        }

        HttpCommandListener listener = new HttpCommandListener() {

            private volatile int counter = size;

            @Override
            public void onResponse(HttpCommand command, Response response) {
                SynchronizedGoodResponse synchronizedGoodResponse = (SynchronizedGoodResponse) response;
                GoodResponse goodResponse = synchronizedGoodResponse.getGoodResponse();
                switch (synchronizedGoodResponse.getSynchrAction()) {
                    case INS:

                        if (goodResponse.getOldId() > 0) {


                            Services.getGoodService().remove(goodResponse.getOldId(), goodResponse.getListId(), goodResponse.getOwnerId());

                            Services.getGoodService().insert(goodResponse.getId(), goodResponse.getListId(), goodResponse.getName(), goodResponse.getPrice(), goodResponse.getQuantity(),
                                    goodResponse.getMeasure(), goodResponse.isPriority(), goodResponse.getNote(), goodResponse.getCategoryId(), goodResponse.getImageId(), goodResponse.getState(),
                                    goodResponse.getOwnerId(), goodResponse.getLastEditorId(), app.formatDatetime(goodResponse.getCreateTime()), app.formatDatetime(goodResponse.getModifiedTime()), SynchAction.NO_CHANGES);

                        } else {
                            Good good = Services.getGoodService().getGoodById(goodResponse.getListId(), goodResponse.getId(), goodResponse.getOwnerId());

                            EnumSet<FieldGood> fieldGoods = FieldGood.createEmptySaveTypes();

                            good.changeName(goodResponse.getName(), fieldGoods);
                            good.changePrice(goodResponse.getPrice(), fieldGoods);
                            good.changeQuantity(goodResponse.getQuantity(), fieldGoods);
                            good.changeMeasure(goodResponse.getMeasure(), fieldGoods);
                            good.changePriority(goodResponse.isPriority(), fieldGoods);
                            good.changeNote(goodResponse.getNote(), fieldGoods);
                            good.changeCategory(goodResponse.getCategoryId(), fieldGoods);
                            good.changeImageID(goodResponse.getImageId(), fieldGoods);
                            good.changeLastEditor(goodResponse.getLastEditorId(), fieldGoods);

                            good.setModifiedTime(goodResponse.getModifiedTime());
                            good.setSynchAction(SynchAction.NO_CHANGES);

                            Services.getGoodService().save(fieldGoods, good);
                        }
                        break;
                    case UPD:

                        EnumSet<FieldGood> fields = FieldGood.createEmptySaveTypes();
                        Good good = Services.getGoodService().getGoodById(goodResponse.getListId(), goodResponse.getId(), goodResponse.getOwnerId());

                        good.changeName(goodResponse.getName(), fields);
                        good.changeCategory(goodResponse.getCategoryId(), fields);
                        good.changePrice(goodResponse.getPrice(), fields);
                        good.changeState(goodResponse.getState(), fields);
                        good.changeQuantity(goodResponse.getQuantity(), fields);
                        good.changeMeasure(goodResponse.getMeasure(), fields);
                        good.changePriority(goodResponse.isPriority(), fields);
                        good.changeNote(goodResponse.getNote(), fields);
                        good.changeLastEditor(goodResponse.getLastEditorId(), fields);
                        good.changeImageID(goodResponse.getImageId(), fields);

                        Date serverLastModifiedTime = good.getModifiedTime();
                        Date localLastModifiedTime = app.convertToLocalDate(serverLastModifiedTime);


                        good.setModifiedTime(localLastModifiedTime);
                        fields.add(FieldGood.MODIFIED_TIME);

                        good.setSynchAction(SynchAction.NO_CHANGES);
                        fields.add(FieldGood.SYNCH_ACTION);

                        goodService.save(fields, good);

                        break;
                    case NIH:
                        break;
                    case DEL:
                        goodService.remove(goodResponse.getId(), goodResponse.getListId(), goodResponse.getOwnerId());
                        break;
                }

                counter--;

                if (counter <= 0) {
                    afterSendGoodData();
                }
            }

            @Override
            public void onError(HttpCommand command, int code, String reason) {
                SyncService.this.onError(command, code, reason);
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
        };


        for (int i = 0; i < size; i++) {
            Good good = goodList.get(i);

            switch (good.getSynchAction()) {
                case NO_CHANGES:

                    if (good.getOwnerId() == app.getOwnerID()) {
                        HttpCommand sendSynchronizedGoodNoChanges = new HttpCommand(listener, new SynchronizedGoodResponse());
                        sendSynchronizedGoodNoChanges.addParam(SeeapennyActivity.LIST_OWNER_ID, good.getOwnerId());
                        sendSynchronizedGoodNoChanges.addParam(SeeapennyActivity.GOOD_LIST_ID, good.getListId());
                        sendSynchronizedGoodNoChanges.addParam(SeeapennyActivity.GOOD_ID_PARAM, good.getId());
                        sendSynchronizedGoodNoChanges.addParam(SeeapennyActivity.GOOD_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(good.getModifiedTime())));

                        sendSynchronizedGoodNoChanges.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_GOOD_NO_CHANGES);
                    } else {
                        //todo расшаренный товар
                    }
                    break;
                case INSERT:

                    if (good.getOwnerId() == app.getOwnerID()) {
                        HttpCommand sendSynchronizedGoodInsert = new HttpCommand(listener, new SynchronizedGoodResponse());

                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.LIST_OWNER_ID, good.getOwnerId());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_ID_PARAM, good.getId());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_LIST_ID, good.getListId());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_NAME, good.getName());

                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_CATEGORY, good.getCategoryId());

                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_PRICE, good.getPrice());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_STATE, good.getState());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_QUANTITY, good.getQuantity());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_MEASURE, good.getMeasure());

                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_IMAGE_ID, good.getImageId());

                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_PRIORITY, good.isPriority());
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_NOTE, good.getNote());

                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_CREATE_TIME, app.formatDatetime(app.convertToServerDate(good.getCreateTime())));
                        sendSynchronizedGoodInsert.addParam(SeeapennyActivity.GOOD_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(good.getModifiedTime())));


                        sendSynchronizedGoodInsert.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_GOOD_INSERT);
                    } else {
                        //todo расшаренный товар
                    }
                    break;
                case UPDATE:
                    if (good.getOwnerId() == app.getOwnerID()) {
                        HttpCommand sendSynchronizedGoodUpdate = new HttpCommand(listener, new SynchronizedGoodResponse());


                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.LIST_OWNER_ID, good.getOwnerId());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_ID_PARAM, good.getId());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_LIST_ID, good.getListId());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_NAME, good.getName());

                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_CATEGORY, good.getCategoryId());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_PRICE, good.getPrice());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_STATE, good.getState());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_QUANTITY, good.getQuantity());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_MEASURE, good.getMeasure());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_IMAGE_ID, good.getImageId());

                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_PRIORITY, good.isPriority());
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_NOTE, good.getNote());

                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_CREATE_TIME, app.formatDatetime(app.convertToServerDate(good.getCreateTime())));
                        sendSynchronizedGoodUpdate.addParam(SeeapennyActivity.GOOD_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(good.getModifiedTime())));


                        sendSynchronizedGoodUpdate.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_GOOD_UPDATE);
                    } else {
                        //todo расшаренный товар
                    }
                    break;
                case DELETE:
                    if (good.getOwnerId() == app.getOwnerID()) {
                        HttpCommand sendSynchronizedGoodDelete = new HttpCommand(listener, new SynchronizedGoodResponse());

                        sendSynchronizedGoodDelete.addParam(SeeapennyActivity.LIST_OWNER_ID, good.getOwnerId());
                        sendSynchronizedGoodDelete.addParam(SeeapennyActivity.GOOD_LIST_ID, good.getListId());
                        sendSynchronizedGoodDelete.addParam(SeeapennyActivity.GOOD_ID_PARAM, good.getId());
                        sendSynchronizedGoodDelete.addParam(SeeapennyActivity.GOOD_MODIFIED_TIME, app.formatDatetime(app.convertToServerDate(good.getModifiedTime())));

                        sendSynchronizedGoodDelete.sendAsForm(SeeapennyApp.getHttpHandler(), SeeapennyActivity.SYNCHRONIZED_GOOD_DELETE);
                    } else {
                        //todo расшаренный товар
                    }

                    break;
            }
        }
    }

    public void onError(HttpCommand command, int code, String reason) {
        if (app.getCurrentActivity() instanceof ShopListActivity) {
            ((ShopListActivity) (app.getCurrentActivity())).notifySyncError(code, reason);
        }
    }
}
