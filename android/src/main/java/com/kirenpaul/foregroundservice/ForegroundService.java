package com.kirenpaul.foregroundservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import static com.kirenpaul.foregroundservice.Constants.NOTIFICATION_CONFIG;
import static com.kirenpaul.foregroundservice.Constants.TASK_CONFIG;

/**
 * Main foreground service implementation with Android 14 support
 *
 * Features:
 * - Singleton pattern for easy access
 * - Internal start counter for multiple start/stop calls
 * - Android 14+ foreground service type handling
 * - Headless task execution support
 * - Proper lifecycle management and cleanup
 */
public class ForegroundService extends Service {

    private static final String TAG = "ForegroundService";
    private static ForegroundService mInstance = null;
    private static Bundle lastNotificationConfig = null;

    private int running = 0;
    private Handler handler;
    private Context context;
    private Runnable runnableCode;
    private Bundle taskConfig;

    /**
     * Check if service instance exists and is running
     */
    public static boolean isServiceCreated() {
        try {
            return mInstance != null && mInstance.ping();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Get singleton instance of the service
     */
    public static ForegroundService getInstance() {
        if (isServiceCreated()) {
            return mInstance;
        }
        return null;
    }

    /**
     * Get running counter value
     */
    public int isRunning() {
        return running;
    }

    /**
     * Simple ping method to check if service is alive
     */
    private boolean ping() {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        running = 0;
        mInstance = this;
        context = this;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");

        // Clean up handler callbacks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        running = 0;
        mInstance = null;
        lastNotificationConfig = null;

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start the foreground service with notification
     *
     * @param notificationConfig Bundle containing notification configuration
     * @return true if service started successfully, false otherwise
     */
    private boolean startService(Bundle notificationConfig) {
        try {
            int id = (int) notificationConfig.getDouble("id");

            Notification notification = NotificationHelper
                .getInstance(context)
                .buildNotification(context, notificationConfig);

            if (notification == null) {
                Log.e(TAG, "Failed to build notification");
                return false;
            }

            // Android 14+ requires explicit service type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                String serviceType = notificationConfig.getString("serviceType", "dataSync");
                int serviceTypeFlag = ServiceTypeManager.getServiceTypeFlag(serviceType);

                Log.d(TAG, String.format(
                    "Starting foreground service with type: %s (flag: %d)",
                    serviceType, serviceTypeFlag
                ));

                startForeground(id, notification, serviceTypeFlag);
            } else {
                startForeground(id, notification);
            }

            running += 1;
            lastNotificationConfig = notificationConfig;

            Log.d(TAG, "Foreground service started successfully. Running count: " + running);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to start foreground service", e);
            return false;
        }
    }

    /**
     * Initialize the looping task runner
     */
    private void initializeTaskRunner() {
        runnableCode = new Runnable() {
            @Override
            public void run() {
                if (!isServiceCreated() || running <= 0) {
                    Log.d(TAG, "Task runner stopped - service not running");
                    return;
                }

                try {
                    final Intent service = new Intent(context, ForegroundServiceTask.class);
                    service.putExtras(taskConfig);
                    context.startService(service);

                    int loopDelay = (int) taskConfig.getDouble("loopDelay", 5000);
                    handler.postDelayed(this, loopDelay);
                } catch (Exception e) {
                    Log.e(TAG, "Error in task runner", e);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.w(TAG, "onStartCommand called with null intent");
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            Log.w(TAG, "onStartCommand called with null action");
            return START_NOT_STICKY;
        }

        Log.d(TAG, "onStartCommand called with action: " + action);

        switch (action) {
            case Constants.ACTION_FOREGROUND_SERVICE_START:
                handleStartService(intent);
                break;

            case Constants.ACTION_UPDATE_NOTIFICATION:
                handleUpdateNotification(intent);
                break;

            case Constants.ACTION_FOREGROUND_RUN_TASK:
                handleRunTask(intent);
                break;

            case Constants.ACTION_FOREGROUND_SERVICE_STOP:
                handleStopService();
                return START_NOT_STICKY;

            case Constants.ACTION_FOREGROUND_SERVICE_STOP_ALL:
                handleStopServiceAll();
                return START_NOT_STICKY;

            default:
                Log.w(TAG, "Unknown action: " + action);
        }

        // Service should restart automatically if it's killed
        return START_REDELIVER_INTENT;
    }

    /**
     * Handle ACTION_FOREGROUND_SERVICE_START
     */
    private void handleStartService(Intent intent) {
        if (intent.getExtras() != null && intent.getExtras().containsKey(NOTIFICATION_CONFIG)) {
            Bundle notificationConfig = intent.getExtras().getBundle(NOTIFICATION_CONFIG);
            if (notificationConfig != null) {
                startService(notificationConfig);
            }
        }
    }

    /**
     * Handle ACTION_UPDATE_NOTIFICATION
     */
    private void handleUpdateNotification(Intent intent) {
        if (intent.getExtras() == null || !intent.getExtras().containsKey(NOTIFICATION_CONFIG)) {
            return;
        }

        Bundle notificationConfig = intent.getExtras().getBundle(NOTIFICATION_CONFIG);
        if (notificationConfig == null) {
            return;
        }

        if (running <= 0) {
            Log.d(TAG, "Update notification called without running service, trying to restart");
            startService(notificationConfig);
        } else {
            try {
                int id = (int) notificationConfig.getDouble("id");

                Notification notification = NotificationHelper
                    .getInstance(context)
                    .buildNotification(context, notificationConfig);

                if (notification != null) {
                    NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (mNotificationManager != null) {
                        mNotificationManager.notify(id, notification);
                        lastNotificationConfig = notificationConfig;
                        Log.d(TAG, "Notification updated successfully");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to update notification", e);
            }
        }
    }

    /**
     * Handle ACTION_FOREGROUND_RUN_TASK
     */
    private void handleRunTask(Intent intent) {
        if (intent.getExtras() == null || !intent.getExtras().containsKey(TASK_CONFIG)) {
            Log.w(TAG, "Run task called without task config");
            return;
        }

        // Check if service is running
        if (running <= 0 && lastNotificationConfig == null) {
            Log.e(TAG, "Service is not running to execute tasks");
            stopSelf();
            return;
        }

        // Try to restart service if it was killed
        if (running <= 0) {
            Log.d(TAG, "Run task called without running service, trying to restart");
            if (!startService(lastNotificationConfig)) {
                Log.e(TAG, "Failed to restart service for task execution");
                return;
            }
        }

        taskConfig = intent.getExtras().getBundle(TASK_CONFIG);
        if (taskConfig == null) {
            Log.w(TAG, "Task config bundle is null");
            return;
        }

        try {
            boolean onLoop = taskConfig.getBoolean("onLoop", false);

            if (onLoop) {
                // Start looping task runner
                initializeTaskRunner();
                if (runnableCode != null) {
                    handler.post(runnableCode);
                    Log.d(TAG, "Started looping task runner");
                }
            } else {
                // Execute one-time task
                runHeadlessTask(taskConfig);
                Log.d(TAG, "Executed one-time headless task");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start task", e);
        }
    }

    /**
     * Handle ACTION_FOREGROUND_SERVICE_STOP
     */
    private void handleStopService() {
        if (running > 0) {
            running -= 1;
            Log.d(TAG, "Service stop requested. Running count: " + running);

            if (running == 0) {
                Log.d(TAG, "Stopping foreground service");
                cleanupResources();
                stopSelf();
                lastNotificationConfig = null;
            }
        } else {
            Log.d(TAG, "Service is not running, stopping anyway");
            cleanupResources();
            stopSelf();
            lastNotificationConfig = null;
        }
    }

    /**
     * Handle ACTION_FOREGROUND_SERVICE_STOP_ALL
     */
    private void handleStopServiceAll() {
        Log.d(TAG, "Force stopping foreground service");
        running = 0;
        cleanupResources();
        mInstance = null;
        lastNotificationConfig = null;
        stopSelf();
    }

    /**
     * Clean up all resources (handler callbacks, tasks, etc.)
     */
    private void cleanupResources() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            Log.d(TAG, "Handler callbacks cleared");
        }
        if (runnableCode != null) {
            runnableCode = null;
        }
        taskConfig = null;
    }

    /**
     * Run a one-time headless task with optional delay
     *
     * @param bundle Task configuration bundle
     */
    private void runHeadlessTask(Bundle bundle) {
        final Intent service = new Intent(context, ForegroundServiceTask.class);
        service.putExtras(bundle);

        int delay = (int) bundle.getDouble("delay", 0);

        if (delay <= 0) {
            // Execute immediately
            context.startService(service);
        } else {
            // Execute after delay
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (running <= 0) {
                        Log.d(TAG, "Service stopped before delayed task could execute");
                        return;
                    }
                    try {
                        context.startService(service);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to start delayed headless task", e);
                    }
                }
            }, delay);
        }
    }
}
