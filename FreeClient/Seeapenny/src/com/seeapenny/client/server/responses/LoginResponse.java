package com.seeapenny.client.server.responses;


import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.json.JSONUtils;
import com.seeapenny.client.server.Response;
import com.seeapenny.client.server.User;
import org.json.JSONObject;

import java.util.Date;

public class LoginResponse extends Response {

    private String sessionId;
    private long sessionDuration;
    private User user;
    private Date serverTime;


    @Override
    public void fromJson(JSONObject rootDto) {
        super.fromJson(rootDto);

        sessionId = rootDto.optString("sessionId");
        sessionDuration = rootDto.optLong("sessionDuration", 60 * 1000);

        JSONObject sessionDto = rootDto.optJSONObject("session");
        if (sessionDto != null) {
            sessionId = sessionDto.optString("@id");
            sessionDuration = sessionDto.optLong("@durationMillis");
        }

        user = new User();
        JSONUtils.fillJSONable(rootDto, "user", user);


        JSONObject serverTimeDto = rootDto.optJSONObject("serverTime");
        if (serverTimeDto != null) {
            serverTime = JSONUtils.fromJsonDate(serverTimeDto, "@time", SeeapennyApp.DATE_TIME_FORMATTER);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getSessionDuration() {
        return sessionDuration;
    }

    public User getUser() {
        return user;
    }

    public Date getServerTime() {
        return serverTime;
    }
}
