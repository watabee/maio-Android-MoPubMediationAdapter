package com.mopub.mobileads;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListenerInterface;

public class MaioAdManager {

    private MaioAdManager() {
    }

    public static MaioAdManager getInstance() {
        MaioUtils.trace();

        if (_instance == null) {
            _instance = new MaioAdManager();
        }
        return _instance;
    }


    private static MaioAdManager _instance;
    private List<MaioAdsListenerInterface> _listeners = new ArrayList<>();
    private boolean _isInitialized = false;

    public void init(@NonNull Activity activity, @NonNull String mediaEid, @NonNull MaioAdsListenerInterface listener) {
        MaioUtils.trace();

        _listeners.add(listener);
        if (_isInitialized) return;

        MaioAds.init(activity, mediaEid, new MaioAdsListenerInterface() {
            @Override
            public void onInitialized() {
                _isInitialized = true;
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onInitialized();
                    }
                });
            }

            @Override
            public void onChangedCanShow(final String zoneId, final boolean newValue) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onChangedCanShow(zoneId, newValue);
                    }
                });
            }

            @Override
            public void onOpenAd(final String zoneId) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onOpenAd(zoneId);
                    }
                });
            }

            @Override
            public void onClosedAd(final String zoneId) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onClosedAd(zoneId);
                    }
                });
            }

            @Override
            public void onStartedAd(final String zoneId) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onStartedAd(zoneId);
                    }
                });
            }

            @Override
            public void onFinishedAd(final int playTime, final boolean skipped, final int duration, final String zoneId) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onFinishedAd(playTime, skipped, duration, zoneId);
                    }
                });
            }

            @Override
            public void onClickedAd(final String zoneId) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onClickedAd(zoneId);
                    }
                });
            }

            @Override
            public void onFailed(final FailNotificationReason failNotificationReason, final String zoneId) {
                invokeAllListeners(new Action<MaioAdsListenerInterface>() {
                    @Override
                    public void invoke(MaioAdsListenerInterface item) {
                        item.onFailed(failNotificationReason, zoneId);
                    }
                });
            }
        });
    }

    private void invokeAllListeners(Action<MaioAdsListenerInterface> action) {
        MaioUtils.trace();

        for (MaioAdsListenerInterface listener : _listeners) {
            action.invoke(listener);
        }
    }

    public boolean isInitialized() {
        MaioUtils.trace();

        return _isInitialized;
    }


    public boolean canShow(String zoneId) {
        MaioUtils.trace();

        return MaioAds.canShow(zoneId);
    }

    public void removeListener(@NonNull MaioAdsListenerInterface listener) {
        MaioUtils.trace();

        _listeners.remove(listener);
    }

    public void show(String zoneId) {
        MaioUtils.trace();

        MaioAds.show(zoneId);
    }

    private interface Action<T> {
        void invoke(T item);
    }
}
