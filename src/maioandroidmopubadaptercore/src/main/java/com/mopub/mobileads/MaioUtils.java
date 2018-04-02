package com.mopub.mobileads;

import android.util.Log;

import java.util.Locale;

import jp.maio.sdk.android.FailNotificationReason;

/**
 * Created by ichi-dohi on 2018/02/05.
 */

public class MaioUtils {

    static MoPubErrorCode getMoPubErrorCode(FailNotificationReason reason) {
        switch (reason) {
            case RESPONSE:
                return MoPubErrorCode.SERVER_ERROR;
            case NETWORK_NOT_READY:
                return MoPubErrorCode.NO_CONNECTION;
            case NETWORK:
                return MoPubErrorCode.INTERNAL_ERROR;
            case UNKNOWN:
                return MoPubErrorCode.UNSPECIFIED;
            case AD_STOCK_OUT:
                return MoPubErrorCode.NETWORK_NO_FILL;
            case VIDEO:
                return MoPubErrorCode.VIDEO_PLAYBACK_ERROR;
            default:
                throw new IllegalArgumentException("reason");
        }
    }
}
