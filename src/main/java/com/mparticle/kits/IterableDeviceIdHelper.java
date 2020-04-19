package com.mparticle.kits;

import android.content.Context;
import android.provider.Settings;

import java.lang.reflect.Method;

class IterableDeviceIdHelper {
    static String getGoogleAdId(Context context) {
        try {
            Class AdvertisingIdClient = Class
                    .forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
            Method getAdvertisingInfo = AdvertisingIdClient.getMethod("getAdvertisingIdInfo",
                    Context.class);
            Object advertisingInfo = getAdvertisingInfo.invoke(null, context);
            Method isLimitAdTrackingEnabled = advertisingInfo.getClass().getMethod(
                    "isLimitAdTrackingEnabled");
            Boolean limitAdTrackingEnabled = (Boolean) isLimitAdTrackingEnabled
                    .invoke(advertisingInfo);
            Method getId = advertisingInfo.getClass().getMethod("getId");
            String advertisingId = (String) getId.invoke(advertisingInfo);
            if (!limitAdTrackingEnabled) {
                return advertisingId;
            }
        } catch (Exception ignored) {}
        return null;
    }

    static String getAndroidID(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignored) {}
        return null;
    }
}
