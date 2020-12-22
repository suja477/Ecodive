package com.ecodiver.dive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int WRITE_EXTERNAL_STORAGE = 1;
    private static final int READ_EXTERNAL_STORAGE = 1;
    private ImageView ivUploadImage1;
    private ImageView ivFilteredImage;
    private ImageView ivGrayImage;
    private ImageView sketchImage;
    private Button btnFilterImage;
    private Uri filePathImage;
    private String TAG;
    private Bitmap bitmap;
    private  ProgressDialog progDailog;
    public static Uri imagepath;

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    Mat imageMat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivUploadImage1 = findViewById(R.id.btn_image1);
        ivFilteredImage=findViewById(R.id.btn_image2);
      //  ivGrayImage=findViewById(R.id.gray_image);
      //  sketchImage=findViewById(R.id.sketch_image);
//button to upload image
        ivUploadImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();

            }
        });
        btnFilterImage = findViewById(R.id.btn_applyfiltr);

        btnFilterImage.setOnClickListener(new View.OnClickListener() {
                   @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable)ivUploadImage1.getDrawable()).getBitmap();
                       showProgressDialog();
                       if(bitmap!=null){
                           Bitmap newbitmap=FilterUtility.applyOtherFilter(bitmap);
                          // Bitmap sepia=FilterUtility.sepiaFilter(bitmap);
                        //   ivGrayImage.setImageBitmap(newbitmap);
                        //   sketchImage.setImageBitmap(sepia);
                           }
               // ColorMatrix cm = FilterUtility.applyFilter(bitmap);
              new FilterAsyncTask(getApplicationContext(),new PrivateAsyncTask()).execute(bitmap);
             //   new GreyFilter(getApplicationContext(),new PrivateAsyncTask()).execute(bitmap);
                    /* ColorMatrix cm = new ColorMatrix(new float[]
                               {
                                      1.0191516f, 0.11369972f, -0.15909073, 0, 0,
                                       0, 1, 0, 0, 0,
                                       0, 0, 1, 0, 0,
                                       0, 0, 0, 1, 0
                               });
             final ColorFilter filter = new ColorMatrixColorFilter(cm);
            ivUploadImage1.getDrawable().setColorFilter(filter);*/
            }
        });


    }

    private void showProgressDialog() {
        progDailog = new ProgressDialog(MainActivity.this);
        progDailog.setMessage("Loading...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }


    //open image chooser and upload image
    public void showFileChooser() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{String.valueOf(READ_EXTERNAL_STORAGE)},
                    READ_EXTERNAL_STORAGE);
        }

        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.pic_select)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathImage = data.getData();
            Log.i("imageupload", filePathImage.toString());

                imagepath=filePathImage;
                Picasso.with(this).load(filePathImage).fit().
                        centerCrop().into(ivUploadImage1);
           // Bitmap bitmap = ((BitmapDrawable)ivUploadImage1.getDrawable()).getBitmap();

          //      Drawable drawable = new BitmapDrawable(getResources(), bitmap);
          /*  if(bitmap!=null){
                Bitmap newbitmap=FilterUtility.applyOtherFilter(bitmap);
                Bitmap sepia=FilterUtility.sepiaFilter(bitmap);
                ivGrayImage.setImageBitmap(newbitmap);
                sketchImage.setImageBitmap(sepia);}*/

        }
    }



    private class PrivateAsyncTask implements AsyncTaskCompleteListener<ColorMatrix>
    {


              @Override
        public void onTaskComplete(ColorMatrix result) {
                  if(result!=null){
                  final ColorFilter filter = new ColorMatrixColorFilter(result);
                  Picasso.with(getApplicationContext()).load(filePathImage).fit().
                          centerCrop().into(ivFilteredImage);
                  ivFilteredImage.setColorFilter(filter);
                  progDailog.dismiss();
                  //FilterUtility.hue()
                 // ivUploadImage1.getDrawable().setColorFilter(filter);
                  Log.i("APPLY FILTER","filter applied");
              //    Bitmap bitmap = ((BitmapDrawable)ivUploadImage1.getDrawable()).getBitmap();
              //   bitmap= changeHue(bitmap,FilterUtility.hueshift);
                //  ivUploadImage1.setImageBitmap(bitmap);
              }}


              //for grayscale image
        @Override
        public void onTaskComplete(Bitmap bitmap) {
            if(bitmap!=null){
                ivGrayImage.setImageBitmap(bitmap);
                progDailog.dismiss();
            }
        }
    }


}