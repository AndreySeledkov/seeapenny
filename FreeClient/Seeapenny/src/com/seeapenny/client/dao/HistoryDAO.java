package com.seeapenny.client.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.adapter.ShopList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sony on 01.08.13.
 */
public class HistoryDAO {

    static final String ID = "_id";
    static final String OWNER_ID = "owner_id";
    static final String NAME = "name";
    static final String CREATE_TIME = "create_time";
    static final String MODIFIED_TIME = "modified_time";

    public static String HISTORY_LIST_TABLE = "history_list";
    public static String HISTORY_LIST_TABLE_QUERY = " (_id integer ,owner_id integer,name text," +
            "create_time text,modified_time text,PRIMARY KEY (_id,owner_id));";
    /////---------------------------------------------------------------------------////////////////////////

    static final String GOOD_ID = "_id";
    static final String LIST_ID = "list_id";
    static final String GOOD_NAME = "name";
    static final String MEASURE = "measure";
    static final String GOOD_MODIFIED_TIME = "modified_time";

    static final String PRICE = "price";
    static final String GOOD_OWNER_ID = "owner_id";
    static final String LAST_EDITOR_ID = "last_editor_id";

    static final String STATE = "state";
    static final String GOOD_CREATE_TIME = "create_time";
    static final String QUANTITY = "quantity";
    static final String CATEGORY = "category_id";
    static final String NOTE = "note";
    static final String PRIORITY = "priority";

    static final String IMAGE_ID = "image_id";


    public static String GOOD_TABLE_NAME = "history_good";


    public static String GOOD_TABLE_QUERY = " (_id integer,list_id integer,owner_id integer," +
            "name text,image_id integer,measure integer,modified_time text," +
            "price integer,last_editor_id integer,state integer,create_time text," +
            "quantity integer,category_id integer,note text,priority integer,primary key(_id,list_id,owner_id));";


    private SeeapennyApp app;

    public HistoryDAO() {
        this.app = SeeapennyApp.getInstance();

    }

    public ShopList read(Cursor c) {
        int idColIndex = c.getColumnIndex(ID);
        int ownerIdColIndex = c.getColumnIndex(OWNER_ID);
        int nameColIndex = c.getColumnIndex(NAME);
        int createTimeColIndex = c.getColumnIndex(CREATE_TIME);
        int modifiedColIndex = c.getColumnIndex(MODIFIED_TIME);

        ShopList shopListResponse = new ShopList();
        shopListResponse.setId(c.getInt(idColIndex));
        shopListResponse.setOwnerId(c.getInt(ownerIdColIndex));
        shopListResponse.setName(c.getString(nameColIndex));
        shopListResponse.setCreateTime(app.formatDatetime(c.getString(createTimeColIndex)));
        shopListResponse.setLastModifiedTime(app.formatDatetime(c.getString(modifiedColIndex)));

        return shopListResponse;
    }

    public Good readGood(Cursor c) {
        int idColIndex = c.getColumnIndex(ID);
        int listIdColIndex = c.getColumnIndex(LIST_ID);
        int nameColIndex = c.getColumnIndex(NAME);
        int measureColIndex = c.getColumnIndex(MEASURE);
        int modifiedTimeColIndex = c.getColumnIndex(MODIFIED_TIME);

        int priceColIndex = c.getColumnIndex(PRICE);
        int ownerIdColIndex = c.getColumnIndex(OWNER_ID);
        int lastEditorIdColIndex = c.getColumnIndex(LAST_EDITOR_ID);

        int stateColIndex = c.getColumnIndex(STATE);
        int createTimeColIndex = c.getColumnIndex(CREATE_TIME);
        int quantityColIndex = c.getColumnIndex(QUANTITY);
        int categoryColIndex = c.getColumnIndex(CATEGORY);
        int noteColIndex = c.getColumnIndex(NOTE);
        int priorityColIndex = c.getColumnIndex(PRIORITY);
        int imageIdColIndex = c.getColumnIndex(IMAGE_ID);


        Good good = new Good();
        good.setId(c.getLong(idColIndex));
        good.setListId(c.getLong(listIdColIndex));
        good.setName(c.getString(nameColIndex));

        good.setOwnerId(c.getLong(ownerIdColIndex));
        good.setLastEditorId(c.getLong(lastEditorIdColIndex));
        good.setPrice(c.getInt(priceColIndex));
        good.setQuantity(c.getInt(quantityColIndex));
        good.setCategoryId(c.getInt(categoryColIndex));
        good.setMeasure(c.getInt(measureColIndex));
        good.setNote(c.getString(noteColIndex));
        good.setPriority(c.getInt(priorityColIndex) > 0);

        good.setImageId(c.getInt(imageIdColIndex));
        good.setCreateTime(app.formatDatetime(c.getString(createTimeColIndex)));
        good.setModifiedTime(app.formatDatetime(c.getString(modifiedTimeColIndex)));

        good.setState(c.getInt(stateColIndex));

//        GoodCategory goodCategory = Services.getCategoryService().getCategoryId(good.getCategoryId(),good.getOwnerId());
//        good.setGoodCategory(goodCategory);

        return good;
    }

    public ShopList addShopList(long id, long ownerId, String name, String createTime, String lastModifiedTime) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        ShopList shopList = new ShopList();
        shopList.setId(id);
        shopList.setOwnerId(ownerId);
        shopList.setName(name);
        shopList.setCreateTime(app.formatDatetime(createTime));
        shopList.setLastModifiedTime(app.formatDatetime(lastModifiedTime));

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(ID, id);
            contentValues.put(OWNER_ID, ownerId);
            contentValues.put(NAME, name);
            contentValues.put(CREATE_TIME, createTime);
            contentValues.put(MODIFIED_TIME, lastModifiedTime);

            db.insertOrThrow(HISTORY_LIST_TABLE, null, contentValues);
            contentValues.clear();
        } catch (Exception e) {
            e.printStackTrace();
            shopList = null;
        }

        db.close();
        app.getDbHelper().close();
        return shopList;
    }

    public Good addGood(long id, long listId, String name, double price, double quantity, int measure, boolean priority, String note, int categoryId,
                        long imageId, int state, long ownerId, long lastEditorId, String createTime, String modifiedTime) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        Good good = new Good();
        good.setId(id);
        good.setListId(listId);
        good.setName(name);
        good.setMeasure(measure);
        good.setModifiedTime(app.formatDatetime(modifiedTime));
        good.setPrice(price);
        good.setOwnerId(ownerId);
        good.setLastEditorId(lastEditorId);
        good.setState(state);
        good.setCreateTime(app.formatDatetime(createTime));
        good.setQuantity(quantity);
        good.setCategoryId(categoryId);
        good.setNote(note);
        good.setPriority(priority);
        good.setImageId(imageId);

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(ID, id);
            contentValues.put(LIST_ID, listId);
            contentValues.put(NAME, name);
            contentValues.put(MEASURE, measure);
            contentValues.put(MODIFIED_TIME, modifiedTime);
            contentValues.put(PRICE, price);
            contentValues.put(OWNER_ID, ownerId);
            contentValues.put(LAST_EDITOR_ID, lastEditorId);
            contentValues.put(STATE, state);
            contentValues.put(CREATE_TIME, createTime);
            contentValues.put(QUANTITY, quantity);
            contentValues.put(CATEGORY, categoryId);
            contentValues.put(NOTE, note);
            contentValues.put(PRIORITY, priority);

            contentValues.put(IMAGE_ID, imageId);


            db.insertOrThrow(GOOD_TABLE_NAME, null, contentValues);
            contentValues.clear();
        } catch (Exception e) {
            e.printStackTrace();
            good = null;
        }

        db.close();
        app.getDbHelper().close();

        return good;
    }

    public ArrayList<Good> getAllGoodByListId(long listId, long ownerId) {
        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String sql = "SELECT * FROM good WHERE list_id=" + listId + " AND owner_id=" + ownerId;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Good good = readGood(c);
                goods.add(good);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        app.getDbHelper().close();


        return goods;

    }

    public List<ShopList> getAllList() {

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        List<ShopList> allList = new ArrayList<ShopList>();


        Cursor c = db.query(HISTORY_LIST_TABLE, null, null, null, null, null, null);

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

    public boolean removeAll() {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(HISTORY_LIST_TABLE, null, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }
}
