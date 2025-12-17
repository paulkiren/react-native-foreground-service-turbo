package com.kirenpaul.foregroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Broadcast receiver for handling notification button clicks
 *
 * This receiver captures notification interactions and sends them
 * to React Native via DeviceEventEmitter
 */
public class NotificationEventReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationEventReceiver";

    public static final String ACTION_NOTIFICATION_BUTTON = "com.kirenpaul.foregroundservice.NOTIFICATION_BUTTON";
    public static final String ACTION_NOTIFICATION_MAIN = "com.kirenpaul.foregroundservice.NOTIFICATION_MAIN";

    public static final String EXTRA_EVENT_TYPE = "eventType";
    public static final String EXTRA_EVENT_DATA = "eventData";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        Log.d(TAG, "Notification event received: " + action);

        try {
            WritableMap eventData = Arguments.createMap();

            if (ACTION_NOTIFICATION_BUTTON.equals(action)) {
                String buttonPress = intent.getStringExtra("buttonOnPress");
                String button2Press = intent.getStringExtra("button2OnPress");

                if (buttonPress != null) {
                    eventData.putString("button", buttonPress);
                }
                if (button2Press != null) {
                    eventData.putString("button2", button2Press);
                }
            } else if (ACTION_NOTIFICATION_MAIN.equals(action)) {
                String mainPress = intent.getStringExtra("mainOnPress");
                if (mainPress != null) {
                    eventData.putString("main", mainPress);
                }
            }

            sendEventToReactNative(context, eventData);
        } catch (Exception e) {
            Log.e(TAG, "Error handling notification event", e);
        }
    }

    /**
     * Send event to React Native via DeviceEventEmitter
     */
    private void sendEventToReactNative(Context context, WritableMap eventData) {
        try {
            ReactApplication reactApplication = (ReactApplication) context.getApplicationContext();
            ReactInstanceManager reactInstanceManager = reactApplication.getReactNativeHost().getReactInstanceManager();
            ReactContext reactContext = reactInstanceManager.getCurrentReactContext();

            if (reactContext != null && reactContext.hasActiveReactInstance()) {
                reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("notificationClickHandle", eventData);

                Log.d(TAG, "Event sent to React Native: " + eventData.toString());
            } else {
                Log.w(TAG, "React Native context not available, event not sent");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to send event to React Native", e);
        }
    }
}
