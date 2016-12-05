package com.seeapenny.client.http;

import com.seeapenny.client.SeeapennyApp;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class HttpHandler implements HttpResponseInterceptor {

    private static final String LOG_TAG = HttpHandler.class.getSimpleName();

    private static final boolean DEBUG = false;

    private static final String GZIP = "gzip";

    public static final String MIME_JSON = "application/json";
    public static final String MIME_XML = "application/xml";
    public static final String MIME_FORM = "application/x-www-form-urlencoded";
    public static final String MIME_AMR = "audio/amr";
    public static final String MIME_AUDIO_3GP = "audio/3gpp";
    public static final String MIME_VIDEO_3GP = "video/3gpp";
    public static final String MIME_AUDIO_MP4 = "audio/mp4";
    public static final String MIME_VIDEO_MP4 = "video/mp4";

    private final DefaultHttpClient client;

    public HttpHandler() {
        if (DEBUG) {
            java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
            java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
        }

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, SeeapennyApp.userAgent());
        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 21 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8 * 1024);

        HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);

        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 3);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(3));

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(manager, params);
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        client.addResponseInterceptor(this);

        // client.setReuseStrategy(new ReuseStratagy());
        // client.setKeepAliveStrategy(new KeepAliveStratagy());

//        if (EconomyApp.DEBUG) Log.d("HttpHandler", "newInstance: " + this);
    }

    public String[] getAllCookies() {
        List<Cookie> cookies = client.getCookieStore().getCookies();
        String[] cookieStrs = new String[cookies.size()];
        for (int i = 0; i < cookieStrs.length; i++) {
            Cookie cookie = cookies.get(i);
            cookieStrs[i] = cookie.getName() + "=" + cookie.getValue();
        }
        return cookieStrs;
    }

    public List<SerializableCookie> getSerializableCookies() {
        List<Cookie> cookies = client.getCookieStore().getCookies();
        List<SerializableCookie> serCookies = new ArrayList<SerializableCookie>(cookies.size());
        for (Cookie cookie: cookies) {
            SerializableCookie serCookie = SerializableCookie.create(cookie);
            serCookies.add(serCookie);
        }
        return serCookies;
    }

    public void setCookies(List<SerializableCookie> serCookies) {
        CookieStore cookieStore = client.getCookieStore();
        for (SerializableCookie serCookie : serCookies) {
            Cookie cookie = serCookie.toCookie();
            cookieStore.addCookie(cookie);
        }
    }

    public HttpResponse executeGet(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.setHeader("Connection", "close");
        return client.execute(request);
    }

    public HttpResponse executePost(String uri, String contentType, String acceptType, String body) throws IOException {
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", acceptType);
        request.setHeader("Accept-Encoding", GZIP);
        request.setHeader("Connection", "close");

        if (body != null && body.length() > 0) {
            AbstractHttpEntity entity = null;
            try {
                entity = new StringEntity(body);
            } catch (UnsupportedEncodingException uee) {
            }
            entity.setContentType(contentType);
            request.setEntity(entity);
        }

        return client.execute(request);
    }

    public HttpResponse executeLongPoll(String uri, String accept) throws IOException {
        HttpPost request = new HttpPost(uri);
        request.setHeader("Connection", "close");
        HttpConnectionParams.setConnectionTimeout(request.getParams(), 30 * 60 * 1000);
        HttpConnectionParams.setSoTimeout(request.getParams(), 31 * 60 * 1000);
        request.setHeader("Accept", accept);
        request.setHeader("Accept-Encoding", GZIP);

        return client.execute(request);
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpEntity entity = response.getEntity();
        Header encoding = entity.getContentEncoding();
        if (encoding != null) {
            for (HeaderElement element : encoding.getElements()) {
                if (GZIP.equalsIgnoreCase(element.getName())) {
                    response.setEntity(new InflatingEntity(response.getEntity()));
                    break;
                }
            }
        }
    }

    private static class InflatingEntity extends HttpEntityWrapper {

        public InflatingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException, IllegalStateException {
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            return -1;
        }

    }

    // public static class ReuseStratagy extends DefaultConnectionReuseStrategy {
    //
    // @Override
    // public boolean keepAlive(HttpResponse response, HttpContext context) {
    // boolean keepAlive = super.keepAlive(response, context);
    // Log.d("ReuseStratagy", "keepAlive: " + keepAlive);
    // return keepAlive;
    // }
    //
    // }
    //
    // public static class KeepAliveStratagy extends
    // DefaultConnectionKeepAliveStrategy {
    //
    // @Override
    // public long getKeepAliveDuration(HttpResponse response, HttpContext
    // context) {
    // long keepAliveDuration = super.getKeepAliveDuration(response, context);
    // Log.d("KeepAliveStratagy", "keepAliveDuration: " + keepAliveDuration);
    // return keepAliveDuration;
    // }
    // }

}

