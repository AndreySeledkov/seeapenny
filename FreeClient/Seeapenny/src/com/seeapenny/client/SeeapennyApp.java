package com.seeapenny.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.seeapenny.client.activity.SeeapennyActivity;
import com.seeapenny.client.dao.DbHelper;
import com.seeapenny.client.http.HttpHandler;
import com.seeapenny.client.server.*;
import com.seeapenny.client.server.requests.LoginRequest;
import com.seeapenny.client.sound.SoundPlayer;
import com.seeapenny.client.sound.SoundPlayerFactory;
import com.seppius.i18n.plurals.PluralResources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class SeeapennyApp extends Application {


    public static final String LOG_TAG = SeeapennyApp.class.getSimpleName();

    public static final boolean DEBUG = false;
    private static final boolean STRICT_MODE = false;

    public static final ThreadSafeDateFormat DATE_FORMATTER = new ThreadSafeDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final ThreadSafeDateFormat DATE_TIME_FORMATTER = new ThreadSafeDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");

    private static final int SYNC_NOTIFY_ID = -2;

    private static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = 30 * DAY;
    private static final long YEAR = 365 * DAY;

    private static final String UID_PREF = "uid";

    private static final String MARKET_REFERRER_PREF = "marketReferrer";
    private static final String ORDER_PREF = "order_";
    private static final String INSTALL_PREF = "install";
    private static final String NUM_GS_TIPS_PREF = "numGallerySwipeTips";
    private static final String NUM_CA_TIPS_PREF = "numChatAudioTips";

    private static final String SOUND = "soundPref";
    private static final String VIBRATE = "vibratePref";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_POSITION = "currency_position";
    private static final String ROTATION = "rotationPref";
    private static final String SCREEN = "screenPref";
    private static final String SORT_ASC = "sortAscPref";


    private static final String IGNORE_BOUGHT_GOOD = "ignoreBoughtGoodPref";

    private static final String VOICE_RECOGNITION = "voiceRecognitionPref";
    private static final String LANGUAGE_POSITION = "languagePref";

    private static final String GOOD_SORT = "goodSortPref";

    private static final String PRODUCT_IMAGE = "showInProductImage";
    private static final String PRODUCT_NOTE = "showInProductNote";

    private static final String GOOD_DUPLICATE = "goodDuplicate";

    private static final String IS_SHOWN_GUIDE_DIALOG = "isShowGuideDialog";
    private static final String REMOVE_SHOPLIST = "removeShopList";

    private static final String LOGIN_REQUEST = "loginRequest";

    private static final String LAST_SYNC_TIME = "lastSynchTime";

    private static volatile SeeapennyApp instance;

    private SeeapennyActivity currentActivity;

    private HttpHandler httpHandler;
    private CacheManager cacheManager;
    private LongPollManager pollManager;
    private SharedPreferences prefs;

    private long maxImageSquare;
    private String versionName;
    private String userAgent;

    protected SoundPlayer soundPlayer = SoundPlayerFactory.createSoundPlayer();

    private Locale myLocale;

    private LoginRequest loginRequest;

    private PhotoChooser photoChooser;

    private Resources res;
    private PluralResources pluralRes;
    private LruCache<Integer, String> minuteCache = new LruCache<Integer, String>(59);
    private LruCache<Integer, String> hourCache = new LruCache<Integer, String>(23);
    private LruCache<Integer, String> dayCache = new LruCache<Integer, String>(6);
    private LruCache<Integer, String> weekCache = new LruCache<Integer, String>(3);

    private Vibrator vibrator;

    private AlarmManagerBroadcastReceiver goodListNotificationBroadcastReceiver;


    private AppState appState;
    private boolean cookiesLoaded;

    private String hashSecret;

    private DbHelper dbHelper;

    private PopupNotificationManager popupNotificationManager;

    private boolean synchronization;

    @Override
    public void onCreate() {
        if (STRICT_MODE) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder();
            threadPolicyBuilder.detectAll().penaltyLog();
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());

            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder();
            vmPolicyBuilder.detectAll().penaltyLog().penaltyDeath();
            StrictMode.setVmPolicy(vmPolicyBuilder.build());

            // StrictMode.enableDefaults();
        }
        super.onCreate();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            versionName = "0.0.0";
        }
        userAgent = "AndroidSeeapenny/" + versionName;

        Runtime runtime = Runtime.getRuntime();
        long heapSize = runtime.maxMemory();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        long amHeapSize = am.getMemoryClass() * 1024 * 1024;
        if (amHeapSize < heapSize) {
            heapSize = amHeapSize;
        }

        double memKoef = heapSize / 1024 / 1024 / 16.0;
        maxImageSquare = (long) (memKoef * 1.2 * 1000000); // memKoef * 1.2Mpx

        instance = this;
        httpHandler = new HttpHandler();
        int cacheSize = (int) (heapSize * 0.4);
        int maxNumImageDownloaders = 2;
        if (Build.VERSION.SDK_INT >= 15) {
            maxNumImageDownloaders = runtime.availableProcessors() * 2;
        }
        cacheManager = new CacheManager(cacheSize, maxNumImageDownloaders);
        cacheManager.init();

        pollManager = new LongPollManager();
        dbHelper = new DbHelper(this);

        prefs = getSharedPreferences(SeeapennyApp.class.getSimpleName(), MODE_PRIVATE);

        res = getResources();
        try {
            pluralRes = new PluralResources(res);
        } catch (SecurityException sex) {
            sex.printStackTrace();
        } catch (NoSuchMethodException nsmex) {
            nsmex.printStackTrace();
        }

        photoChooser = new PhotoChooser();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        goodListNotificationBroadcastReceiver = new AlarmManagerBroadcastReceiver();

        int languagePosition = loadLocale();
        if (languagePosition == -1) {
            Locale locale = Locale.getDefault();

            if ("en".equalsIgnoreCase(locale.getLanguage())) {
                languagePosition = 0;
            } else if ("ru".equalsIgnoreCase(locale.getLanguage())) {
                languagePosition = 1;
            }
            saveLocalePosition(languagePosition);
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simCountryIso = telephonyManager.getSimCountryIso().toLowerCase();

        if ("-1".equals(getCurrency())) {
            if (simCountryIso == null || "".equals(simCountryIso)) {
                String language = Locale.getDefault().getLanguage();
                if (language.equals("en")) {
                    setCurrency("$");
                    setCurrencyPosition(2);
                } else if (language.equals("ua")) {
                    setCurrency("грн.");
                    setCurrencyPosition(1);
                } else {
                    setCurrency("руб.");
                    setCurrencyPosition(0);
                }
            } else if ("RU".equalsIgnoreCase(simCountryIso) || "RUS".equalsIgnoreCase(simCountryIso)) {
                setCurrency("руб.");
                setCurrencyPosition(0);
            } else if ("US".equalsIgnoreCase(simCountryIso) || "USA".equalsIgnoreCase(simCountryIso)) {
                setCurrency("$");
                setCurrencyPosition(2);
            } else if ("UA".equalsIgnoreCase(simCountryIso) || "UAH".equalsIgnoreCase(simCountryIso)) {
                setCurrency("грн.");
                setCurrencyPosition(1);
            }
        }

        hashSecret = res.getString(R.string.hash_secret);
    }


    @Override
    public void onTerminate() {
        httpHandler = null;
        instance = null;

        super.onTerminate();
    }

    public AlarmManagerBroadcastReceiver getGoodListNotificationBroadcastReceiver() {
        return goodListNotificationBroadcastReceiver;
    }

    public AppState getAppState() {
        if (appState == null) {
            appState = new AppState();
        }
        return appState;
    }


    public Vibrator getVibrator() {
        return vibrator;
    }

    @Override
    public void onLowMemory() {
        cacheManager.clear();
    }

    public static SeeapennyApp getInstance() {
        return instance;
    }

    public static HttpHandler getHttpHandler() {
        return instance.httpHandler;
    }

    public static CacheManager getCacheManager() {
        return instance.cacheManager;
    }

    public static String versionName() {
        return instance.versionName;
    }

    public static String userAgent() {
        return instance.userAgent;
    }

    public static long getMaxImageSquare() {
        return instance.maxImageSquare;
    }

    public static Resources getAppResources() {
        return instance.res;
    }

    public static PopupNotificationManager getPopupNotificationManager() {
        return instance.popupNotificationManager;
    }

    public static PhotoChooser getPhotoChooser() {
        return instance.photoChooser;
    }


    public void setSound(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(SOUND, flag);
        editor.commit();
    }

    public boolean isSound() {
        return prefs.getBoolean(SOUND, true);
    }

    public void setVibrate(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(VIBRATE, flag);
        editor.commit();
    }

    public void setCurrencyPosition(int currencyPosition) {
        Editor editor = prefs.edit();
        editor.putInt(CURRENCY_POSITION, currencyPosition);
        editor.commit();
    }

    public int getCurrencyPosition() {
        return prefs.getInt(CURRENCY_POSITION, -1);
    }

    public void setCurrency(String currency) {
        Editor editor = prefs.edit();
        editor.putString(CURRENCY, currency);
        editor.commit();
    }

    public String getCurrency() {
        return prefs.getString(CURRENCY, "-1");
    }

    public boolean getRotation() {
        return prefs.getBoolean(ROTATION, true);
    }

    public void setRotation(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(ROTATION, flag);
        editor.commit();
    }

    public void setIgnoreBoughtGood(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(IGNORE_BOUGHT_GOOD, flag);
        editor.commit();
    }

    public boolean isIgnoreBoughtGood() {
        return prefs.getBoolean(IGNORE_BOUGHT_GOOD, true);
    }

    public void setSortAsc(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(SORT_ASC, flag);
        editor.commit();
    }

    public boolean isSortAsc() {
        return prefs.getBoolean(SORT_ASC, true);
    }

    public boolean getScreen() {
        return prefs.getBoolean(SCREEN, true);
    }

    public void setScreen(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(SCREEN, flag);
        editor.commit();
    }

    public boolean getVoiceRecognitionScreen() {
        return prefs.getBoolean(VOICE_RECOGNITION, true);
    }

    public void setVoiceRecognitionScreen(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(VOICE_RECOGNITION, flag);
        editor.commit();
    }

    public boolean getSound() {
        return prefs.getBoolean(SOUND, true);
    }

    public boolean getVibrate() {
        return prefs.getBoolean(VIBRATE, true);
    }

    public List<String> getListName() {
        List<String> strings = new ArrayList<String>();
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");
        strings.add("Абрикос");


        return strings;
    }

    public void putInListName(String newListName) {
//        Set<String> stringSet = prefs.getStringSet(LIST_NAME, new HashSet<String>());
//
//        stringSet.add(newListName);
//
//        Editor editor = prefs.edit();
//        editor.putStringSet(LIST_NAME, stringSet);
//        editor.commit();
        //todo fix android 2.1.
    }

    public void removeInListName(String removeName) {
//        Set<String> stringSet = prefs.getStringSet(LIST_NAME, new HashSet<String>());
//
//        stringSet.remove(removeName);
//
//        Editor editor = prefs.edit();
//        editor.putStringSet(LIST_NAME, stringSet);
//        editor.commit();
    }


    public boolean getProductImage() {
        return prefs.getBoolean(PRODUCT_IMAGE, true);
    }

    public boolean getProductNote() {
        return prefs.getBoolean(PRODUCT_NOTE, true);
    }

    public void setProductNote(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(PRODUCT_NOTE, flag);
        editor.commit();
    }

    public void setProductImage(boolean flag) {
        Editor editor = prefs.edit();
        editor.putBoolean(PRODUCT_IMAGE, flag);
        editor.commit();
    }


    //    public String getListSort() {
//        return sharedPrefs.getString("sortListPref", "create_time");
//    }
//


//    public long getUserID() {
//        if (getUser() != null) {
//            return getUser().getId();
//        } else {
//            return prefs.getLong(USER_ID, 0);
//        }
//    }

    public String getGoodSort() {
        return prefs.getString(GOOD_SORT, "create_time");
    }

    public void saveGoodSort(String sort) {
        Editor editor = prefs.edit();
        editor.putString(GOOD_SORT, sort);
        editor.commit();
    }

    public boolean checkDuplicateGood() {
        return prefs.getBoolean(GOOD_DUPLICATE, true);
    }

    public void saveDuplicateGood(boolean duplicate) {
        Editor editor = prefs.edit();
        editor.putBoolean(GOOD_DUPLICATE, duplicate);
        editor.commit();
    }

    public boolean isShownGuideDialog() {
        return prefs.getBoolean(IS_SHOWN_GUIDE_DIALOG, false);
    }

    public void setShownGuideDialog(boolean shown) {
        Editor editor = prefs.edit();
        editor.putBoolean(IS_SHOWN_GUIDE_DIALOG, shown);
        editor.commit();
    }

    public boolean isRemoveShoplist() {
        return prefs.getBoolean(REMOVE_SHOPLIST, true);
    }

    public void setRemoveShoplist(boolean remove) {
        Editor editor = prefs.edit();
        editor.putBoolean(REMOVE_SHOPLIST, remove);
        editor.commit();
    }


//
//    public String getCurrency() {
//        return sharedPrefs.getString("currencyPref", "NULL");
//    }

//    public boolean getAutoSynchronized() {
//        return sharedPrefs.getBoolean("synchronizdPref", true);
//    }

    private void saveUid(String uid) {
        Editor editor = prefs.edit();
        editor.putString(UID_PREF, uid);
        editor.commit();
    }

    private String loadUid() {
        return prefs.getString(UID_PREF, null);
    }


    public void saveMarketReferrer(String referrer) {
        Editor editor = prefs.edit();
        editor.putString(MARKET_REFERRER_PREF, referrer);
        editor.commit();
    }

    public String loadMarketReferrer() {
        return prefs.getString(MARKET_REFERRER_PREF, null);
    }

    public void saveInstall() {
        Editor editor = prefs.edit();
        editor.putBoolean(INSTALL_PREF, true);
        editor.commit();
    }

    public boolean loadInstall() {
        return prefs.getBoolean(INSTALL_PREF, false);
    }

    public void savePurchase(OrderInfo orderInfo) {
        Editor editor = prefs.edit();
        String orderInfoStr;
        try {
            JSONObject json = orderInfo.toJson();
            orderInfoStr = json.toString();
        } catch (JSONException jsonex) {
            orderInfoStr = orderInfo.getProductId();
        }
        editor.putString(ORDER_PREF + orderInfo.getOrderId(), orderInfoStr);
        editor.commit();
        General.commit(editor);
    }

    public int loadNumGallerySwipeTips() {
        return prefs.getInt(NUM_GS_TIPS_PREF, 0);
    }

    public void saveNumGallerySwipeTips(int numTips) {
        Editor editor = prefs.edit();
        editor.putInt(NUM_GS_TIPS_PREF, numTips);
        editor.commit();
    }


    public String uid() {
        String uid = loadUid();
        if (uid == null) {
            uid = generateUID();
            saveUid(uid);
        }
        return uid;
    }

    public String androidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String phoneNumber() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    private String generateUID() {
        String buf = "";
        Random rand = new Random();
        Date date = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);

        buf += 0 + ".";
        buf += Integer.toString(cl.get(Calendar.YEAR));
        buf += "_";
        buf += Integer.toString(cl.get(Calendar.MONTH));
        buf += "_";
        buf += Integer.toString(cl.get(Calendar.DATE));
        buf += "_";
        buf += Integer.toString(cl.get(Calendar.HOUR_OF_DAY));
        buf += "_";
        buf += Integer.toString(cl.get(Calendar.MINUTE));
        buf += "_";
        buf += Integer.toString(cl.get(Calendar.SECOND));
        buf += "_";
        buf += Math.abs(rand.nextInt());

        return buf;
    }

    public String imei() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    public String formatElapsed(Date date) {
        long now = sessionState.serverTimeNow();
        long before = date.getTime();
        long elapsed = now - before;

        String formated;
        if (elapsed > YEAR) {
            int year = (int) (elapsed / YEAR);
            formated = pluralRes.getQuantityString(R.plurals.year_format, year, year);
        } else if (elapsed > MONTH) {
            int month = (int) (elapsed / MONTH);
            formated = pluralRes.getQuantityString(R.plurals.month_format, month, month);
        } else if (elapsed > WEEK) {
            int week = (int) (elapsed / WEEK);
            formated = formatFromCache(week, R.plurals.week_format, weekCache);
        } else if (elapsed > DAY) {
            int day = (int) (elapsed / DAY);
            formated = formatFromCache(day, R.plurals.day_format, dayCache);
        } else if (elapsed > HOUR) {
            int hour = (int) (elapsed / HOUR);
            formated = formatFromCache(hour, R.plurals.hour_format, hourCache);
        } else if (elapsed > MINUTE) {
            int minute = (int) (elapsed / MINUTE);
            formated = formatFromCache(minute, R.plurals.minute_format, minuteCache);
        } else {
            formated = res.getString(R.string.second_format);
        }

        return formated;
    }

    private String formatFromCache(int value, int resId, LruCache<Integer, String> cache) {
        Integer key = Integer.valueOf(value);
        String formated = cache.get(key);
        if (formated == null) {
            formated = pluralRes.getQuantityString(resId, key, key);
            cache.put(key, formated);
        }
        return formated;
    }

    public void setLoginRequest(LoginRequest loginRequest) {
        this.loginRequest = loginRequest;

        saveObject(loginRequest, LOGIN_REQUEST);
    }

    public LoginRequest getLoginRequest() {
        if (loginRequest == null) {
            loginRequest = loadObject(LOGIN_REQUEST);
        }
        return loginRequest;
    }

    public void setLastSynchTime(String time) {
        Editor editor = prefs.edit();
        editor.putString(LAST_SYNC_TIME, time);
        editor.commit();
    }

    public String getLastSynchTime() {
        return prefs.getString(LAST_SYNC_TIME, null);
    }


    public void setCurrentActivity(SeeapennyActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    //
    public SeeapennyActivity getCurrentActivity() {
        return currentActivity;
    }

    public void startLongPolling() {
        pollManager.start();
    }

    public void restoreLongPolling() {
        pollManager.restore();
    }

    public void processLongPollQueue() {
        pollManager.processEventQueue();
    }

    public void showSyncIcon() {
        synchronization = true;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.notification_sync_text));
        builder.setSmallIcon(android.R.drawable.stat_notify_sync);
        builder.setContentIntent(contentIntent);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);


        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(SYNC_NOTIFY_ID, builder.build());
    }

    public void removeSyncIcon() {
        synchronization = false;
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(SYNC_NOTIFY_ID);
    }

    public boolean isSynchronization() {
        return synchronization;
    }

    public String getGoogleAcountEmail() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        String email = null;
        for (Account ac : accounts) {
            String name = ac.name;
            String type = ac.type;
            if (type.equals("com.google")) {
                email = name;
                break;
            }
        }
        return email;
    }

    private <S extends Serializable> void saveObject(S object, String fileName) {
        save(object, fileName);
    }

    private <S extends Serializable> void saveList(List<S> list, String fileName) {
        save(list, fileName);
    }

    private void save(Object object, String fileName) {
        // fileName = fileName + versionName;

        if (object == null) {
            File dir = getFilesDir();
            if (dir != null && dir.isDirectory()) {
                File file = new File(dir, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        } else {
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE));
                out.writeObject(object);
            } catch (NullPointerException npex) {  //openFileOutput may throw NullPointerException
                //ignore
            } catch (IOException ioex) {
                if (SeeapennyApp.DEBUG) Log.e("SeeapennyApp", "Exception", ioex);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private <S extends Serializable> S loadObject(String fileName) {
        return (S) load(fileName);
    }

    private <S extends Serializable> List<S> loadList(String fileName) {
        return (List<S>) load(fileName);
    }

    private Object load(String fileName) {
        // fileName = fileName + versionName;

        Object object = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(openFileInput(fileName));
            object = in.readObject();
        } catch (ClassNotFoundException cnfex) {
//            if (SeeapennyApp.DEBUG) Log.e("EconomyApp", "Exception", cnfex);
        } catch (IOException ioex) {
//            if (SeeapennyApp.DEBUG) Log.e("EconomyApp", "Exception", ioex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return object;
    }

    public void clearSessionStateDir() {
        File filesDir = getFilesDir();
        if (filesDir != null && filesDir.isDirectory()) {
            for (File file : filesDir.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    public File getPermanentDir() {
        File dir = null;
        File filesDir = getFilesDir();
        if (filesDir != null && filesDir.isDirectory()) {
            dir = new File(filesDir, "permanent");
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
        return dir;
    }

    public File getAudioFile(String name) {
        File file = null;
        File filesDir = getFilesDir();
        if (filesDir != null && filesDir.isDirectory()) {
            file = new File(filesDir, name);
        }
        return file;
    }

    public static String getAppLocale() {
        String language = Locale.getDefault().getLanguage();
        if ("uk".equals(language) || "be".equals(language) || "uz".equals(language) || "kk".equals(language)
                || "az".equals(language) || "hy".equals(language) || "ka".equals(language) || "ky".equals(language)
                || "tg".equals(language) || "tk".equals(language) || "mo".equals(language)) {
            return "ru";
        }

        return language;
    }

    public SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }

    public Date formatDatetime(String date) {
        try {
            return DATE_TIME_FORMATTER.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    public String formatDatetime(Date date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    public String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }


    public int loadLocale() {
        return prefs.getInt(LANGUAGE_POSITION, -1);
    }

    public void saveLocalePosition(int position) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LANGUAGE_POSITION, position);
        editor.commit();
    }

    public void changeLang(String lang, int position) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocalePosition(position);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public File getStorageDir() {
        File storageDir = null;
        if (Build.VERSION.SDK_INT >= 8) {
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            storageDir = new File(root, getString(R.string.app_name));
            storageDir.mkdirs();
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory() + "Pictures/" + getString(R.string.app_name));
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public DbHelper getDbHelper() {
        if (dbHelper == null) {
            dbHelper = new DbHelper(this);
        }
        return dbHelper;
    }


}
