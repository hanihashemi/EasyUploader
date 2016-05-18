package com.blogspot.hanihashemi.easyuploader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.hanihashemi.easyuploaderlibrary.RequestHeader;
import com.blogspot.hanihashemi.easyuploaderlibrary.EasyUploder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyUploder.UploadFileListener {

    public static final String TAG = "MyActivity";
    private static final int SELECT_PHOTO = 100;
    private Button btnClickMe;
    private ProgressBar progressBar;
    private TextView txtProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClickMe = (Button) findViewById(R.id.btnClickMe);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        btnClickMe.setOnClickListener(this);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        txtProgress.setText("0 %");

        EasyUploder uploadFile = new EasyUploder();

        ArrayList<RequestHeader> requestHeaders = new ArrayList<>();
        requestHeaders.add(new RequestHeader("Cache-Control", "no-cache"));

        uploadFile.send(
                "http://192.168.2.124/",
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
    public void onSuccessUploading(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "File uploaded", Toast.LENGTH_LONG).show();
            }
        });
        Log.i(TAG, response);
    }

    @Override
    public void onFailUploading(Exception exception, String url) {
        exception.printStackTrace();
    }

    @Override
    public void onProgressUploading(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(percent);
                txtProgress.setText(String.format("%s %%", percent));
            }
        });
    }
}