package com.blogspot.hanihashemi.easyuploaderlibrary;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hani on 4/8/15.
 */
public class UploadFile {
    private String serverUrl;
    private String filePath;

    public UploadFile() {

    }

    public void send(String serverUrl, String filePath) {
        this.serverUrl = serverUrl;
        this.filePath = filePath;

        new AsyncUpload(serverUrl, filePath).start();
    }

    private class AsyncUpload implements Runnable {
        private String serverUrl;
        private String filePath;

        public AsyncUpload(String serverUrl, String filePath) {
            this.serverUrl = serverUrl;
            this.filePath = filePath;
        }

        public void start() {
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
//            httpURLConnection.addRequestProperty("X-Auth-Token", MyApplication.getInstance().getUserToken());
                httpURLConnection.connect();

                OutputStream out = httpURLConnection.getOutputStream();

                byte[] buffer = new byte[2048];
                FileInputStream fileInputStream = new FileInputStream(file);

                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }

                InputStream is = httpURLConnection.getInputStream();
                byte[] b1 = new byte[1024];
                StringBuffer bufferResponse = new StringBuffer();

                while (is.read(b1) != -1)
                    bufferResponse.append(new String(b1));

                httpURLConnection.disconnect();
                System.out.println(bufferResponse.toString());

                fileInputStream.close();
                out.close();
                httpURLConnection.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getFilePath() {
        return filePath;
    }
}
