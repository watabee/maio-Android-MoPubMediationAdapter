package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListener;
import jp.maio.sdk.android.MaioAdsListenerInterface;

import static com.mopub.mobileads.MaioUtils.getMoPubErrorCode;

@SuppressWarnings({"PointlessBooleanExpression", "unused"})
public class MaioInterstitial extends CustomEventInterstitial {

    private MaioCredentials _credentials;
    private MaioAdsListenerInterface _listener;

    @Override
    protected void loadInterstitial(Context context,
                                    final CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {
        MaioUtils.trace();

        if (context instanceof Activity == false) {
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
            return;
        }

        _credentials = MaioCredentials.Create(serverExtras);

        if (MaioAdManager.getInstance().isInitialized()) {
            if (MaioAdManager.getInstance().canShow(_credentials.getZoneId())) {
                customEventInterstitialListener.onInterstitialLoaded();
            } else {
                customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
            }
            return;
        }

        _listener = new MaioAdsListener() {

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
        };
        MaioAdManager.getInstance().init((Activity) context, _credentials.getMediaId(), _listener);
    }

    @Override
    protected void showInterstitial() {
        MaioUtils.trace();

        if (MaioAdManager.getInstance().isInitialized() == false) {
            return;
        }

        MaioAdManager.getInstance().show(_credentials.getZoneId());
    }

    @Override
    protected void onInvalidate() {
        MaioUtils.trace();

        MaioAdManager.getInstance().removeListener(_listener);
    }
}