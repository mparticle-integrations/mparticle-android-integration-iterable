package com.mparticle.kits;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.WorkerThread;

import java.lang.reflect.Method;

class IterableDeviceIdHelper {
    @WorkerThread
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
}
