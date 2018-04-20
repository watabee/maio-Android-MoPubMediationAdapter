package com.mopub.mobileads;

import android.util.Log;

import java.util.Locale;

import jp.maio.sdk.android.FailNotificationReason;

/**
 * Created by ichi-dohi on 2018/02/05.
 */

@SuppressWarnings("PointlessBooleanExpression")
public class MaioUtils {

    private static final boolean IS_DEVELOP = false;

    static void writeDevelopLog(String tag, String message) {
        if (IS_DEVELOP == false) {
            return;
        }

        Log.d(tag, message);
    }

    static void trace() {
        if (IS_DEVELOP == false) {
            return;
        }

        if(Thread.currentThread().getStackTrace().length < 2) {
            throw new InternalError("stack trace not enough");
        }

        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();

        Log.d("[TRACE]", String.format("%s.%s", className, methodName));

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
