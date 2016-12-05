package com.seeapenny.client;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoCapturer {

    private final String albumName;
    private final boolean addToGallery;
    private Uri photoUri;
    private SeeapennyApp app;

    public PhotoCapturer(String albumName, boolean addToGallery) {
        this.albumName = albumName;
        this.addToGallery = addToGallery;
        this.app = SeeapennyApp.getInstance();
    }

    private File createImageFile(String fileName) {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + fileName + ".jpg";
        File file = new File(getStorageDir(), imageFileName);
        return file;
    }

    public void start(Activity activity, int requestCode, String fileName) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile(fileName);
        if (file != null) {
            photoUri = Uri.fromFile(file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        try {
            activity.startActivityForResult(takePictureIntent, requestCode);
        } catch (ActivityNotFoundException anfex) {
            Toast.makeText(activity, R.string.camera_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap handleIcon(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Object obj = extras.get("data");
            if (obj instanceof Bitmap) {
                return (Bitmap) obj;
            }
        }
        return null;
    }

    public Uri handlePhoto(Activity activity) {
        if (addToGallery) {
            addToGallery(activity);
        }
        return photoUri;
    }

    private void addToGallery(Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    private File getStorageDir() {
        File storageDir = null;
        if (Build.VERSION.SDK_INT >= 8) {
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            storageDir = new File(root, albumName);
            storageDir.mkdirs();
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory() + "Pictures/" + albumName);
            storageDir.mkdirs();
        }
        return storageDir;
    }

}
