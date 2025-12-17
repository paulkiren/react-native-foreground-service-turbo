package com.kirenpaul.foregroundservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.module.annotations.ReactModule;

import java.util.HashMap;
import java.util.Map;

/**
 * TurboModule implementation for Foreground Service
 *
 * This module bridges JavaScript to Android native foreground service functionality.
 * Implements the Spec interface defined in NativeForegroundService.ts
 *
 * Key features:
 * - Android 13+ POST_NOTIFICATIONS permission checking
 * - Android 14+ foreground service type validation
 * - Full error handling and validation
 * - TurboModule architecture for React Native New Architecture
 */
@ReactModule(name = ForegroundServiceModule.NAME)
public class ForegroundServiceModule extends NativeForegroundServiceSpec {

    public static final String NAME = "ForegroundService";
    private static final String TAG = "ForegroundServiceModule";

    private final ReactApplicationContext reactContext;
    private final PermissionChecker permissionChecker;

    public ForegroundServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.permissionChecker = new PermissionChecker(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Get module constants
     */
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("MIN_SDK", 25);
        constants.put("TARGET_SDK", 34);
        return constants;
    }

    /**
     * Start the foreground service with notification
     *
     * @param notificationConfig Notification configuration from JavaScript
     * @param promise Promise to resolve/reject
     */
    @ReactMethod
    public void startService(ReadableMap notificationConfig, Promise promise) {
        // Validate configuration
        if (!validateConfig(notificationConfig, promise)) {
            return;
        }

        // Check POST_NOTIFICATIONS permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!permissionChecker.hasPostNotificationsPermission()) {
                promise.reject(
                    Constants.ERROR_PERMISSION_DENIED,
                    "POST_NOTIFICATIONS permission not granted. " +
                    "Please request this permission before starting the service."
                );
                return;
            }
        }

        // Validate service type for Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (!notificationConfig.hasKey("serviceType")) {
                promise.reject(
                    Constants.ERROR_INVALID_CONFIG,
                    "serviceType is required for Android 14+. " +
                    "Please specify: 'dataSync', 'location', or 'mediaPlayback'"
                );
                return;
            }

            String serviceType = notificationConfig.getString("serviceType");
            if (!permissionChecker.hasForegroundServicePermission(serviceType)) {
                promise.reject(
                    Constants.ERROR_PERMISSION_DENIED,
                    permissionChecker.getPermissionErrorMessage(serviceType)
                );
                return;
            }
        }

        try {
            Intent intent = new Intent(reactContext, ForegroundService.class);
            intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_START);
            intent.putExtra(Constants.NOTIFICATION_CONFIG, Arguments.toBundle(notificationConfig));

            // Use startForegroundService for Android O+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                reactContext.startForegroundService(intent);
            } else {
                reactContext.startService(intent);
            }

            promise.resolve(null);
        } catch (IllegalStateException | SecurityException e) {
            promise.reject(
                Constants.ERROR_SERVICE_ERROR,
                "Failed to start foreground service: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Stop the foreground service (decrements internal counter)
     *
     * @param promise Promise to resolve/reject
     */
    @ReactMethod
    public void stopService(Promise promise) {
        Intent intent = new Intent(reactContext, ForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_STOP);

        try {
            // Send stop action via startService (service will handle decrement and stop if needed)
            reactContext.startService(intent);
            promise.resolve(null);
        } catch (IllegalStateException e) {
            // If startService fails, try stopService as fallback
            try {
                reactContext.stopService(intent);
                promise.resolve(null);
            } catch (Exception e2) {
                promise.reject(
                    Constants.ERROR_SERVICE_ERROR,
                    "Service stop failed: " + e2.getMessage(),
                    e2
                );
            }
        }
    }

    /**
     * Force stop the foreground service regardless of start counter
     *
     * @param promise Promise to resolve/reject
     */
    @ReactMethod
    public void stopServiceAll(Promise promise) {
        Intent intent = new Intent(reactContext, ForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_STOP_ALL);

        try {
            reactContext.startService(intent);
            promise.resolve(null);
        } catch (IllegalStateException e) {
            try {
                reactContext.stopService(intent);
                promise.resolve(null);
            } catch (Exception e2) {
                promise.reject(
                    Constants.ERROR_SERVICE_ERROR,
                    "Service stop all failed: " + e2.getMessage(),
                    e2
                );
            }
        }
    }

    /**
     * Update notification of running service
     *
     * @param notificationConfig Updated notification configuration
     * @param promise Promise to resolve/reject
     */
    @ReactMethod
    public void updateNotification(ReadableMap notificationConfig, Promise promise) {
        if (!validateConfig(notificationConfig, promise)) {
            return;
        }

        try {
            Intent intent = new Intent(reactContext, ForegroundService.class);
            intent.setAction(Constants.ACTION_UPDATE_NOTIFICATION);
            intent.putExtra(Constants.NOTIFICATION_CONFIG, Arguments.toBundle(notificationConfig));

            ComponentName componentName = reactContext.startService(intent);
            if (componentName != null) {
                promise.resolve(null);
            } else {
                promise.reject(
                    Constants.ERROR_SERVICE_ERROR,
                    "Update notification failed - service did not start"
                );
            }
        } catch (IllegalStateException | SecurityException e) {
            promise.reject(
                Constants.ERROR_SERVICE_ERROR,
                "Update notification failed: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Check if service is running
     *
     * @param promise Promise that resolves to running counter value
     */
    @ReactMethod
    public void isRunning(Promise promise) {
        ForegroundService instance = ForegroundService.getInstance();
        int res = 0;
        if (instance != null) {
            res = instance.isRunning();
        }
        promise.resolve(res);
    }

    /**
     * Run a headless task
     *
     * @param taskConfig Task configuration from JavaScript
     * @param promise Promise to resolve/reject
     */
    @ReactMethod
    public void runTask(ReadableMap taskConfig, Promise promise) {
        if (!taskConfig.hasKey("taskName")) {
            promise.reject(Constants.ERROR_INVALID_CONFIG, "taskName is required");
            return;
        }

        if (!taskConfig.hasKey("delay")) {
            promise.reject(Constants.ERROR_INVALID_CONFIG, "delay is required");
            return;
        }

        try {
            Intent intent = new Intent(reactContext, ForegroundService.class);
            intent.setAction(Constants.ACTION_FOREGROUND_RUN_TASK);
            intent.putExtra(Constants.TASK_CONFIG, Arguments.toBundle(taskConfig));

            ComponentName componentName = reactContext.startService(intent);
            if (componentName != null) {
                promise.resolve(null);
            } else {
                promise.reject(
                    Constants.ERROR_SERVICE_ERROR,
                    "Failed to run task: Service did not start"
                );
            }
        } catch (IllegalStateException | SecurityException e) {
            promise.reject(
                Constants.ERROR_SERVICE_ERROR,
                "Failed to run task: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Cancel a specific notification by ID
     *
     * @param id Notification ID to cancel
     * @param promise Promise to resolve/reject
     */
    @ReactMethod
    public void cancelNotification(double id, Promise promise) {
        try {
            int notificationId = (int) id;
            android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) reactContext.getSystemService(
                    reactContext.NOTIFICATION_SERVICE
                );

            if (mNotificationManager != null) {
                mNotificationManager.cancel(notificationId);
                promise.resolve(null);
            } else {
                promise.reject(
                    Constants.ERROR_SERVICE_ERROR,
                    "Failed to get NotificationManager"
                );
            }
        } catch (Exception e) {
            promise.reject(
                Constants.ERROR_SERVICE_ERROR,
                "Failed to cancel notification: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Check if POST_NOTIFICATIONS permission is granted (Android 13+)
     *
     * @param promise Promise that resolves to boolean
     */
    @ReactMethod
    public void checkPostNotificationsPermission(Promise promise) {
        boolean hasPermission = permissionChecker.hasPostNotificationsPermission();
        promise.resolve(hasPermission);
    }

    /**
     * Validate notification configuration
     *
     * @param config Configuration to validate
     * @param promise Promise to reject if invalid
     * @return true if valid, false otherwise
     */
    private boolean validateConfig(ReadableMap config, Promise promise) {
        if (config == null) {
            promise.reject(
                Constants.ERROR_INVALID_CONFIG,
                "Notification config is invalid - config is null"
            );
            return false;
        }

        if (!config.hasKey("id")) {
            promise.reject(
                Constants.ERROR_INVALID_CONFIG,
                "Notification config is invalid - id is required"
            );
            return false;
        }

        if (!config.hasKey("title")) {
            promise.reject(
                Constants.ERROR_INVALID_CONFIG,
                "Notification config is invalid - title is required"
            );
            return false;
        }

        if (!config.hasKey("message")) {
            promise.reject(
                Constants.ERROR_INVALID_CONFIG,
                "Notification config is invalid - message is required"
            );
            return false;
        }

        return true;
    }
}
