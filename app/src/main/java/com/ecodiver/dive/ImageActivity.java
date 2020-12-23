package com.ecodiver.dive;

import androidx.appcompat.app.AppCompatActivity;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    private Uri fileimagepath;
    private ImageView ivUpload;
    private Button sepiaBtn;
    private Button grayscaleBtn;
    private  GPUImage gpuImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        grayscaleBtn=findViewById(R.id.btn_grayscale);
        sepiaBtn=findViewById(R.id.btn_sepia);
        Intent imageIntent=getIntent();
        if(imageIntent.hasExtra("BUNDLE_EXTRA"))
        {
            Bundle bundle=imageIntent.getBundleExtra("BUNDLE_EXTRA");
            fileimagepath=bundle.getParcelable("IMAGE");

        }

        /*Picasso.with(this).load(fileimagepath).fit().
                centerCrop().into(ivUpload);*/

        GPUImageGrayscaleFilter gpuImageGrayscaleFilter=new GPUImageGrayscaleFilter();
        Uri imageUri = fileimagepath;
         gpuImage = new GPUImage(getApplicationContext());
        gpuImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.gpusurfaceview));
        gpuImage.setImage(imageUri); // this loads image on the current thread, should be run in a thread
        grayscaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gpuImage.setFilter(gpuImageGrayscaleFilter);

                // Later when image should be saved saved:
               // gpuImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
                }

        });
        GPUImageSepiaToneFilter gpuImageSepiaToneFilter=new GPUImageSepiaToneFilter();
        sepiaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gpuImage.setFilter(gpuImageSepiaToneFilter);

                // Later when image should be saved saved:
                // gpuImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
            }

        });
    }
    public void saveImage(View v){

        gpuImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
    }
    public void shareImage(View v){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileimagepath);

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"EcoDiver");
        startActivity(Intent.createChooser(sharingIntent,"choose one"));
    }
}