package com.seeapenny.client.http;


import com.seeapenny.client.server.Response;

public interface HttpCommandListener {

    public void onResponse(HttpCommand command, Response response);

    public void onError(HttpCommand command, int code, String reason);

    public void beforeSend(HttpCommand httpCommand);

    public void onPartSent(HttpCommand httpCommand, int offset, int length);

    public void afterReceive(HttpCommand httpCommand);

}

