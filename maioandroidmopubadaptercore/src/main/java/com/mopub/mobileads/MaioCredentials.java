package com.mopub.mobileads;

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PointlessBooleanExpression")
class MaioCredentials {
    private String _mediaId;
    String getMediaId() {
        return _mediaId;
    }

    private String _zoneId;
    String getZoneId() {
        return _zoneId;
    }

    private MaioCredentials(@NonNull String mediaId, String zoneId) {
        _mediaId = mediaId;
        _zoneId = zoneId;
    }

    private static final String MEDIA_ID = "mediaId";
    private static final String ZONE_ID = "zoneId";

    static MaioCredentials Create(Map<String, String> serverExtras) throws IllegalArgumentException {
        String mediaId = serverExtras.get(MEDIA_ID);
        if (mediaId == null) {
            throw new IllegalArgumentException("could not obtain media ID from server extras");
        }
        if(isValidMaioId(mediaId) == false) {
            throw new IllegalArgumentException("media id is not a valid string");
        }
        String zoneId = serverExtras.get(ZONE_ID);
        if(zoneId != null && isValidMaioId(zoneId) == false) {
            throw new IllegalArgumentException("zone id is not a valid string");
        }

        return new MaioCredentials(mediaId, zoneId);
    }

    private static boolean isValidMaioId(@NonNull String uuidString) {
        if(uuidString.startsWith("Demo")) return true;
        try {
            @SuppressWarnings("unused") UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}