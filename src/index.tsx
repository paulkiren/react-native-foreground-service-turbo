/**
 * @kirenpaul/rn-foreground-service-turbo
 *
 * Modern React Native foreground service library with TurboModule support.
 * Full Android 14 compliance with task management and customizable notifications.
 *
 * @packageDocumentation
 */

import ForegroundServiceManager from './ForegroundServiceManager';

// Export types for TypeScript users
export * from './types';

// Export the manager as default export for backward compatibility
export default ForegroundServiceManager;

// Named exports for convenience
export const {
  register,
  start,
  update,
  stop,
  stopAll,
  is_running,
  add_task,
  update_task,
  remove_task,
  is_task_running,
  remove_all_tasks,
  get_task,
  get_all_tasks,
  cancel_notification,
  eventListener,
} = ForegroundServiceManager;
