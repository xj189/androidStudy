package com.style.lib.media.video;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.HashMap;


public class PlayVideoActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private String path;

    private VideoView videoView;
    private TextView textExist;
    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
        setContentView(R.layout.activity_play_video);

        path = getIntent().getStringExtra("path");


        videoView = (VideoView) findViewById(R.id.videoview);

        textExist = (TextView) findViewById(R.id.tv_exist);

        container = findViewById(R.id.rl_container);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playVideo();
    }


    private void playVideo() {


        videoView.setVideoPath(path);
        // 开始播放
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                textExist.setVisibility(View.VISIBLE);
                videoView.start();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                return false;
            }
        });
    }

    private void getVideoDuration(String path){
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (path != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(path, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }

            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        } catch (Exception ex) {
            Log.e(TAG, "MediaMetadataRetriever exception " + ex);
        } finally {
            try {
                mmr.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
