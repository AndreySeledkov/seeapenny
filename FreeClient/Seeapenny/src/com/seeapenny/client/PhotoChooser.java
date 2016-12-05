package com.seeapenny.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoChooser {

    public static final int PHOTO_CAPTURE_REQUEST_CODE = 3333;
    public static final int PICK_PHOTO_REQUEST_CODE = 4444;

    private PhotoCapturer photoCapturer;
    private Dialog dialog;

    public void showChoosePhotoSourceDialog(Context context, View.OnClickListener listener) {
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.photo_dialog);

        View cpsGallery = dialog.findViewById(R.id.cps_gallery);
        cpsGallery.setOnClickListener(listener);


        View cpsCamera = dialog.findViewById(R.id.cps_camera);
        cpsCamera.setOnClickListener(listener);

        View cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxWidth = (int) ((300 * displayMetrics.density) + 0.5);
        int pxHeight = (int) ((500 * displayMetrics.density) + 0.5);
        dialog.getWindow().setLayout(pxWidth, pxHeight);

        dialog.show();

    }

    public void photoSourceChoosed(Activity activity, boolean takePhoto, String fileName) {
//        dialog.dismiss();
        if (takePhoto) { // camera
            toTakePhoto(activity, fileName);
        } else { // gallery
            toPickPhoto(activity);
        }
    }

    private void toTakePhoto(Activity activity, String fileName) {
        String albumName = activity.getResources().getString(R.string.app_name);
        photoCapturer = new PhotoCapturer(albumName, true);
        photoCapturer.start(activity, PHOTO_CAPTURE_REQUEST_CODE, fileName);
    }

    private void toPickPhoto(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT > 10) {
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        }
        try {
            activity.startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
        } catch (ActivityNotFoundException acfex) {
            Toast.makeText(activity, R.string.no_gallery, Toast.LENGTH_LONG).show();
        }
    }

    public Uri photoUri(Activity activity, int requestCode, Intent data) {
        Uri photoUri = null;
        String path = null;
        int orientation = 0;

        if (requestCode == PHOTO_CAPTURE_REQUEST_CODE) {
            photoUri = photoCapturer.handlePhoto(activity);
        } else if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (data != null) {
                photoUri = data.getData();
            }
        }

        if (photoUri == null) {
            String[] projection = {MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.ORIENTATION};
            String fileSort = MediaStore.Images.ImageColumns._ID + " DESC";
            Cursor cursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                    fileSort);
            long imageId = 0;
            if (cursor != null && cursor.moveToFirst()) {
                imageId = cursor.getLong(0);
                path = cursor.getString(1);
                orientation = cursor.getInt(2);

            }
            photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));

        } else {
            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor cursor = activity.managedQuery(photoUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                path = cursor.getString(0);
                orientation = cursor.getInt(1);

            }
        }

        if (orientation != 0) {
            SeeapennyApp.getCacheManager().clear();

            Bitmap src = scaledBitmap(path);
            if (src != null) {

                Matrix matrix = new Matrix();
                matrix.preRotate(orientation);
                Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
                src.recycle();
                src = null;

                String rotatedPath = path + ".rot";
                saveBitmap(dst, rotatedPath);
                dst.recycle();

                photoUri = Uri.fromFile(new File(rotatedPath));
            }
        }

        return photoUri;
    }

    private void saveBitmap(Bitmap bitmap, String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
        } catch (IOException ioex) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioex) {
                }
            }
        }
    }

    private Bitmap scaledBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;

        int scale = 1;
        while (width * height > SeeapennyApp.getMaxImageSquare()) {
            width /= 2;
            height /= 2;
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return BitmapFactory.decodeFile(path, options);
    }

    public Uri getPhotoUri() {
        if (photoCapturer != null) {
            return photoCapturer.getPhotoUri();
        }
        return null;
    }

    public void setPhotoUri(Uri photoUri) {
        if (photoUri != null) {
            if (photoCapturer == null) {
                photoCapturer = new PhotoCapturer("", true);
            }
            photoCapturer.setPhotoUri(photoUri);
        }
    }

}
