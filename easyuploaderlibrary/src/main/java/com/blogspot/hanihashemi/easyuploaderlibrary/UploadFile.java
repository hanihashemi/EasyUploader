package com.blogspot.hanihashemi.easyuploaderlibrary;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by hani on 4/8/15.
 */
public class UploadFile implements Runnable {
    private String serverUrl;
    private String filePath;
    private UploadFileListener uploadFileListener;
    private List<RequestHeader> requestHeaders;

    public UploadFile() {

    }

    public void send(
            String serverUrl,
            String filePath,
            List<RequestHeader> requestHeaders,
            UploadFileListener uploadFileListener
    ) {
        this.serverUrl = serverUrl;
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
            URL url = new URL(getServerUrl());

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setChunkedStreamingMode(2048);
            for (RequestHeader requestHeader : requestHeaders)
                httpURLConnection.addRequestProperty(requestHeader.getKey(), requestHeader.getValue());
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
                uploadFileListener.onProgress(totalBytesRead, totalFileBytes);
            }

            InputStream is = httpURLConnection.getInputStream();
            byte[] b1 = new byte[1024];
            StringBuffer bufferResponse = new StringBuffer();

            while (is.read(b1) != -1)
                bufferResponse.append(new String(b1));

            httpURLConnection.disconnect();

            uploadFileListener.onSuccess(bufferResponse.toString());

            fileInputStream.close();
            out.close();
            httpURLConnection.disconnect();
        } catch (Exception ex) {
            uploadFileListener.onFail(ex);
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public interface UploadFileListener {
        void onSuccess(String response);

        void onFail(Exception exception);

        void onProgress(long sent, long total);
    }
}
