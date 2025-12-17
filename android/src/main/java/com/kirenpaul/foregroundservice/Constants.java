package com.kirenpaul.foregroundservice;

/**
 * Constants used throughout the foreground service module
 */
class Constants {
    // Bundle keys
    static final String NOTIFICATION_CONFIG = "com.kirenpaul.foregroundservice.notif_config";
    static final String TASK_CONFIG = "com.kirenpaul.foregroundservice.task_config";

    // Service actions
    static final String ACTION_FOREGROUND_SERVICE_START = "com.kirenpaul.foregroundservice.service_start";
    static final String ACTION_FOREGROUND_SERVICE_STOP = "com.kirenpaul.foregroundservice.service_stop";
    static final String ACTION_FOREGROUND_SERVICE_STOP_ALL = "com.kirenpaul.foregroundservice.service_stop_all";
    static final String ACTION_FOREGROUND_RUN_TASK = "com.kirenpaul.foregroundservice.service_run_task";
    static final String ACTION_UPDATE_NOTIFICATION = "com.kirenpaul.foregroundservice.service_update_notification";

    // Error codes
    static final String ERROR_INVALID_CONFIG = "ERROR_INVALID_CONFIG";
    static final String ERROR_SERVICE_ERROR = "ERROR_SERVICE_ERROR";
    static final String ERROR_ANDROID_VERSION = "ERROR_ANDROID_VERSION";
    static final String ERROR_PERMISSION_DENIED = "ERROR_PERMISSION_DENIED";

    // Notification channel
    static final String NOTIFICATION_CHANNEL_ID = "com.kirenpaul.foregroundservice.channel";
}
