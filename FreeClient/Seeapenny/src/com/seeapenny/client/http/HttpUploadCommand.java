package com.seeapenny.client.http;

import android.util.Log;
import com.seeapenny.client.General;
import com.seeapenny.client.SeeapennyApp;
import com.seeapenny.client.server.Response;
import org.apache.http.NameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpUploadCommand extends HttpCommand {

    private static final String LOG_TAG = HttpUploadCommand.class.getSimpleName();

    private static final String CRLF = "\r\n";
    private static final String HYPHENS = "--";

    private String uploadFieldName = "image";

    public HttpUploadCommand(HttpCommandListener listener, Response response) {
        super(listener, response);
    }

    public void setUploadFieldName(String uploadFieldName) {
        this.uploadFieldName = uploadFieldName;
    }

    public void sendUpload(String url, String filePath, String contentType, String acceptType) {
        if (SeeapennyApp.DEBUG)
            Log.d("HttpUploadCommand", "sendUpload filePath: " + filePath + " contentType: " + contentType);
        this.acceptType = acceptType;
        listener.beforeSend(this);
         General.executeTask(this, url, filePath, contentType);
    }

    @Override
    protected Void doInBackground(String... params) {
        String urlStr = params[0];
        String path = params[1];
        String contentType = params[2];

        String boundary = "Asrf456BGe4h";

        HttpURLConnection conn = null;
        try {
            File file = new File(path);

            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", acceptType);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            String[] cookies = SeeapennyApp.getHttpHandler().getAllCookies();
            for (String cookie : cookies) {
                conn.setRequestProperty("Cookie", cookie);
            }

            OutputStream out = conn.getOutputStream();

            StringBuilder headerBuf = new StringBuilder();

            List<NameValuePair> requestParams = getParams();
            if (params.length > 0) {
                for (NameValuePair paramPair : requestParams) {
                    String name = paramPair.getName();
                    String value = paramPair.getValue();

                    headerBuf.append(HYPHENS).append(boundary).append(CRLF);
                    headerBuf.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(CRLF)
                            .append(CRLF);
                    headerBuf.append(value).append(CRLF);
                }
            }

            headerBuf.append(HYPHENS).append(boundary).append(CRLF);
            headerBuf.append("Content-Disposition: form-data; name=\"").append(uploadFieldName)
                    .append("\"; filename=\"").append(file.getName()).append("\"").append(CRLF);
            headerBuf.append("Content-Type: ").append(contentType).append(CRLF).append(CRLF);
            out.write(headerBuf.toString().getBytes("UTF-8"));

            FileInputStream fin = new FileInputStream(file);
            byte[] buf = new byte[8 * 1024];
            int sumReaded = 0;
            final int length = (int) file.length();
            publishProgress(Integer.valueOf(0), Integer.valueOf(length));
            while (sumReaded < length) {
                int readed = fin.read(buf, 0, buf.length);
                sumReaded += readed;

                out.write(buf, 0, readed);

                publishProgress(Integer.valueOf(sumReaded), Integer.valueOf(length));
            }

            StringBuilder footerBuf = new StringBuilder();
            footerBuf.append(CRLF).append(HYPHENS).append(boundary).append(HYPHENS).append(CRLF).append(CRLF);
            out.write(footerBuf.toString().getBytes("UTF-8"));

            out.close();

            responseCode = conn.getResponseCode();
            responseReason = conn.getResponseMessage();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                readResponse(conn.getInputStream());
            }
            publishProgress(Integer.valueOf(length), Integer.valueOf(length));
        } catch (IOException ioex) {
            exception(ioex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int offset = values[0].intValue();
        int length = values[1].intValue();
        listener.onPartSent(this, offset, length);
    }

}
