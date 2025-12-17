/**
 * Service types for Android 14+ foreground services
 */
export type ServiceType = 'dataSync' | 'location' | 'mediaPlayback';

/**
 * Notification visibility levels
 */
export type NotificationVisibility = 'private' | 'public' | 'secret';

/**
 * Notification importance levels
 */
export type NotificationImportance = 'none' | 'min' | 'low' | 'default' | 'high' | 'max';

/**
 * Notification button configuration
 */
export interface NotificationButton {
  /**
   * Button text displayed in notification
   */
  text: string;
  /**
   * Event identifier sent when button is pressed
   */
  onPressEvent: string;
}

/**
 * Progress bar configuration for notifications
 */
export interface ProgressConfig {
  /**
   * Maximum progress value
   */
  max: number;
  /**
   * Current progress value
   */
  curr: number;
}

/**
 * Configuration for starting or updating a foreground service
 */
export interface StartServiceConfig {
  /**
   * Unique notification ID
   */
  id: number;

  /**
   * Notification title
   * @default id (converted to string)
   */
  title?: string;

  /**
   * Notification message/content text
   * @default 'Foreground Service Running...'
   */
  message?: string;

  /**
   * Enable vibration for notification
   * @default false
   */
  vibration?: boolean;

  /**
   * Notification visibility level
   * @default 'public'
   */
  visibility?: NotificationVisibility;

  /**
   * Small icon resource name (must exist in android/app/src/main/res/drawable or mipmap)
   * @default 'ic_notification'
   */
  icon?: string;

  /**
   * Large icon resource name (must exist in android/app/src/main/res/drawable or mipmap)
   * @default 'ic_launcher'
   */
  largeIcon?: string;

  /**
   * Notification importance and priority level
   * @default 'max'
   */
  importance?: NotificationImportance;

  /**
   * Badge number for notification (device dependent)
   * @default '1'
   */
  number?: string;

  /**
   * First action button configuration
   */
  button?: NotificationButton;

  /**
   * Second action button configuration
   */
  button2?: NotificationButton;

  /**
   * Event identifier sent when main notification is tapped
   * @default 'mainOnPress'
   */
  mainOnPress?: string;

  /**
   * Progress bar configuration
   */
  progress?: ProgressConfig;

  /**
   * Notification color (hex format: #RRGGBB)
   */
  color?: string;

  /**
   * Only alert once for this notification
   * @default true
   */
  setOnlyAlertOnce?: boolean;

  /**
   * Make notification ongoing (cannot be dismissed by user)
   * Note: The main foreground service notification is always ongoing
   * @default false
   */
  ongoing?: boolean;

  /**
   * Foreground service type (REQUIRED for Android 14+)
   *
   * - 'dataSync': For data synchronization tasks
   * - 'location': For location tracking (requires FOREGROUND_SERVICE_LOCATION permission)
   * - 'mediaPlayback': For media playback (requires FOREGROUND_SERVICE_MEDIA_PLAYBACK permission)
   *
   * @default 'dataSync'
   */
  serviceType?: ServiceType;
}

/**
 * Task configuration options
 */
export interface TaskOptions {
  /**
   * Delay before first execution (milliseconds)
   * @default 5000
   */
  delay?: number;

  /**
   * Whether task should repeat
   * @default true
   */
  onLoop?: boolean;

  /**
   * Unique task identifier
   * @default auto-generated
   */
  taskId?: string;

  /**
   * Callback called when task completes successfully
   */
  onSuccess?: () => void;

  /**
   * Callback called when task encounters an error
   */
  onError?: (error: Error) => void;
}

/**
 * Internal task representation
 * @internal
 */
export interface Task extends TaskOptions {
  /**
   * Task function to execute
   */
  task: () => Promise<void> | void;

  /**
   * Next scheduled execution time (timestamp)
   * @internal
   */
  nextExecutionTime: number;

  /**
   * Actual delay used (rounded to sampling interval)
   * @internal
   */
  delay: number;
}

/**
 * Notification click event data
 */
export interface NotificationClickEvent {
  /**
   * Main notification press event (if main notification was tapped)
   */
  main?: string;

  /**
   * Button press event (if button 1 was tapped)
   */
  button?: string;

  /**
   * Button 2 press event (if button 2 was tapped)
   */
  button2?: string;
}

/**
 * Event listener cleanup function
 */
export type EventListenerCleanup = () => void;
