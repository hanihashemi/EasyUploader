package com.blogspot.hanihashemi.easyuploaderlibrary;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Hani on 4/8/15.
 * http://oostaa.com
 */
public class EasyUploader implements Runnable {

    private static final int HTTP_CREATED = 201;
    private static final int CONNECTION_TIMEOUT = 10000;
    private String url;
    private String filePath;
    private UploadFileListener uploadFileListener;
    private List<RequestHeader> requestHeaders;
    private static final int HTTP_SUCCESS = 200;

    public EasyUploader() {

    }

    public void send(
            String serverUrl,
            String filePath,
            List<RequestHeader> requestHeaders,
            UploadFileListener uploadFileListener
    ) {
        this.url = serverUrl;
        this.filePath = filePath;
        this.requestHeaders = requestHeaders;
        this.uploadFileListener = uploadFileListener;

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            File file = new File(getFilePath());
            if (!file.exists())
                throw new FileNotFoundException("File isn't exist: " + file.getAbsolutePath());
            URL url = new URL(getUrl());

            Log.d("send request to: %s", url.toString());

            HttpURLConnection httpURLConnection = setConnectionSettings(url);
            httpURLConnection.connect();

            OutputStream out = httpURLConnection.getOutputStream();

            byte[] buffer = new byte[2048];
            FileInputStream fileInputStream = new FileInputStream(file);

            long totalFileBytes = file.length();
            long totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                out.flush();
                uploadFileListener.onProgressUploading((int) (100 * totalBytesRead / totalFileBytes));
                Thread.sleep(10); // :D
            }

            if (httpURLConnection.getResponseCode() == HTTP_CREATED || httpURLConnection.getResponseCode() == HTTP_SUCCESS)
                uploadFileListener.onSuccessUploading("file://" + getFilePath());
            else
                uploadFileListener.onFailUploading(new Exception("unsuccessful uploading"), getUrl());

            fileInputStream.close();
            out.close();
            httpURLConnection.disconnect();
        } catch (Exception ex) {
            uploadFileListener.onFailUploading(ex, getUrl());
        }
    }

    private HttpURLConnection setConnectionSettings(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setChunkedStreamingMode(2048);
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(CONNECTION_TIMEOUT);
        for (RequestHeader requestHeader : requestHeaders)
            httpURLConnection.addRequestProperty(requestHeader.getKey(), requestHeader.getValue());
        return httpURLConnection;
    }

    public String getUrl() {
        return url;
    }

    public String getFilePath() {
        return filePath;
    }

    public interface UploadFileListener {
        void onSuccessUploading(String response);

        void onFailUploading(Exception exception, String url);

        void onProgressUploading(int percent);
    }
}
