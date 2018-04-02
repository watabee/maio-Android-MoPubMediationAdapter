package jp.maio.maioandroidmopubadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mopub.mobileads.AdViewController;
import com.mopub.mobileads.MaioUtils;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideos;

public class MainActivity extends AppCompatActivity implements MoPubInterstitial.InterstitialAdListener {

    private final String AD_UNIT_ID = "fcf07a6802db40cfa0ce0c249f7c2b52";
    private final String AD_UNIT_ID_INTER = "9052a031590f49aca475b099c9c4f62a";
    private MoPubInterstitial _moPubInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoPubRewardedVideos.initializeRewardedVideo(this);

        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRewardedVideo();
            }
        });

        Button showButton = findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideo();
            }
        });

        Button loadInterstitialButton = findViewById(R.id.loadInterButton);
        loadInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInterstitial();
            }
        });


        Button showInterButton = findViewById(R.id.showInterButton);
        showInterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

    }

    private void loadRewardedVideo() {
        MoPubRewardedVideos.loadRewardedVideo(AD_UNIT_ID);
    }
    private void showRewardedVideo() {
        MoPubRewardedVideos.showRewardedVideo(AD_UNIT_ID);
    }

    private void loadInterstitial() {
        _moPubInterstitial = new MoPubInterstitial(this, AD_UNIT_ID_INTER);
        _moPubInterstitial.setInterstitialAdListener(this);
        _moPubInterstitial.load();
    }

    private void showInterstitial() {
        _moPubInterstitial.show();
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        Log.d("DEBUG", "onInterstitialLoaded");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        Log.d("DEBUG", "failed: " + errorCode.toString());
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {

    }
}
