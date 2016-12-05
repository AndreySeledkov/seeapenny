package com.seeapenny.client.server.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LoginRequest implements Serializable {


    private final User user = new User();
    private final Client client = new Client();
    private final boolean registration;

    private String code;
    private String accessToken;

    private transient String hashSecret;

    public LoginRequest(boolean registration) {
        this.registration = registration;
    }

    public void setImei(String imei) {
        client.setImei(imei);
    }

    public void setPartner(String partner) {
        client.setPartner(partner);
    }

    public void setPlatform(String platform) {
        client.setPlatform(platform);
    }

    public void setUid(String uid) {
        client.setUid(uid);
    }

    public void setUserAgent(String userAgent) {
        client.setUserAgent(userAgent);
    }

    public void setPhoneNumber(String phoneNumber) {
        client.setPhoneNumber(phoneNumber);
    }

    public void setBuildId(String buildId) {
        client.setBuildId(buildId);
    }

    public void setScreenWidth(int width) {
        client.setScreenWidth(width);
    }

    public void setScreenHeight(int height) {
        client.setScreenHeight(height);
    }

    public void setLocale(String locale) {
        client.setLocale(locale);
    }

    public void setAppLocale(String appLocale) {
        client.setAppLocale(appLocale);
    }

    public void setAndroidId(String androidId) {
        client.setAndroidId(androidId);
    }

    public void setLogin(String login) {
        user.setLogin(login);
    }

    public String getLogin() {
        return user.getLogin();
    }

    public void setPassword(String passw) {
        user.setPassword(passw);
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setName(String name) {
        user.setName(name);
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setHashSecret(String hashSecret) {
        this.hashSecret = hashSecret;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject root = new JSONObject();

        if (registration) {
            root.put("reg", user.toJson());
        } else {
            root.put("user", user.toJson());
        }
        root.put("client", client.toJson());

        if (code != null) {
            root.put("code", code);
        }

        if (accessToken != null) {
            root.put("accessToken", accessToken);
        }

        return root;
    }

    public static class User implements Serializable {

        private String login;
        private String password;
        private String name;

        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public void setName(String name) {
            this.name = name;
        }


        public JSONObject toJson() throws JSONException {
            JSONObject root = new JSONObject();

            root.put("login", login);
            root.put("password", password);

            if (name != null) {
                root.put("name", name);
            }

            return root;
        }
    }

    private static class Client implements Serializable {

        private String partner;
        private String platform;
        private String uid;
        private String imei;
        private String userAgent;
        private String phoneNumber;
        private String buildId;
        private int screenWidth;
        private int screenHeight;
        private String locale;
        private String androidId;
        private String appLocale;

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public void setAndroidId(String androidId) {
            this.androidId = androidId;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setBuildId(String buildId) {
            this.buildId = buildId;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public void setAppLocale(String appLocale) {
            this.appLocale = appLocale;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject root = new JSONObject();

            root.put("partner", partner);
            root.put("platform", platform);
            root.put("userAgent", userAgent);
            root.put("uid", uid);
            root.put("imei", imei);
            root.put("phone", phoneNumber);
            root.put("screenWidth", screenWidth);
            root.put("screenHeight", screenHeight);
            root.put("locale", locale);
            root.put("buildId", buildId);
            root.put("udid", androidId);
            root.put("appLocale", appLocale);

            return root;
        }
    }
}
