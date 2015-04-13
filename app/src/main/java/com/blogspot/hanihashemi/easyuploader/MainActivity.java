package com.blogspot.hanihashemi.easyuploader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.blogspot.hanihashemi.easyuploaderlibrary.UploadFile;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, UploadFile.UploadFileListener {

    private static final int SELECT_PHOTO = 100;
    public static final String IMAGE_FILES = "image/*";
    public static final String TAG = "MyActivity";
    private Button btnClickMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClickMe = (Button) findViewById(R.id.btnClickMe);
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
        UploadFile uploadFile = new UploadFile();
        uploadFile.send("http://192.168.1.103/", imagePath, this);
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
        Log.i(TAG, response);
    }

    @Override
    public void onFail(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void onProgress(long sent, long total) {
        Log.i(TAG, ((sent * 100) / total) + "");
    }
}
