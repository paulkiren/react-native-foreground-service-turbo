import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

/**
 * Notification configuration for foreground service
 */
export interface NotificationConfig {
  id: number;
  title: string;
  message: string;
  vibration?: boolean;
  visibility?: "private" | "public" | "secret";
  icon?: string;
  largeIcon?: string;
  importance?: "none" | "min" | "low" | "default" | "high" | "max";
  number?: string;
  button?: boolean;
  buttonText?: string;
  buttonOnPress?: string;
  button2?: boolean;
  button2Text?: string;
  button2OnPress?: string;
  mainOnPress?: string;
  progressBar?: boolean;
  progressBarMax?: number;
  progressBarCurr?: number;
  color?: string;
  setOnlyAlertOnce?: boolean;
  ongoing?: boolean;
  /**
   * Service type for Android 14+ (API 34+)
   * Required for Android 14 and above
   * @default 'dataSync'
   */
  serviceType?: "dataSync" | "location" | "mediaPlayback";
}

/**
 * Task configuration for headless task execution
 */
export interface TaskConfig {
  taskName: string;
  delay: number;
  loopDelay?: number;
  onLoop?: boolean;
  /**
   * Task timeout in milliseconds
   * @default 60000 (60 seconds)
   */
  timeout?: number;
}

/**
 * Native Foreground Service TurboModule Specification
 *
 * This module provides Android foreground service capabilities with:
 * - Persistent notification management
 * - Headless task execution
 * - Android 13+ POST_NOTIFICATIONS permission handling
 * - Android 14+ foreground service type support
 */
export interface Spec extends TurboModule {
  /**
   * Start the foreground service with a notification
   *
   * @param config Notification configuration
   * @returns Promise that resolves when service starts successfully
   * @throws Error if configuration is invalid or permissions are missing
   *
   * @example
   * ```typescript
   * await ForegroundService.startService({
   *   id: 1,
   *   title: 'My Service',
   *   message: 'Running...',
   *   serviceType: 'dataSync' // Required for Android 14+
   * });
   * ```
   */
  startService(config: NotificationConfig): Promise<void>;

  /**
   * Stop the foreground service (decrements internal counter)
   *
   * @returns Promise that resolves when service stops
   *
   * @note If start() was called multiple times, stop() must be called
   * the same number of times to fully stop the service
   */
  stopService(): Promise<void>;

  /**
   * Force stop the foreground service regardless of start counter
   *
   * @returns Promise that resolves when service is forcefully stopped
   */
  stopServiceAll(): Promise<void>;

  /**
   * Update the notification of a running service
   *
   * @param config Updated notification configuration
   * @returns Promise that resolves when notification is updated
   * @throws Error if service is not running
   *
   * @example
   * ```typescript
   * await ForegroundService.updateNotification({
   *   id: 1,
   *   title: 'Updated Title',
   *   message: 'Progress: 50%',
   *   progressBar: true,
   *   progressBarMax: 100,
   *   progressBarCurr: 50
   * });
   * ```
   */
  updateNotification(config: NotificationConfig): Promise<void>;

  /**
   * Check if the foreground service is currently running
   *
   * @returns Promise that resolves to the number of active service instances
   */
  isRunning(): Promise<number>;

  /**
   * Run a headless task in the background
   *
   * @param config Task configuration
   * @returns Promise that resolves when task is queued
   *
   * @note Tasks registered via AppRegistry.registerHeadlessTask must be
   * registered before calling this method
   */
  runTask(config: TaskConfig): Promise<void>;

  /**
   * Cancel a specific notification by ID
   *
   * @param id Notification ID to cancel
   * @returns Promise that resolves when notification is cancelled
   *
   * @note Useful for dismissing secondary notifications while keeping
   * the foreground service running
   */
  cancelNotification(id: number): Promise<void>;

  /**
   * Check if POST_NOTIFICATIONS permission is granted (Android 13+)
   *
   * @returns Promise that resolves to true if permission is granted,
   * false otherwise. Always returns true for Android < 13
   *
   * @example
   * ```typescript
   * const hasPermission = await ForegroundService.checkPostNotificationsPermission();
   * if (!hasPermission) {
   *   // Request permission from user
   *   await PermissionsAndroid.request(
   *     PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
   *   );
   * }
   * ```
   */
  checkPostNotificationsPermission(): Promise<boolean>;

  /**
   * Get module constants
   *
   * @returns Object containing module constants
   */
  getConstants(): {
    /**
     * Minimum supported Android SDK version
     */
    MIN_SDK: number;
    /**
     * Target Android SDK version
     */
    TARGET_SDK: number;
  };
}

export default TurboModuleRegistry.getEnforcing<Spec>("ForegroundService");
