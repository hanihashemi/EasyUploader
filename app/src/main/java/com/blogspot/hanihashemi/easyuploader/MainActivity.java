package com.blogspot.hanihashemi.easyuploader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.hanihashemi.easyuploaderlibrary.RequestHeader;
import com.blogspot.hanihashemi.easyuploaderlibrary.UploadFile;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, UploadFile.UploadFileListener {

    private static final int SELECT_PHOTO = 100;
    public static final String TAG = "MyActivity";
    private Button btnClickMe;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClickMe = (Button) findViewById(R.id.btnClickMe);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        btnClickMe.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            String imagePath = getPath(data.getData());
            uploadImage(imagePath);
        }
    }

    private void uploadImage(String imagePath) {
        progressBar.setProgress(0);

        UploadFile uploadFile = new UploadFile();

        ArrayList<RequestHeader> requestHeaders = new ArrayList<>();
        requestHeaders.add(new RequestHeader("Cache-Control", "no-cache"));

        uploadFile.send(
                "http://192.168.1.120/",
                imagePath,
                requestHeaders,
                this);
    }

    public String getPath(Uri uri) {
        Uri selectedImage = uri;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(
                selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    @Override
    public void onSuccess(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "File uploaded", Toast.LENGTH_LONG).show();
            }
        });
        Log.i(TAG, response);
    }

    @Override
    public void onFail(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void onProgress(long sent, long total) {
        int progress = (int) (100 * sent / total);
        Log.i(TAG, progress + " %");
        progressBar.setProgress(progress);
    }
}
