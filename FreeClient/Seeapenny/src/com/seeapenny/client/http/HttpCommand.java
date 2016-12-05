package com.seeapenny.client.http;

import android.os.AsyncTask;
import android.util.Log;

import com.seeapenny.client.General;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.server.BussinessError;
import com.seeapenny.client.server.Response;

import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class HttpCommand extends AsyncTask<String, Integer, Void> {

    private static final String LOG_TAG = HttpCommand.class.getSimpleName();

    public static final int CANCELED_CODE = -1;
    public static final int IO_ERROR_CODE = -2;

    protected HttpCommandListener listener;
    protected Response response;
    private List<NameValuePair> params;

    protected HttpHandler httpHandler;
    private String contentType;
    private String requestBody;
    private boolean longPoll;
    protected String acceptType;

    protected volatile int responseCode;
    protected volatile String responseReason;


    public HttpCommand(HttpCommandListener listener, Response response) {
        this.listener = listener;
        this.response = response;
        this.params = new ArrayList<NameValuePair>();
    }

    public void sendAsJson(HttpHandler httpHandler, String url, String body) {
        this.httpHandler = httpHandler;
        this.contentType = HttpHandler.MIME_JSON;
        this.acceptType = HttpHandler.MIME_JSON;
        this.requestBody = body;
        listener.beforeSend(this);
        General.executeAsyncTask(this, url);
    }

    public void sendAsForm(HttpHandler httpHandler, String url) {
        sendAsForm(httpHandler, url, HttpHandler.MIME_JSON);
    }

    public void sendAsForm(HttpHandler httpHandler, String url, String acceptType) {
        this.httpHandler = httpHandler;
        this.contentType = HttpHandler.MIME_FORM;
        this.acceptType = acceptType;
        if (params.size() > 0) {
            this.requestBody = URLEncodedUtils.format(params, "UTF-8");
        }
        listener.beforeSend(this);
        General.executeAsyncTask(this, url);
    }

    public void sendAsLongPoll(HttpHandler httpHandler, String url) {
        this.httpHandler = httpHandler;
        this.longPoll = true;
        this.acceptType = HttpHandler.MIME_JSON;
        listener.beforeSend(this);
        General.executeAsyncTask(this, url);
    }

    public void addParam(String name, int value) {
        addParam(name, Integer.toString(value));
    }

    public final void addParam(String name, long value) {
        addParam(name, Long.toString(value));
    }

    public final void addParam(String name, double value) {
        addParam(name, Double.toString(value));
    }

    public final void addParam(String name, boolean value) {
        addParam(name, Boolean.toString(value));
    }

    public void addParam(String name, String value) {
        NameValuePair param = new BasicNameValuePair(name, value);
        params.add(param);
    }

    protected List<NameValuePair> getParams() {
        return params;
    }

    protected void exception(Exception ex) {
        if (SeeapennyApp.DEBUG) Log.e(LOG_TAG, "", ex);
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpResponse httpResponse = null;
        try {
            if (longPoll) {
                httpResponse = httpHandler.executeLongPoll(params[0], acceptType);
            } else {
                httpResponse = httpHandler.executePost(params[0], contentType, acceptType, requestBody);
            }
//         org.apache.http.NoHttpResponseException
        } catch (IOException ioex) {
            exception(ioex);
            responseCode = IO_ERROR_CODE;
            responseReason = ioex.getMessage();


//            if (ioex instanceof SocketTimeoutException) {
//                listener.onError(HttpCommand.this, BussinessError.TIMEOUT_EXCEPTION.getCode(), ioex.getMessage());
//            } else {
//                listener.onError(HttpCommand.this, BussinessError.UNKNOWN_ERROR.getCode(), ioex.getMessage());
//            }
        }

        if (httpResponse != null) {
            StatusLine status = httpResponse.getStatusLine();
            responseCode = status.getStatusCode();
            responseReason = status.getReasonPhrase();

            if (responseCode == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                try {
                    readResponse(entity.getContent());
                } catch (IOException ioex) {
                    exception(ioex);
                } finally {
                    try {
                        entity.consumeContent();
                    } catch (IOException ioex) {
                    }
                }
            }
        }

        return null;
    }

    protected void readResponse(InputStream in) {
        if (HttpHandler.MIME_JSON.equals(acceptType)) {
            String responseBody = toString(in);
            if (SeeapennyApp.DEBUG) Log.d(LOG_TAG, "readResponse JSON: " + responseBody);
            parseJson(responseBody);
        } else if (HttpHandler.MIME_XML.equals(acceptType)) {
            if (SeeapennyApp.DEBUG) {
                String responseBody = toString(in);
                Log.d(LOG_TAG, "readResponse XML: " + responseBody);
                parseXml(responseBody);
            } else {
                parseXml(in);
            }
        }
    }

    protected final String toString(InputStream in) {
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 32 * 1024);
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append('\n');
            }

        } catch (IOException ioex) {
            Log.e(LOG_TAG, "", ioex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioex) {
                    Log.e(LOG_TAG, "", ioex);
                }
            }
        }

        return buf.toString();
    }

    protected final void parseJson(String responseBody) {
//      long time = System.currentTimeMillis();

        JSONObject root = null;
        try {
            root = new JSONObject(responseBody);
        } catch (JSONException jsonex) {
            exception(jsonex);
        }
        if (root != null) {
            response.fromJson(root);
        }

//      time = System.currentTimeMillis() - time;
//      Log.d("JSON", "parse time: " + time);
    }

    protected final void parseXml(InputStream in) {
//      long time = System.currentTimeMillis();

        Element root = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(in);
            root = dom.getDocumentElement();
        } catch (Exception ex) {
            exception(ex);
        }
        if (root != null) {
            response.fromXml(root);
        }

//      time = System.currentTimeMillis() - time;
//      Log.d("XML", "parse time: " + time);
    }

    protected final void parseXml(String responseBody) {
//      long time = System.currentTimeMillis();

        Element root = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(new InputSource(new StringReader(responseBody)));
            root = dom.getDocumentElement();
        } catch (Exception ex) {
            exception(ex);
        }
        if (root != null) {
            response.fromXml(root);
        }

//      time = System.currentTimeMillis() - time;
//      Log.d("XML", "parse time: " + time);
    }

    @Override
    protected void onPostExecute(Void nothing) {
        if (responseCode == HttpStatus.SC_OK) {
            listener.onResponse(this, response);
        } else {
            listener.onError(this, responseCode, responseReason);
        }
        listener.afterReceive(this);

        clear();
    }

    public boolean getResponseCode() {
        return responseCode == HttpStatus.SC_OK;
    }


    @Override
    protected void onCancelled() {
        listener.onError(this, CANCELED_CODE, null);

        clear();
    }

    private void clear() {
        listener = null;
        response = null;
        params = null;

        httpHandler = null;
        contentType = null;
        requestBody = null;
        acceptType = null;
    }
}
