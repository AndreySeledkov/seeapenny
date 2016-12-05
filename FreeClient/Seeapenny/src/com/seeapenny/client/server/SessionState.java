package com.seeapenny.client.server;

import com.seeapenny.client.server.responses.LoginResponse;

import java.io.Serializable;
import java.util.Date;

public class SessionState implements Serializable {

    private long lastSessionTime;
    private long sessionDuration;
    private String sessionId;

    private long serverTimeDiff;

    private User user;

    public void clear() {
        lastSessionTime = 0;
        sessionDuration = 0;
        sessionId = null;

        serverTimeDiff = 0;

    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    private void setServerTimeDiff(Date serverTime) {
        Date now = new Date();
        this.serverTimeDiff = now.getTime() - serverTime.getTime();
    }

    public long serverTimeNow() {
        return System.currentTimeMillis() - serverTimeDiff;
    }

    public void setLastSessionTime(long lastSessionTime) {
        this.lastSessionTime = lastSessionTime;
    }

    public boolean isSessionValid() {
        long now = System.currentTimeMillis();
        return (user != null && now < lastSessionTime + sessionDuration);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void fillFromLoginResponse(LoginResponse response) {
        user = response.getUser();
        Date serverTime = response.getServerTime();
        if (serverTime != null) {
            setServerTimeDiff(serverTime);
        }
        sessionDuration = response.getSessionDuration();
        sessionId = response.getSessionId();

    }

    public long getServerTimeDiff() {
        return serverTimeDiff;
    }
}
