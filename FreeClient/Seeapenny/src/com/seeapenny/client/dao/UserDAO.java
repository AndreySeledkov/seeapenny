package com.seeapenny.client.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.server.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    static final String USER_ID = "_id";
    static final String USER_NAME = "name";
    static final String SMALL_URL = "small_url";
    static final String LARGE_URL = "large_url";
    static final String ONLINE_STATUS = "online_status";
    static final String LAST_VISIT = "last_visit";
    static final String USER_BLOCKED = "user_blocked";
    static final String USER_DELETED = "user_deleted";

    public static String TABLE_NAME = "users";
    public static String BASE_TABLE_QUERY = " (_id integer primary key,name text,small_url text," +
            "large_url text,online_status text,last_visit text,user_blocked integer,user_deleted integer);";
    private SeeapennyApp app;

    public UserDAO() {
        this.app = SeeapennyApp.getInstance();
    }

    public User read(Cursor c) {
        int userIdColIndex = c.getColumnIndex(USER_ID);
        int userNameColIndex = c.getColumnIndex(USER_NAME);
        int smallUrlColIndex = c.getColumnIndex(SMALL_URL);
        int largeUrlColIndex = c.getColumnIndex(LARGE_URL);
        int onlineStatusColIndex = c.getColumnIndex(ONLINE_STATUS);
        int lastVisitColIndex = c.getColumnIndex(LAST_VISIT);
        int userBlockedColIndex = c.getColumnIndex(USER_BLOCKED);

        int userDeletedColIndex = c.getColumnIndex(USER_DELETED);

        User user = new User();
        user.setId(c.getLong(userIdColIndex));
        user.setName(c.getString(userNameColIndex));
        user.setSmallUrl(c.getString(smallUrlColIndex));
        user.setLargeUrl(c.getString(largeUrlColIndex));
        user.setOnlineStatus(c.getString(onlineStatusColIndex));
        user.setLastVisit(c.getString(lastVisitColIndex));
        user.setUserBlocked(c.getInt(userBlockedColIndex) > 0);
        user.setUserDeleted(c.getInt(userDeletedColIndex) > 0);

        return user;
    }

    public void insert(User user) {

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        final DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, TABLE_NAME);

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_ID, user.getId());
            contentValues.put(USER_NAME, user.getName());
            contentValues.put(SMALL_URL, user.getSmallUrl());
            contentValues.put(LARGE_URL, user.getLargeUrl());
            contentValues.put(ONLINE_STATUS, user.getOnlineStatus());
            contentValues.put(LAST_VISIT, user.getLastVisit());
            contentValues.put(USER_BLOCKED, user.isUserBlocked() ? 1 : 0);
            contentValues.put(USER_DELETED, user.isUserDeleted() ? 1 : 0);

            ih.replace(contentValues);

            contentValues.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ih.close();
        db.close();
        app.getDbHelper().close();
    }

    public User getUser(long userID) {
        User user = null;

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();

        String select = "SELECT * FROM users WHERE _id=" + userID;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToFirst()) {
            do {
                user = read(c);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        app.getDbHelper().close();
        return user;
    }

    public List<User> getShareUsers(long listID) {

        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        List<User> shareUserList = new ArrayList<User>();


        String select = "SELECT * FROM users WHERE list_id==" + listID;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToFirst()) {
            do {
                User user = read(c);
                shareUserList.add(user);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        app.getDbHelper().close();
        return shareUserList;
    }

    public boolean removeAll() {
        SQLiteDatabase db = app.getDbHelper().getWritableDatabase();
        int index = db.delete(TABLE_NAME, null, null);
        db.close();
        app.getDbHelper().close();

        return index > 0;
    }
}
