package com.ecodiver.dive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "Eco Imageactivity";
    private Uri fileimagepath;
    private ImageView ivUpload;
    private Button sepiaBtn;
    private Button grayscaleBtn;
    private GPUImage gpuImage;
    private GPUImage original;
    private GPUImageFilter mCurrentImageFilter;
    private SeekBar mSeekBar;
    private GLSurfaceView glSurfaceView;
    private File savedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.show();
        mSeekBar=findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        grayscaleBtn = findViewById(R.id.btn_grayscale);
        sepiaBtn = findViewById(R.id.btn_sepia);
        Intent imageIntent = getIntent();
        if (imageIntent.hasExtra("BUNDLE_EXTRA")) {
            Bundle bundle = imageIntent.getBundleExtra("BUNDLE_EXTRA");
            fileimagepath = bundle.getParcelable("IMAGE");

        }

        /*Picasso.with(this).load(fileimagepath).fit().
                centerCrop().into(ivUpload);*/

        GPUImageGrayscaleFilter gpuImageGrayscaleFilter = new GPUImageGrayscaleFilter();
        Uri imageUri = fileimagepath;
       original = new GPUImage(getApplicationContext());
       // original.setGLSurfaceView( findViewById(R.id.gpusurfaceviewOriginal));
        original.setImage(imageUri);
        gpuImage = new GPUImage(getApplicationContext());
         glSurfaceView = findViewById(R.id.gpusurfaceview);
        gpuImage.setGLSurfaceView( findViewById(R.id.gpusurfaceview));
        gpuImage.setImage(imageUri); // this loads image on the current thread, should be run in a thread
        grayscaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchFilterTo(gpuImageGrayscaleFilter);
            }

        });
        GPUImageSepiaToneFilter gpuImageSepiaToneFilter = new GPUImageSepiaToneFilter();
        sepiaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchFilterTo(gpuImageSepiaToneFilter);

                // Later when image should be saved saved:
                // gpuImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
            }

        });

    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           // seekText.setText(String.valueOf(i*2)+(getResources().getString(R.string.km)));
            seekBar.setProgress(progress);
           float sat= progress/10f;
                GPUImage gpuImagecopy=original;

               Bitmap bitmap=gpuImagecopy.getBitmapWithFilterApplied();//get original image

            if(mCurrentImageFilter!=null){
                Log.i(TAG,"flter select"+mCurrentImageFilter.toString());
                gpuImagecopy.setFilter(mCurrentImageFilter);
                bitmap=gpuImagecopy.getBitmapWithFilterApplied();

            }
            gpuImagecopy.setImage(bitmap);
            gpuImagecopy.setFilter(new GPUImageSaturationFilter(progress));
            bitmap=gpuImagecopy.getBitmapWithFilterApplied();
            gpuImage.setImage(bitmap);
              
            Log.i(TAG,"seek-------"+sat);
          // gpuImage.requestRender();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };



    public Bitmap getGPUImageFromAssets(float progress){
        InputStream image_stream = null;

        Bitmap bitmap=null;
        try {
            try {
                image_stream = getApplicationContext().getContentResolver().openInputStream(fileimagepath);
                bitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
               image_stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //use GPUImage processed image
        GPUImage gpuImageNew = new GPUImage(this);
        gpuImageNew.setImage(bitmap);
        gpuImageNew.setFilter(new GPUImageSaturationFilter(20));
        bitmap = gpuImageNew.getBitmapWithFilterApplied();
        return bitmap;
    }


    public void saveImage(View v) {
       new SaveData().execute();

    }

    public void shareImage(View v) {
        File root = getExternalFilesDir(null);

        File mediaStorageDir = new File (root.getAbsolutePath() + "/ecodiver");
           // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            String mImageName = "MI_" + timeStamp + ".jpg";
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            if(!mediaFile.exists())//if image file not exists
            {
               new SaveandShareData().execute();

            }
            else{
               // Bitmap bitmapFiltered = gpuImage.getBitmapWithFilterApplied();
                Uri uri = FileProvider.getUriForFile(ImageActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", mediaFile);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);//--------------change
                startActivity(Intent.createChooser(share,"Share via"));

            }
        }else
            new SaveandShareData().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
       // File mediaStorageDir = new File(getApplicationContext().getFilesDir(), "MyAppName" + File.separator + "Images");
        File root = getExternalFilesDir(null);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File mediaStorageDir = new File (root.getAbsolutePath() + "/ecodiver");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private OnGpuImageFilterChosenListener mOnGpuImageFilterChosenListener = new OnGpuImageFilterChosenListener() {
        @Override
        public void onGpuImageFilterChosenListener(GPUImageFilter filter, String filterName) {
            switchFilterTo(filter);
            //  mFilterNameTv.setText(filterName);
        }


    };

    private void switchFilterTo(GPUImageFilter filter) {
        if (mCurrentImageFilter == null
                || (filter != null && !mCurrentImageFilter.getClass().equals(filter.getClass()))) {
            mCurrentImageFilter = filter;
            gpuImage.setFilter(mCurrentImageFilter);
            //   mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mCurrentImageFilter);
            //   mSeekBar.setVisibility(mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        } else {
           // mSeekBar.setVisibility(View.GONE);
        }
    }


    private class SaveData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            savedImageFile = getOutputMediaFile();
            FileOutputStream out = null;
            Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
            Log.i(TAG,"got bitmapwith filter");
            try {
                out = new FileOutputStream(savedImageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Log.i(TAG, "-----------file saved-----------"+savedImageFile.getAbsolutePath());
                scanFile(savedImageFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

           }

        protected void onPostExecute(Boolean result) {
            //This is run on the UI thread so you can do as you wish here
            if(result)
                Toast.makeText(getApplicationContext(),"saved ",
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(),"some error occured ",
                        Toast.LENGTH_SHORT).show();
        }
    }

    private class SaveandShareData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            savedImageFile = getOutputMediaFile();
            FileOutputStream out = null;
            Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
            try {
                out = new FileOutputStream(savedImageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Log.i(TAG, "-----------file saved-----------"+savedImageFile.getAbsolutePath());
                scanFile(savedImageFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        protected void onPostExecute(Boolean result) {
            //This is run on the UI thread so you can do as you wish here
            if(result) {
              //  Bitmap bitmapFiltered = gpuImage.getBitmapWithFilterApplied();
                Uri uri = FileProvider.getUriForFile(ImageActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", savedImageFile);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);//--------------change
                startActivity(Intent.createChooser(share,"Share via"));
            }

            else
                Toast.makeText(getApplicationContext(),"some error occured ",
                        Toast.LENGTH_SHORT).show();
        }
    }
    private void scanFile(String path) {

        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("Tag", "Scan finished. You can view the image in the gallery now.");
                    }
                });
    }
}