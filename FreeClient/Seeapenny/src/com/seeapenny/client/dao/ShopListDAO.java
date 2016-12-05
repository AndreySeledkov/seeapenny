package com.seeapenny.client.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.adapter.ShopList;
import com.seeapenny.client.server.User;
import com.seeapenny.client.service.Services;
import com.seeapenny.client.service.ShopListFilter;

import java.util.ArrayList;


public class ShopListDAO {

    static final String ID = "_id";
    static final String OWNER_ID = "owner_id";
    static final String NAME = "name";
    static final String CREATE_TIME = "create_time";
    static final String MODIFIED_TIME = "modified_time";
    static final String SYNCH_ACTION = "synch_action";

    public static String TABLE_NAME = "list";
    public static String BASE_TABLE_QUERY = " (_id integer ,owner_id integer,name text," +
            "create_time text,modified_time text,synch_action integer,PRIMARY KEY (_id,owner_id));";
    private SeeapennyApp app;


    public ShopListDAO() {
        this.app = SeeapennyApp.getInstance();

    }

    public ShopList read(Cursor c) {
        int idColIndex = c.getColumnIndex(ID);
        int ownerIdColIndex = c.getColumnIndex(OWNER_ID);
        int nameColIndex = c.getColumnIndex(NAME);
        int createTimeColIndex = c.getColumnIndex(CREATE_TIME);
        int modifiedColIndex = c.getColumnIndex(MODIFIED_TIME);
        int recordTypeColIndex = c.getColumnIndex(SYNCH_ACTION);

        ShopList shopListResponse = new ShopList();
        shopListResponse.setId(c.getInt(idColIndex));
        shopListResponse.setOwnerId(c.getInt(ownerIdColIndex));
        shopListResponse.setName(c.getString(nameColIndex));
        shopListResponse.setSynchAction(SynchAction.valueOf(c.getInt(recordTypeColIndex)));
        shopListResponse.setCreateTime(app.formatDatetime(c.getString(createTimeColIndex)));
        shopListResponse.setLastModifiedTime(app.formatDatetime(c.getString(modifiedColIndex)));

        User ownerUser = Services.getUserService().getUser(shopListResponse.getOwnerId());
        shopListResponse.setOwnerUser(ownerUser);

        return shopListResponse;
    }

    public boolean save(long id, long ownerID, String name, String lastModifiedTime, SynchAction synchAction) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, name);
        cv.put(MODIFIED_TIME, lastModifiedTime);
        cv.put(SYNCH_ACTION, synchAction.getId());

        String strFilter = "_id = " + id + " AND owner_id = " + ownerID;

        int index = db.update(TABLE_NAME, cv, strFilter, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }


    public boolean save(ShopList shopList) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, shopList.getName());
        cv.put(MODIFIED_TIME, shopList.getFormatLastModifiedTime());
        cv.put(SYNCH_ACTION, shopList.getSynchAction().getId());

        String strFilter = "_id = " + shopList.getId() + " AND owner_id = " + shopList.getOwnerId();

        int index = db.update(TABLE_NAME, cv, strFilter, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }


//    public boolean updateId(long newID, long oldID, long ownerId) {
//        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put(ID, newID);
//
//        String strFilter = "_id=" + oldID + " AND owner_id=" + ownerId;
//
//        int index = db.update(TABLE_NAME, cv, strFilter, null);
//        db.close();
//        app.getDbHelper().close();
//
//        return index > 0;
//    }

    public void updateEmptyOwnerId(long ownerId) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(OWNER_ID, ownerId);

        String strFilter = "owner_id=0";

        db.update(TABLE_NAME, cv, strFilter, null);
        db.close();
        app.getDbHelper().close();
    }

    public ShopList addShopList(long id, long ownerId, String name, String createTime, String lastModifiedTime, SynchAction synchAction) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        ShopList shopList = new ShopList();
        shopList.setId(id);
        shopList.setOwnerId(ownerId);
        shopList.setName(name);
        shopList.setCreateTime(app.formatDatetime(createTime));
        shopList.setLastModifiedTime(app.formatDatetime(lastModifiedTime));
        shopList.setSynchAction(synchAction);

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(ID, id);
            contentValues.put(OWNER_ID, ownerId);
            contentValues.put(NAME, name);
            contentValues.put(CREATE_TIME, createTime);
            contentValues.put(MODIFIED_TIME, lastModifiedTime);
            contentValues.put(SYNCH_ACTION, synchAction.getId());

            db.insertOrThrow(TABLE_NAME, null, contentValues);
            contentValues.clear();
        } catch (Exception e) {
            e.printStackTrace();
            shopList = null;
        }

        db.close();
        app.getDbHelper().close();
        return shopList;
    }

    public ArrayList<ShopList> getAllList(ShopListFilter filter) {

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ArrayList<ShopList> allList = new ArrayList<ShopList>();

        StringBuilder builder = new StringBuilder("SELECT * FROM list ");

        if (!filter.isIncludeDeleted()) {
            builder.append("WHERE synch_action!=").append(SynchAction.DELETE.getId());
        }

        builder.append(" ORDER BY _id ASC");

        Cursor c = db.rawQuery(builder.toString(), null);

        if (c.moveToFirst()) {
            do {
                ShopList user = read(c);
                allList.add(user);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        app.getDbHelper().close();
        return allList;
    }


    public long getLastId() {
        long lastId = 0;

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, new String[]{"MAX(_id)"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            lastId = c.getInt(0);
        }

        c.close();
        db.close();
        app.getDbHelper().close();

        return lastId;
    }

    public ShopList getShopList(long listID, long ownerId) {
        ShopList shopList = null;

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String select = "SELECT * FROM list WHERE _id=" + listID + " AND owner_id=" + ownerId;
        Cursor c = db.rawQuery(select, null);
        if (c.moveToFirst()) {
            shopList = read(c);
        }

        c.close();
        db.close();
        app.getDbHelper().close();

        return shopList;
    }


    public boolean remove(long listID, long ownerID) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String strFilter = "_id = " + listID + " AND owner_id = " + ownerID;

        int index = db.delete(TABLE_NAME, strFilter, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }

    public boolean removeAll() {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(TABLE_NAME, null, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }
}
