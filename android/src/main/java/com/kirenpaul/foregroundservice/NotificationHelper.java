package com.kirenpaul.foregroundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Log;

/**
 * Helper class for building and managing notifications for foreground service
 *
 * Handles Android 12+ PendingIntent flag requirements:
 * - Main intent uses FLAG_IMMUTABLE (security best practice)
 * - Button intents use FLAG_MUTABLE (required for user interaction)
 */
class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String NOTIFICATION_CHANNEL_ID = Constants.NOTIFICATION_CHANNEL_ID;

    private static NotificationHelper instance = null;
    private final NotificationManager mNotificationManager;
    private final Context context;
    private final NotificationConfig config;

    private PendingIntent pendingBtnIntent;
    private PendingIntent pendingBtn2Intent;

    public static synchronized NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    private NotificationHelper(Context context) {
        this.context = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.config = new NotificationConfig(context);
    }

    /**
     * Build a notification from configuration bundle
     *
     * @param context Application context
     * @param bundle Configuration bundle from React Native
     * @return Configured notification or null if configuration is invalid
     */
    Notification buildNotification(Context context, Bundle bundle) {
        if (bundle == null) {
            Log.e(TAG, "buildNotification: invalid config - bundle is null");
            return null;
        }

        Class<?> mainActivityClass = getMainActivityClass(context);
        if (mainActivityClass == null) {
            Log.e(TAG, "buildNotification: unable to find main activity class");
            return null;
        }

        // Main notification intent - uses FLAG_IMMUTABLE for security
        Intent notificationIntent = new Intent(context, mainActivityClass);
        notificationIntent.putExtra("mainOnPress", bundle.getString("mainOnPress"));
        int uniqueInt1 = (int) (System.currentTimeMillis() & 0xfffffff);

        // CRITICAL FIX: Use FLAG_IMMUTABLE for main intent (Android 12+ security requirement)
        int mainIntentFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            uniqueInt1,
            notificationIntent,
            mainIntentFlags
        );

        // Button 1 intent - uses FLAG_MUTABLE (required for user interaction)
        if (bundle.getBoolean("button", false)) {
            Intent notificationBtnIntent = new Intent(context, mainActivityClass);
            notificationBtnIntent.putExtra("buttonOnPress", bundle.getString("buttonOnPress"));
            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

            // Button intents need FLAG_MUTABLE for Android 12+
            int buttonFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

            pendingBtnIntent = PendingIntent.getActivity(
                context,
                uniqueInt,
                notificationBtnIntent,
                buttonFlags
            );
        }

        // Button 2 intent - uses FLAG_MUTABLE (required for user interaction)
        if (bundle.getBoolean("button2", false)) {
            Intent notificationBtn2Intent = new Intent(context, mainActivityClass);
            notificationBtn2Intent.putExtra("button2OnPress", bundle.getString("button2OnPress"));
            int uniqueInt2 = (int) (System.currentTimeMillis() & 0xfffffff);

            // Button intents need FLAG_MUTABLE for Android 12+
            int buttonFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                : PendingIntent.FLAG_UPDATE_CURRENT;

            pendingBtn2Intent = PendingIntent.getActivity(
                context,
                uniqueInt2,
                notificationBtn2Intent,
                buttonFlags
            );
        }

        // Parse notification priority
        int priority = NotificationCompat.PRIORITY_HIGH;
        String priorityString = bundle.getString("importance");
        if (priorityString != null) {
            switch (priorityString.toLowerCase()) {
                case "max":
                    priority = NotificationCompat.PRIORITY_MAX;
                    break;
                case "high":
                    priority = NotificationCompat.PRIORITY_HIGH;
                    break;
                case "low":
                    priority = NotificationCompat.PRIORITY_LOW;
                    break;
                case "min":
                    priority = NotificationCompat.PRIORITY_MIN;
                    break;
                case "default":
                    priority = NotificationCompat.PRIORITY_DEFAULT;
                    break;
            }
        }

        // Parse notification visibility
        int visibility = NotificationCompat.VISIBILITY_PRIVATE;
        String visibilityString = bundle.getString("visibility");
        if (visibilityString != null) {
            switch (visibilityString.toLowerCase()) {
                case "private":
                    visibility = NotificationCompat.VISIBILITY_PRIVATE;
                    break;
                case "public":
                    visibility = NotificationCompat.VISIBILITY_PUBLIC;
                    break;
                case "secret":
                    visibility = NotificationCompat.VISIBILITY_SECRET;
                    break;
            }
        }

        // Create or update notification channel
        checkOrCreateChannel(mNotificationManager, bundle);

        // Build notification
        String title = bundle.getString("title", "Foreground Service");
        String message = bundle.getString("message", "Running...");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setVisibility(visibility)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setOngoing(bundle.getBoolean("ongoing", false));

        // Add action buttons
        if (bundle.getBoolean("button", false) && pendingBtnIntent != null) {
            String buttonText = bundle.getString("buttonText", "Button");
            // Use a simple icon for button - apps should provide their own
            int buttonIcon = android.R.drawable.ic_menu_info_details;
            notificationBuilder.addAction(buttonIcon, buttonText, pendingBtnIntent);
        }

        if (bundle.getBoolean("button2", false) && pendingBtn2Intent != null) {
            String button2Text = bundle.getString("button2Text", "Button");
            int buttonIcon = android.R.drawable.ic_menu_info_details;
            notificationBuilder.addAction(buttonIcon, button2Text, pendingBtn2Intent);
        }

        // Set notification color (Android 5.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int defaultColor = this.config.getNotificationColor();
            if (defaultColor != -1) {
                notificationBuilder.setColor(defaultColor);
            }
        }

        // Override with custom color if provided
        String color = bundle.getString("color");
        if (color != null && !color.isEmpty()) {
            try {
                notificationBuilder.setColor(Color.parseColor(color));
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Invalid color format: " + color);
            }
        }

        // Big text style for long messages
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        // Small icon
        String iconName = bundle.getString("icon");
        if (iconName == null || iconName.isEmpty()) {
            iconName = "ic_notification";
        }
        int iconResId = getResourceIdForResourceName(context, iconName);
        if (iconResId == 0) {
            // Fallback to app icon
            iconResId = context.getApplicationInfo().icon;
        }
        notificationBuilder.setSmallIcon(iconResId);

        // Large icon
        String largeIconName = bundle.getString("largeIcon");
        if (largeIconName == null || largeIconName.isEmpty()) {
            largeIconName = "ic_launcher";
        }
        int largeIconResId = getResourceIdForResourceName(context, largeIconName);
        if (largeIconResId != 0) {
            try {
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(context.getResources(), largeIconResId);
                if (largeIconBitmap != null) {
                    notificationBuilder.setLargeIcon(largeIconBitmap);
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to set large icon: " + e.getMessage());
            }
        }

        // Badge number
        String numberString = bundle.getString("number");
        if (numberString != null) {
            try {
                int numberInt = Integer.parseInt(numberString);
                if (numberInt > 0) {
                    notificationBuilder.setNumber(numberInt);
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Invalid number format: " + numberString);
            }
        }

        // Progress bar
        if (bundle.getBoolean("progressBar", false)) {
            double max = bundle.getDouble("progressBarMax", 100);
            double curr = bundle.getDouble("progressBarCurr", 0);
            notificationBuilder.setProgress((int) max, (int) curr, false);
        }

        // Set only alert once
        if (bundle.getBoolean("setOnlyAlertOnce", true)) {
            notificationBuilder.setOnlyAlertOnce(true);
        }

        return notificationBuilder.build();
    }

    /**
     * Get main activity class from package manager
     */
    private Class<?> getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null || launchIntent.getComponent() == null) {
            Log.e(TAG, "Failed to get launch intent or component");
            return null;
        }
        try {
            return Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Failed to get main activity class", e);
            return null;
        }
    }

    /**
     * Get resource ID for a given resource name
     */
    private int getResourceIdForResourceName(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
        }
        return resourceId;
    }

    /**
     * Create or update notification channel (Android 8.0+)
     */
    private void checkOrCreateChannel(NotificationManager manager, Bundle bundle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        if (manager == null) {
            return;
        }

        // Check if channel already exists
        NotificationChannel existingChannel = manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
        if (existingChannel != null) {
            // Channel exists, no need to recreate
            return;
        }

        // Parse importance level
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String importanceString = bundle.getString("importance");
        if (importanceString != null) {
            switch (importanceString.toLowerCase()) {
                case "default":
                    importance = NotificationManager.IMPORTANCE_DEFAULT;
                    break;
                case "max":
                    importance = NotificationManager.IMPORTANCE_MAX;
                    break;
                case "high":
                    importance = NotificationManager.IMPORTANCE_HIGH;
                    break;
                case "low":
                    importance = NotificationManager.IMPORTANCE_LOW;
                    break;
                case "min":
                    importance = NotificationManager.IMPORTANCE_MIN;
                    break;
                case "none":
                    importance = NotificationManager.IMPORTANCE_NONE;
                    break;
                case "unspecified":
                    importance = NotificationManager.IMPORTANCE_UNSPECIFIED;
                    break;
            }
        }

        // Create channel
        NotificationChannel channel = new NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            this.config.getChannelName(),
            importance
        );
        channel.setDescription(this.config.getChannelDescription());
        channel.enableLights(true);
        channel.enableVibration(bundle.getBoolean("vibration", false));
        channel.setShowBadge(true);

        manager.createNotificationChannel(channel);
        Log.d(TAG, "Notification channel created: " + NOTIFICATION_CHANNEL_ID);
    }
}
