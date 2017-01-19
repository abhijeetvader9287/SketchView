package com.abhijeet.fabricview;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.onegravity.colorpicker.ColorPickerDialog;
import com.onegravity.colorpicker.ColorPickerListener;
import com.onegravity.colorpicker.SetColorPickerListenerEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1888, SELECT_FILE = 1999, REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    Button clearButton;
    Button textButton;
    FabricView fabricView;
    AppCompatActivity appCompatActivity;
    Button colorButton;
    Button Savebutton;
    Button Imagebutton;
    String TakePhotoMeetup;
    String ChooseFromGalleryMeetup;
    String cancel;
    String userChoosenTask = "";
    final int[] changedcolor = {1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textButton = (Button) findViewById(R.id.addTextbutton);
        appCompatActivity = this;
        clearButton = (Button) findViewById(R.id.clearbutton);
        fabricView = (FabricView) findViewById(R.id.faricView);
        Savebutton=(Button)findViewById(R.id.Savebutton);
        Imagebutton = (Button) findViewById(R.id.addImagebutton);
        colorButton = (Button) findViewById(R.id.changeColorbutton);
        TakePhotoMeetup = "Take Photo";
        ChooseFromGalleryMeetup = "Select From Gallary";
        cancel = "Cancel";
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paint paint = new Paint();
                paint.setTextSize(25f);
                fabricView.drawText("test", 100, 100, paint);
            }
        });
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorPickerDialog = new ColorPickerDialog(appCompatActivity, changedcolor[0], true).show();
                SetColorPickerListenerEvent.setListener(colorPickerDialog, new ColorPickerListener() {
                    @Override
                    public void onDialogClosing() {
                        fabricView.setColor(changedcolor[0]);
                    }
                    @Override
                    public void onColorChanged(int color) {
                        changedcolor[0] = color;
                    }
                });
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabricView.cleanPage();
            }
        });
        Imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        Savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                fabricView.setBackgroundColor(Color.WHITE);
                fabricView.setDrawingCacheEnabled(true);
                Bitmap bitmap = fabricView.getDrawingCache();

                Canvas c = new Canvas(bitmap);

                c.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                fabricView.draw(c);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.png");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
    private void selectImage() {
        final CharSequence[] items = {TakePhotoMeetup, ChooseFromGalleryMeetup,
                cancel};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    result = Utility.checkPermission(MainActivity.this);
                    if (items[item].equals(TakePhotoMeetup)) {
                        userChoosenTask = TakePhotoMeetup;
                        if (result)
                            cameraIntentLower();
                    } else if (items[item].equals(ChooseFromGalleryMeetup)) {
                        userChoosenTask = ChooseFromGalleryMeetup;
                        if (result)
                            galleryIntent();
                    } else if (items[item].equals(cancel)) {
                        dialog.dismiss();
                    }
                } else {
                    if (items[item].equals(TakePhotoMeetup)) {
                        userChoosenTask = TakePhotoMeetup;
                        takePhoto();
                    } else if (items[item].equals(ChooseFromGalleryMeetup)) {
                        if (Utility.checkPermission(MainActivity.this)) {
                            userChoosenTask = ChooseFromGalleryMeetup;
                            galleryIntent();
                        }
                    } else if (items[item].equals(cancel)) {
                        dialog.dismiss();
                    }
                }
            }
        });
        builder.show();
    }
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
    }
    void takePhoto() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            } else {
                cameraIntentUpper();
            }
        }
    }
    private void cameraIntentLower() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void cameraIntentUpper() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(TakePhotoMeetup)) {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            cameraIntentLower();
                        } else {
                            cameraIntentUpper();
                        }
                    } else if (userChoosenTask.equals(ChooseFromGalleryMeetup))
                        galleryIntent();
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            fabricView.drawImage(0, 0, 100, 100, bm);
        } catch (Exception ex) {
        }
    }
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                Uri selectedImageUri = data.getData();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                fabricView.drawImage(0, 0, 100, 100, bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
