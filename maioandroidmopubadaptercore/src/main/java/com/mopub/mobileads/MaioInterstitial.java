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

    private MaioCredentials _credentials;

    @Override
    protected void loadInterstitial(Context context,
                                    final CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {
        if(context instanceof Activity == false) {
            writeLogPreLoad("Context must be type of Activity.");
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
            return;
        }

        _credentials = MaioCredentials.Create(serverExtras);

        if(_isInitialized) {
            if(MaioAds.canShow(_credentials.getZoneId())) {
                customEventInterstitialListener.onInterstitialLoaded();
            } else {
                customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
            }
            return;
        }

        MaioAds.init((Activity)context, _credentials.getMediaId(), new MaioAdsListener() {
            @Override
            public void onInitialized() {
                _isInitialized = true;
            }

            @Override
            public void onChangedCanShow(String zoneId, boolean newValue) {
                if(_credentials.getZoneId() != null && !_credentials.getZoneId().equals(zoneId)) {
                    return;
                }

                if(customEventInterstitialListener != null) {
                    if(newValue) {
                        customEventInterstitialListener.onInterstitialLoaded();
                    } else {
                        customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
                    }
                }
            }

            @Override
            public void onClosedAd(String zoneId) {
                if(_credentials.getZoneId() != null && !_credentials.getZoneId().equals(zoneId)) {
                    return;
                }

                if(customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialDismissed();
                }
            }

            @Override
            public void onClickedAd(String zoneId) {
                if(_credentials.getZoneId() != null && !_credentials.getZoneId().equals(zoneId)) {
                    return;
                }

                if(customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialClicked();
                }
            }

            @Override
            public void onFailed(FailNotificationReason failNotificationReason, String zoneId) {
                if(_credentials.getZoneId() != null && !_credentials.getZoneId().equals(zoneId)) {
                    return;
                }

                if(customEventInterstitialListener != null) {
                    MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                    customEventInterstitialListener.onInterstitialFailed(errorCode);
                }
            }

            @Override
            public void onStartedAd(String zoneId) {
                if(_credentials.getZoneId() != null && !_credentials.getZoneId().equals(zoneId)) {
                    return;
                }

                if(customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialShown();
                }
            }
        });
    }

    @Override
    protected void showInterstitial() {
        if(_isInitialized == false) return;

        MaioAds.show(_credentials.getZoneId());
    }

    @Override
    protected void onInvalidate() {
        // ignored
    }
}