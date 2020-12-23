package com.ecodiver.dive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    private Uri fileimagepath;
    private ImageView ivUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ivUpload=findViewById(R.id.imageView);
        Intent imageIntent=getIntent();
        if(imageIntent.hasExtra("BUNDLE_EXTRA"))
        {
            Bundle bundle=imageIntent.getBundleExtra("BUNDLE_EXTRA");
            fileimagepath=bundle.getParcelable("IMAGE");

        }

        Picasso.with(this).load(fileimagepath).fit().
                centerCrop().into(ivUpload);
    }
}