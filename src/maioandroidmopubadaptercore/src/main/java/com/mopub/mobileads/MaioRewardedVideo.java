package com.mopub.mobileads;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;

import java.util.Map;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListener;

import static com.mopub.mobileads.MaioUtils.getMoPubErrorCode;
import static com.mopub.mobileads.MoPubRewardedVideoManager.onRewardedVideoClicked;
import static com.mopub.mobileads.MoPubRewardedVideoManager.onRewardedVideoClosed;
import static com.mopub.mobileads.MoPubRewardedVideoManager.onRewardedVideoCompleted;
import static com.mopub.mobileads.MoPubRewardedVideoManager.onRewardedVideoLoadFailure;
import static com.mopub.mobileads.MoPubRewardedVideoManager.onRewardedVideoLoadSuccess;
import static com.mopub.mobileads.MoPubRewardedVideoManager.onRewardedVideoStarted;

// equality with booleans have meaning!
// obviously, unused
@SuppressWarnings({"PointlessBooleanExpression", "unused"})
public class MaioRewardedVideo extends CustomEventRewardedVideo {

    private static final String THIRD_PARTY_ID = "maio";
    private MaioCredentials _credentials;

    private static boolean _isInitialized = false;

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {

        if (serverExtras.size() == 0) {
            return false;
        }

        try {
            _credentials = MaioCredentials.Create(serverExtras);
        } catch (IllegalArgumentException e) {
            return false;
        }

        MaioAds.init(launcherActivity, _credentials.getMediaId(), new MaioAdsListener() {

            @Override
            public void onInitialized() {
                _isInitialized = true;
            }

            @Override
            public void onChangedCanShow(String zoneId, boolean canShow) {
                if (isTargetZone(zoneId) == false) return;
                if(!canShow) {
                    onRewardedVideoLoadFailure(MaioRewardedVideo.class, THIRD_PARTY_ID, getMoPubErrorCode(FailNotificationReason.AD_STOCK_OUT));
                }
                onRewardedVideoLoadSuccess(MaioRewardedVideo.class, THIRD_PARTY_ID);
            }

            @Override
            public void onClickedAd(String zoneId) {
                if (isTargetZone(zoneId) == false) return;
                onRewardedVideoClicked(MaioRewardedVideo.class, THIRD_PARTY_ID);
            }

            @Override
            public void onClosedAd(String zoneId) {
                if (isTargetZone(zoneId) == false) return;

                onRewardedVideoClosed(MaioRewardedVideo.class, THIRD_PARTY_ID);
            }

            @Override
            public void onFinishedAd(int playtime, boolean skipped, int duration, String zoneId) {
                if (isTargetZone(zoneId) == false) return;

                MoPubReward reward = skipped
                        ? MoPubReward.failure()
                        : MoPubReward.success("", 0);
                onRewardedVideoCompleted(MaioRewardedVideo.class, THIRD_PARTY_ID, reward);
            }

            @Override
            public void onFailed(FailNotificationReason failNotificationReason, String zoneId) {
                if (isTargetZone(zoneId) == false) return;

                MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                onRewardedVideoLoadFailure(MaioRewardedVideo.class, THIRD_PARTY_ID, errorCode);
            }

            @Override
            public void onStartedAd(String zoneId) {
                if (isTargetZone(zoneId) == false) return;

                onRewardedVideoStarted(MaioRewardedVideo.class, THIRD_PARTY_ID);
            }
        });

        return true;
    }

    private boolean isTargetZone(String receivedZoneId) {
        String zoneId = _credentials.getZoneId();
        return zoneId == null || zoneId.equals(receivedZoneId);
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        if(_isInitialized && MaioAds.canShow(_credentials.getZoneId())) {
            onRewardedVideoLoadSuccess(MaioRewardedVideo.class, THIRD_PARTY_ID);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return THIRD_PARTY_ID;
    }

    @Override
    protected void onInvalidate() {
        // ignored; no invalidation required
    }

    @Override
    protected boolean hasVideoAvailable() {
        //noinspection SimplifiableIfStatement
        if(_isInitialized == false) return false;

        return MaioAds.canShow(_credentials.getZoneId());
    }

    @Override
    protected void showVideo() {
        if(_isInitialized == false) return;

        MaioAds.show(_credentials.getZoneId());
    }

}
