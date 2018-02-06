package com.mopub.mobileads;

import android.util.Log;

import java.util.Locale;

import jp.maio.sdk.android.FailNotificationReason;

/**
 * Created by ichi-dohi on 2018/02/05.
 */

public class MaioUtils {
    public static boolean IsDebug = false;

    private static final String TAG = "MaioMoPubAdapter";
    private static final String PRE_LOAD_ZONE_ID = "PRE-LOAD";

    static void writeLogPreLoad(String message, Object... args) {
        writeLog(PRE_LOAD_ZONE_ID, message, args);
    }

    static void writeLog(String zoneId, String message, Object... args) {
        message = args.length == 0 ? message : String.format(Locale.ENGLISH, message, args);
        Log.d(TAG, String.format(Locale.ENGLISH, "[%s] %s", zoneId, message));
    }

    static void writeDebugLog(String zoneId, String message, Object... args) {
        if (IsDebug) writeLog(zoneId, message, args);
    }


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
