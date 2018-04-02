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
import static com.mopub.mobileads.MaioUtils.writeDebugLog;
import static com.mopub.mobileads.MaioUtils.writeLog;
import static com.mopub.mobileads.MaioUtils.writeLogPreLoad;
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
        writeDebugLog("", serverExtras.toString());

        if (serverExtras.size() == 0) {
            writeLogPreLoad("could not obtain server extras");
            return false;
        }

        try {
            _credentials = MaioCredentials.Create(serverExtras);
        } catch (IllegalArgumentException e) {
            writeLogPreLoad(e.getMessage());
            return false;
        }

        MaioAds.init(launcherActivity, _credentials.getMediaId(), new MaioAdsListener() {

            @Override
            public void onInitialized() {
                writeLogPreLoad("initialized");
                _isInitialized = true;
            }

            @Override
            public void onChangedCanShow(String zoneId, boolean canShow) {
                if (isTargetZone(zoneId) == false) return;
                writeDebugLog(zoneId, "can show changed to %b", canShow);
            }

            @Override
            public void onClickedAd(String zoneId) {
                if (isTargetZone(zoneId) == false) return;
                writeDebugLog(zoneId, "video clicked");

                onRewardedVideoClicked(MaioRewardedVideo.class, THIRD_PARTY_ID);
            }

            @Override
            public void onClosedAd(String zoneId) {
                if (isTargetZone(zoneId) == false) return;
                writeDebugLog(zoneId, "video closed");

                onRewardedVideoClosed(MaioRewardedVideo.class, THIRD_PARTY_ID);
            }

            @Override
            public void onFinishedAd(int playtime, boolean skipped, int duration, String zoneId) {
                if (isTargetZone(zoneId) == false) return;
                writeDebugLog(
                        zoneId,
                        "video play finished. playtime: %d / skipped: %b / duration: %d",
                        playtime, skipped, duration
                );

                MoPubReward reward = skipped
                        ? MoPubReward.failure()
                        : MoPubReward.success("", 0);
                onRewardedVideoCompleted(MaioRewardedVideo.class, THIRD_PARTY_ID, reward);
            }

            @Override
            public void onFailed(FailNotificationReason failNotificationReason, String zoneId) {
                if (isTargetZone(zoneId) == false) return;
                writeLog(zoneId, "video load failed:" + failNotificationReason.toString());

                MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                onRewardedVideoLoadFailure(MaioRewardedVideo.class, THIRD_PARTY_ID, errorCode);
            }

            @Override
            public void onStartedAd(String zoneId) {
                if (isTargetZone(zoneId) == false) return;
                writeDebugLog(zoneId, "started ad");

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
        if(_isInitialized == false) {
            writeLog(_credentials.getZoneId(), "loaded before initialization completed");
            onRewardedVideoLoadFailure(MaioRewardedVideo.class, THIRD_PARTY_ID, MoPubErrorCode.WARMUP);
            return;
        }

        if(MaioAds.canShow(_credentials.getZoneId())) {
            writeDebugLog(_credentials.getZoneId(), "video is ready");
            onRewardedVideoLoadSuccess(MaioRewardedVideo.class, THIRD_PARTY_ID);
        } else {
            writeLog(_credentials.getZoneId(), "video is not ready");
            onRewardedVideoLoadFailure(MaioRewardedVideo.class, THIRD_PARTY_ID, MoPubErrorCode.NO_FILL);
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
