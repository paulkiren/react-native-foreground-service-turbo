package com.kirenpaul.foregroundservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.util.Log;

/**
 * Reads notification configuration from AndroidManifest.xml meta-data
 *
 * Configuration is read from application meta-data tags:
 * - com.kirenpaul.foregroundservice.notification_channel_name
 * - com.kirenpaul.foregroundservice.notification_channel_description
 * - com.kirenpaul.foregroundservice.notification_color
 */
class NotificationConfig {

    private static final String TAG = "NotificationConfig";
    private static final String KEY_CHANNEL_NAME = "com.kirenpaul.foregroundservice.notification_channel_name";
    private static final String KEY_CHANNEL_DESCRIPTION = "com.kirenpaul.foregroundservice.notification_channel_description";
    private static final String KEY_NOTIFICATION_COLOR = "com.kirenpaul.foregroundservice.notification_color";

    private static Bundle metadata;
    private final Context context;

    public NotificationConfig(Context context) {
        this.context = context;
        if (metadata == null) {
            try {
                ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                metadata = applicationInfo.metaData;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Error reading application meta-data, falling back to defaults", e);
                metadata = new Bundle();
            }
        }
    }

    public String getChannelName() {
        try {
            String name = metadata.getString(KEY_CHANNEL_NAME);
            if (name != null && !name.isEmpty()) {
                return name;
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to find " + KEY_CHANNEL_NAME + " in manifest. Falling back to default");
        }
        // Default
        return "Foreground Service";
    }

    public String getChannelDescription() {
        try {
            String description = metadata.getString(KEY_CHANNEL_DESCRIPTION);
            if (description != null && !description.isEmpty()) {
                return description;
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to find " + KEY_CHANNEL_DESCRIPTION + " in manifest. Falling back to default");
        }
        // Default
        return "Persistent notification for foreground service";
    }

    public int getNotificationColor() {
        try {
            int resourceId = metadata.getInt(KEY_NOTIFICATION_COLOR);
            if (resourceId != 0) {
                return ResourcesCompat.getColor(context.getResources(), resourceId, null);
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to find " + KEY_NOTIFICATION_COLOR + " in manifest. Falling back to default");
        }
        // Default: return -1 to indicate no color set
        return -1;
    }
}
