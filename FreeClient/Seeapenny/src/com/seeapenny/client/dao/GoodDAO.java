package com.seeapenny.client.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.adapter.Good;
import com.seeapenny.client.util.FieldGood;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class GoodDAO {

    static final String ID = "_id";
    static final String LIST_ID = "list_id";
    static final String NAME = "name";
    static final String MEASURE = "measure";
    static final String MODIFIED_TIME = "modified_time";

    static final String PRICE = "price";
    static final String OWNER_ID = "owner_id";
    static final String LAST_EDITOR_ID = "last_editor_id";

    static final String STATE = "state";
    static final String CREATE_TIME = "create_time";
    static final String QUANTITY = "quantity";
    static final String CATEGORY = "category_id";
    static final String NOTE = "note";
    static final String PRIORITY = "priority";

    static final String IMAGE_ID = "image_id";

    static final String SYNCH_ACTION = "synch_action";

    public static String TABLE_NAME = "good";

    public static String TABLE_SHARE_NAME = "good_share";

    static final String GOOD_SHARE_ID = "list_id";
    static final String LIST_SHARE_ID = "list_id";
    static final String FILED_SHARE = "list_id";
    static final String MODIFIED_TIME_SHARE = "list_id";


    public static String BASE_TABLE_QUERY = " (_id integer,list_id integer,owner_id integer," +
            "name text,image_id integer,measure integer,modified_time text," +
            "price integer,last_editor_id integer,state integer,create_time text," +
            "quantity integer,category_id integer,note text,priority integer,synch_action integer,primary key(_id,list_id,owner_id));";

    public static String BASE_SHARE_TABLE_QUERY = " (good_id integer,list_id integer," +
            "field integer,modified_time text);";


    private SeeapennyApp app;

    public GoodDAO() {
        this.app = SeeapennyApp.getInstance();
    }

    public Good read(Cursor c) {
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


        int synchActionColIndex = c.getColumnIndex(SYNCH_ACTION);

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
        good.setSynchAction(SynchAction.valueOf(c.getInt(synchActionColIndex)));

        good.setImageId(c.getInt(imageIdColIndex));
        good.setCreateTime(app.formatDatetime(c.getString(createTimeColIndex)));
        good.setModifiedTime(app.formatDatetime(c.getString(modifiedTimeColIndex)));

        good.setState(c.getInt(stateColIndex));

        return good;
    }

    public boolean save(EnumSet<FieldGood> fields, Good good) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();


        for (FieldGood field : fields) {
            switch (field) {
                case NAME:
                    cv.put(NAME, good.getName());
                    break;
                case LAST_EDITOR_ID:
                    cv.put(LAST_EDITOR_ID, good.getLastEditorId());
                    break;
                case MEASURE:
                    cv.put(MEASURE, good.getMeasure());
                    break;
                case MODIFIED_TIME:
                    cv.put(MODIFIED_TIME, app.formatDatetime(good.getModifiedTime()));
                    break;
                case CREATE_TIME:
                    break;
                case IMAGE:
                    cv.put(IMAGE_ID, good.getImageId());
                    break;
                case CATEGORY:
                    cv.put(CATEGORY, good.getCategoryId());
                    break;
                case NOTE:
                    cv.put(NOTE, good.getNote());
                    break;
                case QUANTITY:
                    cv.put(QUANTITY, good.getQuantity());
                    break;
                case PRICE:
                    cv.put(PRICE, good.getPrice());
                    break;
                case PRIORITY:
                    cv.put(PRIORITY, good.isPriority() ? 1 : 0);
                    break;
                case STATE:
                    cv.put(STATE, good.getState());
                    break;
                case SYNCH_ACTION:
                    cv.put(SYNCH_ACTION, good.getSynchAction().ordinal());
                    break;
                default:
            }
        }

        int index = db.update(TABLE_NAME, cv, "_id = ? AND list_id=? AND owner_id=" + good.getOwnerId(),
                new String[]{String.valueOf(good.getId()), String.valueOf(good.getListId())});
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }

    public Good addGood(long id, long listId, String name, double price, double quantity, int measure, boolean priority, String note, int categoryId,
                        long imageId, int state, long ownerId, long lastEditorId, String createTime, String modifiedTime, SynchAction synchAction) {
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
        good.setSynchAction(synchAction);

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
            contentValues.put(SYNCH_ACTION, synchAction.getId());


            db.insertOrThrow(TABLE_NAME, null, contentValues);
            contentValues.clear();
        } catch (Exception e) {
            e.printStackTrace();
            good = null;
        }

        db.close();
        app.getDbHelper().close();

        return good;
    }


    public List<Good> getAllGood() {

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        List<Good> allGoodsList = new ArrayList<Good>();

        String select = "SELECT * FROM good ";
        Cursor c = db.rawQuery(select, null);

        if (c.moveToFirst()) {
            do {
                Good user = read(c);
                allGoodsList.add(user);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        app.getDbHelper().close();

        return allGoodsList;
    }

//    public ArrayList<Good> getGoods(GoodFilter filter, long listId, long ownerId) {
//        ArrayList<Good> goods = new ArrayList<Good>();
//        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
//
//        StringBuilder builder = new StringBuilder("SELECT * FROM good WHERE list_id=").append(listId)
//                .append(" AND owner_id=").append(ownerId);
//
//        if (filter.isShowInserted()) {
//            builder.append(" AND synch_action=").append(SynchAction.INSERT.getId());
//        }
//
//        if (filter.isShowInserted() && filter.isShowDeleted()) {
//            builder.append(" AND ");
//        }
//
//        if (filter.isShowDeleted()) {
//            builder.append("synch_action=").append(SynchAction.DELETE.getId());
//        }
//
//        Cursor c = db.rawQuery(builder.toString(), null);
//        if (c.moveToFirst()) {
//            do {
//                Good good = read(c);
//                goods.add(good);
//            } while (c.moveToNext());
//        }
//        c.close();
//        db.close();
//        app.getDbHelper().close();
//
//        return goods;
//    }


    public long getLastId(long listId, long ownerId) {
        long lastId = 0;

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String sql = "SELECT MAX(_id) FROM good WHERE list_id=" + listId + " AND owner_id=" + ownerId;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            lastId = c.getInt(0);
        }

        c.close();
        db.close();
        app.getDbHelper().close();

        return lastId;
    }

    public Good getGoodById(long listId, long goodId, long ownerId) {
        Good good = null;

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String sql = "SELECT * FROM good WHERE _id=" + goodId + " AND list_id=" + listId + " AND owner_id=" + ownerId;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            good = read(c);
        }

        c.close();
        db.close();
        app.getDbHelper().close();

        return good;
    }

    public ArrayList<Good> getAllGoodByCategoryId(long ownerId, long categoryId) {
        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String sql = "SELECT * FROM good WHERE owner_id=" + ownerId + " AND category_id=" + categoryId;

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Good good = read(c);
                goods.add(good);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        app.getDbHelper().close();

        return goods;
    }

    public ArrayList<Good> getAllGoodByListId(long listId, long ownerId) {
        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String sql = "SELECT * FROM good WHERE list_id=" + listId + " AND owner_id=" + ownerId + " AND synch_action!=" + SynchAction.DELETE.getId();

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Good good = read(c);
                goods.add(good);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        app.getDbHelper().close();


        return goods;

    }

    public boolean remove(long goodId, long listId, long ownerId) {

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(TABLE_NAME, "_id = " + goodId + " AND list_id=" + listId + " AND owner_id=" + ownerId, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }

    public boolean removeAllGoods(long listId, long ownerId) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(TABLE_NAME, "list_id = " + listId + " AND owner_id=" + ownerId, null);
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

    public boolean updateId(long oldListId, long newListId, long ownerId) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(LIST_ID, newListId);

        String strFilter = "list_id = " + oldListId + " AND owner_id = " + ownerId;

        int index = db.update(TABLE_NAME, cv, strFilter, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }

    public void updateEmptyOwnerId(long ownerId) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(OWNER_ID, ownerId);

        String strFilter = "owner_id=0";

        db.update(TABLE_NAME, cv, strFilter, null);
        db.close();
        app.getDbHelper().close();
    }

}
