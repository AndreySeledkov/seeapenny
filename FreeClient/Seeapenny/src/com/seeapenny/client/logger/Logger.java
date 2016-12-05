package com.seeapenny.client.logger;

import android.util.Log;

public class Logger {

    public static final String TAG = "##### PF";
    public static String versionInfo;

    public static void trace(String str) {
        Log.i(TAG, str);
    }

    public static void error(Throwable ex) {
        error("Error", ex);
    }

    public static void error(String str, Throwable ex) {
        error(str, ex, true);
    }

    public static void error(String str, Throwable ex, boolean sendLogToServer) {
        Log.e(TAG, str, ex);

        if (sendLogToServer) {
//            ErrorLog errorLog = new ErrorLog();
//            errorLog.setAndroid("" + Build.VERSION.RELEASE);
//            errorLog.setPhone("" + Build.BRAND + " | " + Build.DEVICE + " | " + Build.MODEL);
//            errorLog.setVersionInfo(versionInfo);
//            errorLog.setMessage(str);
//
//
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(ex.getMessage()).append("\n");
//
//            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
//                stringBuilder.append(stackTraceElement.toString()).append("\n");
//            }
//            errorLog.setStack(stringBuilder.toString());

//            WebClientSingleton.getInstance().request(new WebRequest(WebClient.ERROR_LOG_SERVICE, errorLog));
        }
    }

    public static void error(String msg, String str) {
        str = str.replace('<', '[');
        str = str.replace('>', ']');
        Log.e(TAG, str);
//        ErrorLog errorLog = new ErrorLog();
//        errorLog.setAndroid("" + Build.VERSION.RELEASE);
//        errorLog.setPhone("" + Build.BRAND + " | " + Build.DEVICE + " | " + Build.MODEL);
//        errorLog.setMessage(msg);
//        errorLog.setStack(str);
//        errorLog.setVersionInfo(versionInfo);

//        WebClientSingleton.getInstance().request(new WebRequest(WebClient.ERROR_LOG_SERVICE, errorLog));
    }
}
