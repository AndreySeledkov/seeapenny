package com.seeapenny.client.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sony
 * Date: 04.03.13
 * Time: 0:22
 * To change this template use File | Settings | File Templates.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static long userID;
    private static String DATA_BASE_NAME = "seeapenny.db";
    public static String DATA_BASE_PATH;
    private static final String CREATE_TABLE = "create table ";
    private static final String CREATE_TABLE_IF_NOT_EXIST = "create table if not exists ";


    private static int VERSION_DATA_BASE = 1;
    public static HashMap<String, String> createTableQuerys = new HashMap<String, String>();


    public static void initCreateTablesQuerys() {

        createTableQuerys.clear();
        createTableQuerys.put(GoodDAO.TABLE_NAME, GoodDAO.BASE_TABLE_QUERY);
        createTableQuerys.put(GoodDAO.TABLE_SHARE_NAME, GoodDAO.BASE_SHARE_TABLE_QUERY);
        createTableQuerys.put(ShopListDAO.TABLE_NAME, ShopListDAO.BASE_TABLE_QUERY);
        createTableQuerys.put(UserDAO.TABLE_NAME, UserDAO.BASE_TABLE_QUERY);
        createTableQuerys.put(AccessDAO.TABLE_NAME, AccessDAO.BASE_TABLE_QUERY);

        createTableQuerys.put(HistoryDAO.HISTORY_LIST_TABLE, HistoryDAO.HISTORY_LIST_TABLE_QUERY);
        createTableQuerys.put(HistoryDAO.GOOD_TABLE_NAME, HistoryDAO.GOOD_TABLE_QUERY);

    }

//    public static void checkDB() {
//        if ("".equals(UsersDAO.TABLE_NAME) || "".equals(MessagesDAO.TABLE_NAME) || "".equals(NewsDAO.TABLE_NAME)) {
//            initCreateTablesQuerys();
//        }
//    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);    //To change body of overridden methods use File | Settings | File Templates.
//        checkDB();

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + ShopListDAO.TABLE_NAME + ShopListDAO.BASE_TABLE_QUERY);

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + GoodDAO.TABLE_NAME + GoodDAO.BASE_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + GoodDAO.TABLE_SHARE_NAME + GoodDAO.BASE_SHARE_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + UserDAO.TABLE_NAME + UserDAO.BASE_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + AccessDAO.TABLE_NAME + AccessDAO.BASE_TABLE_QUERY);

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + HistoryDAO.HISTORY_LIST_TABLE + HistoryDAO.HISTORY_LIST_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + HistoryDAO.GOOD_TABLE_NAME + HistoryDAO.GOOD_TABLE_QUERY);

    }

    public DbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, VERSION_DATA_BASE);
        DATA_BASE_PATH = context.getExternalCacheDir().getAbsolutePath() + "/" + DATA_BASE_NAME;

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATA_BASE_PATH, null);

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + ShopListDAO.TABLE_NAME + ShopListDAO.BASE_TABLE_QUERY);

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + GoodDAO.TABLE_NAME + GoodDAO.BASE_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + GoodDAO.TABLE_SHARE_NAME + GoodDAO.BASE_SHARE_TABLE_QUERY);

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + UserDAO.TABLE_NAME + UserDAO.BASE_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + AccessDAO.TABLE_NAME + AccessDAO.BASE_TABLE_QUERY);

        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + HistoryDAO.HISTORY_LIST_TABLE + HistoryDAO.HISTORY_LIST_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_IF_NOT_EXIST + HistoryDAO.GOOD_TABLE_NAME + HistoryDAO.GOOD_TABLE_QUERY);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        checkDB();


//        db = SQLiteDatabase.openOrCreateDatabase(DATA_BASE_PATH, null);
//
//        db.execSQL(CREATE_TABLE + GoodDAO.TABLE_NAME + GoodDAO.BASE_TABLE_QUERY);
//        db.execSQL(CREATE_TABLE + ShopListDAO.TABLE_NAME + ShopListDAO.BASE_TABLE_QUERY);
//        db.execSQL(CREATE_TABLE + CategoriesDAO.TABLE_NAME + CategoriesDAO.BASE_TABLE_QUERY);
//        db.execSQL(CREATE_TABLE + UserDAO.TABLE_NAME + UserDAO.BASE_TABLE_QUERY);

//        for (String createTableQuery : createTableQuerys.values()) {
//            db.execSQL(createTableQuery);
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        checkDB();
        db.execSQL("DROP TABLE IF EXISTS " + ShopListDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoodDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AccessDAO.TABLE_NAME);

        db.execSQL("DROP TABLE IF EXISTS " + HistoryDAO.HISTORY_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDAO.GOOD_TABLE_NAME);

        onCreate(db);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openOrCreateDatabase(DbHelper.DATA_BASE_PATH, null);

//        return super.getWritableDatabase();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
