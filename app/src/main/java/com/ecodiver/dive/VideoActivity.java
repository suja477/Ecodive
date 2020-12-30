package com.ecodiver.dive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daasuu.gpuv.egl.filter.GlSepiaFilter;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoActivity extends AppCompatActivity {

    private static final String MP4_URL = "";
    private SimpleExoPlayer player;

    private GPUPlayerView gpuPlayerView;
    private Uri videopath;
    private Button sepiaVideoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent videoIntent = getIntent();
        if (videoIntent.hasExtra("BUNDLE_EXTRA")) {
            Bundle bundle = videoIntent.getBundleExtra("BUNDLE_EXTRA");
            videopath = bundle.getParcelable("VIDEO");

        }

        TrackSelector trackSelector = new DefaultTrackSelector();

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "MyApplication"), defaultBandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videopath);

        // SimpleExoPlayer
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        // Prepare the player with the source.
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        gpuPlayerView = findViewById(R.id.gpuplayer);
        // set SimpleExoPlayer
        gpuPlayerView.setSimpleExoPlayer(player);
       // gpuPlayerView.setLayoutParams(new LinearLayout((ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // add gpuPlayerView to WrapperView
        // findViewById(R.id.ll_video_view).addView(gpuPlayerView);
        gpuPlayerView.onResume();

        // Set Filter. Filters is here.
        //        Custom filters can be created by inheriting GlFilter.java.

       sepiaVideoBtn=findViewById(R.id.btn_sepiavideo);
        sepiaVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gpuPlayerView.setGlFilter(new GlSepiaFilter());

                // Later when image should be saved saved:
                // gpuImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
            }

        });
        gpuPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                player.setPlayWhenReady(!player.getPlayWhenReady());
                return false;
            }
        });
    }

}