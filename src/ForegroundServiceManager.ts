import { NativeModules, NativeEventEmitter, AppRegistry, Platform } from 'react-native';
import NativeForegroundService from '../turbomodule-specs/NativeForegroundService';
import type {
  StartServiceConfig,
  Task,
  TaskOptions,
  NotificationClickEvent,
  EventListenerCleanup,
} from './types';

/**
 * High-level manager for React Native Foreground Service
 *
 * Provides a simple, developer-friendly API for managing foreground services
 * with task management, notification customization, and event handling.
 *
 * Features:
 * - Task management system with parallel execution
 * - 500ms sampling interval for efficient task scheduling
 * - Android 13+ POST_NOTIFICATIONS permission checking
 * - Android 14+ service type validation
 * - Event handling for notification interactions
 */
class ForegroundServiceManager {
  private static tasks: Record<string, Task> = {};
  private static serviceRunning = false;
  private static serviceStarting = false; // Prevent race conditions
  private static samplingInterval = 500; // milliseconds
  private static eventEmitter = new NativeEventEmitter(NativeModules.ForegroundService);

  /**
   * Register the foreground service task runner
   *
   * MUST be called before start(), typically in your index.js/index.ts entry file.
   *
   * @example
   * ```typescript
   * // index.ts
   * import ForegroundService from '@kirenpaul/rn-foreground-service-turbo';
   * import { AppRegistry } from 'react-native';
   * import App from './App';
   *
   * ForegroundService.register();
   * AppRegistry.registerComponent('MyApp', () => App);
   * ```
   */
  static register(): void {
    if (!this.serviceRunning) {
      AppRegistry.registerHeadlessTask('myTaskName', () => this.taskRunner);
    }
  }

  /**
   * Start the foreground service with a notification
   *
   * @param config Service and notification configuration
   * @throws Error if POST_NOTIFICATIONS permission is not granted (Android 13+)
   * @throws Error if service type is invalid or missing (Android 14+)
   *
   * @example
   * ```typescript
   * await ForegroundService.start({
   *   id: 1,
   *   title: 'My Service',
   *   message: 'Running in background',
   *   serviceType: 'dataSync' // Required for Android 14+
   * });
   * ```
   */
  static async start(config: StartServiceConfig): Promise<void> {
    if (Platform.OS !== 'android') {
      console.warn('ForegroundService is only supported on Android');
      return;
    }

    // Validate and set default service type for Android 14+
    if (!config.serviceType) {
      console.warn(
        'Warning: serviceType not specified. Defaulting to "dataSync". ' +
          'This is required for Android 14+.'
      );
      config.serviceType = 'dataSync';
    }

    // Check POST_NOTIFICATIONS permission (Android 13+)
    const hasPermission = await NativeForegroundService.checkPostNotificationsPermission();
    if (!hasPermission) {
      throw new Error(
        'POST_NOTIFICATIONS permission not granted. ' +
          'Please request this permission before starting the service:\n\n' +
          'import { PermissionsAndroid, Platform } from "react-native";\n' +
          'if (Platform.OS === "android" && Platform.Version >= 33) {\n' +
          '  await PermissionsAndroid.request(\n' +
          '    PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS\n' +
          '  );\n' +
          '}'
      );
    }

    // Prevent race condition: Check if already starting
    if (this.serviceStarting) {
      console.log('Service is already starting, please wait...');
      return;
    }

    // Check native service state to sync with actual state
    const nativeRunningCount = await NativeForegroundService.isRunning();
    if (nativeRunningCount > 0) {
      this.serviceRunning = true;
      console.log('Foreground service is already running.');
      return;
    }

    try {
      this.serviceStarting = true;

      // Convert friendly API to native format
      const nativeConfig = this.convertToNativeConfig(config);

      await NativeForegroundService.startService(nativeConfig);
      this.serviceRunning = true;

      // Start task runner
      await NativeForegroundService.runTask({
        taskName: 'myTaskName',
        delay: this.samplingInterval,
        loopDelay: this.samplingInterval,
        onLoop: true,
      });
    } finally {
      this.serviceStarting = false;
    }
  }

  /**
   * Update the notification of a running service
   *
   * @param config Updated notification configuration
   *
   * @example
   * ```typescript
   * await ForegroundService.update({
   *   id: 1,
   *   title: 'Download Progress',
   *   message: '50% complete',
   *   progress: { max: 100, curr: 50 }
   * });
   * ```
   */
  static async update(config: StartServiceConfig): Promise<void> {
    if (Platform.OS !== 'android') {
      return;
    }

    const nativeConfig = this.convertToNativeConfig(config);
    await NativeForegroundService.updateNotification(nativeConfig);
  }

  /**
   * Stop the foreground service
   *
   * If start() was called multiple times, stop() must be called the same
   * number of times to fully stop the service.
   *
   * @param options Optional configuration
   * @param options.clearTasks Whether to clear all tasks (default: false)
   */
  static async stop(options?: { clearTasks?: boolean }): Promise<void> {
    if (Platform.OS !== 'android') {
      return;
    }

    await NativeForegroundService.stopService();

    // Check if service actually stopped by querying native state
    const nativeRunningCount = await NativeForegroundService.isRunning();
    if (nativeRunningCount === 0) {
      this.serviceRunning = false;

      // Clear tasks if requested or if service fully stopped
      if (options?.clearTasks !== false) {
        this.tasks = {};
        console.log('Service stopped and all tasks cleared');
      }
    }
  }

  /**
   * Force stop the foreground service regardless of start counter
   *
   * This will also clear all tasks and reset state
   */
  static async stopAll(): Promise<void> {
    if (Platform.OS !== 'android') {
      return;
    }

    this.serviceRunning = false;
    this.serviceStarting = false;

    // Clear all tasks immediately
    this.tasks = {};

    await NativeForegroundService.stopServiceAll();

    console.log('Service force stopped and all tasks cleared');
  }

  /**
   * Check if the foreground service is currently running
   *
   * @returns true if service is running, false otherwise
   */
  static is_running(): boolean {
    return this.serviceRunning;
  }

  /**
   * Add a task to the execution queue
   *
   * @param task Function to execute (can be async)
   * @param options Task configuration options
   * @returns Task ID string for managing the task
   *
   * @example
   * ```typescript
   * const taskId = ForegroundService.add_task(
   *   async () => {
   *     const data = await fetchData();
   *     processData(data);
   *   },
   *   {
   *     delay: 5000,      // Run every 5 seconds
   *     onLoop: true,     // Repeat indefinitely
   *     taskId: 'my-task',
   *     onError: (error) => console.error('Task failed:', error)
   *   }
   * );
   * ```
   */
  static add_task(task: () => Promise<void> | void, options: TaskOptions = {}): string {
    const taskId = options.taskId || this.generateTaskId();
    const delay = options.delay || 5000;
    const onLoop = options.onLoop !== undefined ? options.onLoop : true;

    if (!this.tasks[taskId]) {
      this.tasks[taskId] = {
        task,
        delay: Math.ceil(delay / this.samplingInterval) * this.samplingInterval,
        onLoop,
        taskId,
        onSuccess: options.onSuccess || (() => {}),
        onError: options.onError || (() => {}),
        nextExecutionTime: Date.now(),
      };
    }

    return taskId;
  }

  /**
   * Update an existing task
   *
   * @param task Updated task function
   * @param options Task configuration options (must include taskId)
   * @returns Task ID string
   */
  static update_task(
    task: () => Promise<void> | void,
    options: TaskOptions & { taskId: string }
  ): string {
    const delay = options.delay || 5000;
    const onLoop = options.onLoop !== undefined ? options.onLoop : true;

    this.tasks[options.taskId] = {
      task,
      delay: Math.ceil(delay / this.samplingInterval) * this.samplingInterval,
      onLoop,
      taskId: options.taskId,
      onSuccess: options.onSuccess || (() => {}),
      onError: options.onError || (() => {}),
      nextExecutionTime: Date.now(),
    };

    return options.taskId;
  }

  /**
   * Remove a task from the execution queue
   *
   * @param taskId Task ID to remove
   */
  static remove_task(taskId: string): void {
    delete this.tasks[taskId];
  }

  /**
   * Check if a task is currently in the queue
   *
   * @param taskId Task ID to check
   * @returns true if task exists, false otherwise
   */
  static is_task_running(taskId: string): boolean {
    return !!this.tasks[taskId];
  }

  /**
   * Remove all tasks from the execution queue
   */
  static remove_all_tasks(): void {
    this.tasks = {};
  }

  /**
   * Get a task by ID
   *
   * @param taskId Task ID
   * @returns Task object or undefined if not found
   */
  static get_task(taskId: string): Task | undefined {
    return this.tasks[taskId];
  }

  /**
   * Get all tasks in the execution queue
   *
   * @returns Object containing all tasks
   */
  static get_all_tasks(): Record<string, Task> {
    return { ...this.tasks };
  }

  /**
   * Cancel a specific notification by ID
   *
   * Useful for dismissing secondary notifications while keeping the service running.
   *
   * @param id Notification ID to cancel
   */
  static async cancel_notification(id: number): Promise<void> {
    if (Platform.OS !== 'android') {
      return;
    }

    await NativeForegroundService.cancelNotification(id);
  }

  /**
   * Listen for notification click events
   *
   * @param callback Function called when notification or buttons are tapped
   * @returns Cleanup function to remove the listener
   *
   * @example
   * ```typescript
   * useEffect(() => {
   *   const cleanup = ForegroundService.eventListener((event) => {
   *     if (event.main) {
   *       // Main notification tapped
   *       navigation.navigate('Home');
   *     }
   *     if (event.button === 'pause') {
   *       // Pause button tapped
   *       handlePause();
   *     }
   *   });
   *
   *   return cleanup; // Cleanup on unmount
   * }, []);
   * ```
   */
  static eventListener(callback: (event: NotificationClickEvent) => void): EventListenerCleanup {
    const subscription = this.eventEmitter.addListener('notificationClickHandle', callback);

    return () => subscription.remove();
  }

  /**
   * Internal task runner - executes tasks at their scheduled times
   * @private
   */
  private static taskRunner = async (): Promise<void> => {
    try {
      if (!this.serviceRunning) {
        return;
      }

      const now = Date.now();
      const promises: Promise<void>[] = [];

      Object.entries(this.tasks).forEach(([taskId, task]) => {
        if (now >= task.nextExecutionTime) {
          promises.push(
            Promise.resolve(task.task())
              .then(() => task.onSuccess?.())
              .catch((error) => task.onError?.(error))
          );

          if (task.onLoop) {
            task.nextExecutionTime = now + task.delay;
          } else {
            delete this.tasks[taskId];
          }
        }
      });

      await Promise.all(promises);
    } catch (error) {
      console.error('Error in ForegroundService taskRunner:', error);
    }
  };

  /**
   * Generate a random task ID
   * @private
   */
  private static generateTaskId(): string {
    const timestamp = Date.now().toString(36);
    const randomPart = Math.random().toString(36).substring(2, 9);
    return `task_${timestamp}_${randomPart}`;
  }

  /**
   * Convert friendly API config to native format
   * @private
   */
  private static convertToNativeConfig(config: StartServiceConfig): any {
    return {
      id: config.id,
      title: config.title || String(config.id),
      message: config.message || 'Foreground Service Running...',
      vibration: config.vibration || false,
      visibility: config.visibility || 'public',
      icon: config.icon || 'ic_notification',
      largeIcon: config.largeIcon || 'ic_launcher',
      importance: config.importance || 'max',
      number: config.number || '1',
      button: !!config.button,
      buttonText: config.button?.text || '',
      buttonOnPress: config.button?.onPressEvent || 'buttonOnPress',
      button2: !!config.button2,
      button2Text: config.button2?.text || '',
      button2OnPress: config.button2?.onPressEvent || 'button2OnPress',
      mainOnPress: config.mainOnPress || 'mainOnPress',
      progressBar: !!config.progress,
      progressBarMax: config.progress?.max || 0,
      progressBarCurr: config.progress?.curr || 0,
      color: config.color,
      setOnlyAlertOnce: config.setOnlyAlertOnce !== false, // default true
      ongoing: config.ongoing || false,
      serviceType: config.serviceType || 'dataSync',
    };
  }
}

export default ForegroundServiceManager;
