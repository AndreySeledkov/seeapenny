package com.seeapenny.client.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.SynchAction;
import com.seeapenny.client.bean.Access;
import com.seeapenny.client.server.User;
import com.seeapenny.client.service.AccessType;

import java.util.ArrayList;
import java.util.List;

public class AccessDAO {

    static final String USER_FROM = "user_from";
    static final String USER_TO = "user_to";
    static final String SERVICE_TYPE = "type";
    static final String CONTENT_ID = "content_id";

    public static String TABLE_NAME = "access";
    public static String BASE_TABLE_QUERY = " (user_from integer,user_to integer,type integer,content_id integer,primary key (user_to,type,content_id) );";
    private SeeapennyApp app;

    public AccessDAO() {
        this.app = SeeapennyApp.getInstance();
    }

    public Access read(Cursor c) {
        int userFrom = c.getColumnIndex(USER_FROM);
        int userTo = c.getColumnIndex(USER_TO);
        int type = c.getColumnIndex(SERVICE_TYPE);
        int contentID = c.getColumnIndex(CONTENT_ID);


        Access access = new Access();
        access.setUserFrom(c.getInt(userFrom));
        access.setUserTo(c.getInt(userTo));
        access.setContentId(c.getInt(contentID));
        access.setType(AccessType.getDbValue(c.getInt(type)));

        return access;
    }

    public boolean add(long contentID, int accessType, long userFrom, long userTo) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        long index=0;
        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(USER_FROM, userFrom);
            contentValues.put(USER_TO, userTo);
            contentValues.put(SERVICE_TYPE, accessType);
            contentValues.put(CONTENT_ID, contentID);

            index=db.insertOrThrow(TABLE_NAME, null, contentValues);
            contentValues.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        app.getDbHelper().close();

        return index > 0;
    }

    public boolean remove(long contentID, int accessType, long userTo) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(TABLE_NAME, "user_to = " + userTo + ",type=" + accessType + ",content_id=" + contentID, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }


    public List<Access> getAccessEntries(AccessType accessType, long contentID) {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        List<Access> getAccessEntries = new ArrayList<Access>();

        String select = "SELECT * FROM access";
        Cursor c = db.rawQuery(select, null);

        if (c.moveToFirst()) {
            do {
                Access user = read(c);
                getAccessEntries.add(user);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        app.getDbHelper().close();

        return getAccessEntries;
    }

    public boolean removeAll() {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(TABLE_NAME, null, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }

}
