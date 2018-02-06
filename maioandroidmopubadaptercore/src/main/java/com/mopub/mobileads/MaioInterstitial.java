package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListener;

import static com.mopub.mobileads.MaioUtils.getMoPubErrorCode;
import static com.mopub.mobileads.MaioUtils.writeLogPreLoad;

@SuppressWarnings("PointlessBooleanExpression")
public class MaioInterstitial extends CustomEventInterstitial {

    private static boolean _isInitialized;
    private static CustomEventInterstitialListener _moPubListener;

    private final static MaioAdsListener _singletonMaioListener = new MaioAdsListener() {
        @Override
        public void onInitialized() {
            _isInitialized = true;
        }

        @Override
        public void onChangedCanShow(String s, boolean canShow) {
            if(_moPubListener != null) {
                if(canShow) {
                    _moPubListener.onInterstitialLoaded();
                } else {
                    _moPubListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
                }
            }
        }

        @Override
        public void onClosedAd(String s) {
            if(_moPubListener != null) _moPubListener.onInterstitialDismissed();
        }

        @Override
        public void onClickedAd(String s) {
            if(_moPubListener != null) _moPubListener.onInterstitialClicked();
        }

        @Override
        public void onFailed(FailNotificationReason failNotificationReason, String s) {
            if(_moPubListener != null) {
                MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                _moPubListener.onInterstitialFailed(errorCode);
            }
        }

        @Override
        public void onStartedAd(String s) {
            if(_moPubListener != null) _moPubListener.onInterstitialShown();
        }
    };

    private MaioCredentials _credentials;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        if(context instanceof Activity == false) {
            writeLogPreLoad("Context must be type of Activity.");
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
            return;
        }

        _moPubListener = customEventInterstitialListener;
        _credentials = MaioCredentials.Create(serverExtras);

        if(_isInitialized) {
            if(MaioAds.canShow(_credentials.getZoneId())) {
                customEventInterstitialListener.onInterstitialLoaded();
            } else {
                customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
            }
            return;
        }

        MaioAds.init((Activity)context, _credentials.getMediaId(), _singletonMaioListener);
    }

    @Override
    protected void showInterstitial() {
        if(_isInitialized == false) return;

        MaioAds.show(_credentials.getZoneId());
    }

    @Override
    protected void onInvalidate() {
        _moPubListener = null;
    }
}
