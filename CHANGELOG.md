# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0] - 2025-12-17

### Changed

- **Package Name**: Renamed from `@kirenpaul/rn-foreground-service-turbo` to `@kirenpaul/react-native-foreground-service-turbo`
  - Follows React Native naming conventions for better discoverability
  - Maintains scoped package namespace for security and branding

### Fixed - Critical Issues

- **Race Condition in Service State Management**: Added `serviceStarting` flag and native state synchronization to prevent duplicate service starts
- **Missing Task Cleanup**: Tasks are now automatically cleared on service stop (configurable with `clearTasks` option)
- **Native Handler Cleanup**: Added `cleanupResources()` method to properly clean up handler callbacks on service stop
- **Notification Event Delivery**: Created `NotificationEventReceiver.java` to properly deliver notification button click events to React Native

### Breaking Changes

- **stop() Method**: Now clears tasks by default. Use `stop({ clearTasks: false })` to preserve tasks.

### Migration

- See [MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md) for detailed upgrade instructions

---

## [1.0.0] - 2025-12-17

### Added

- **TurboModule Architecture**: Complete rewrite using React Native TurboModules for New Architecture support
- **Android 14 Compliance**: Full support for foreground service types (dataSync, location, mediaPlayback)
- **Android 13 Support**: POST_NOTIFICATIONS permission checking and handling
- **Android 12+ PendingIntent Flags**: Proper FLAG_IMMUTABLE and FLAG_MUTABLE usage
- **TypeScript Support**: Full TypeScript definitions for all APIs
- **Service Type Manager**: New class for handling Android 14+ service type requirements
- **Permission Checker**: New class for runtime permission validation
- **Auto Setup**: Postinstall script automatically configures AndroidManifest.xml
- **Comprehensive Documentation**: Complete README, API docs, and migration guide
- **Modern Context Management**: Proper Handler cleanup and context usage patterns

### Changed

- **Package Name**: Changed from `@kirenpaul/rn-foreground-service` to `@kirenpaul/rn-foreground-service-turbo`
- **Minimum React Native**: Now requires 0.68.0+ (was 0.59.0+)
- **Minimum Android SDK**: Now requires API 25 (was API 21)
- **Target Android SDK**: Updated to API 34 (was API 31)
- **Build Configuration**: Updated to compileSdk 34, buildTools 34.0.0
- **Dependencies**: Updated to AndroidX Core 1.12.0, WorkManager 2.9.0
- **Service Implementation**: Modernized with Android 14 service type handling
- **Notification Helper**: Fixed PendingIntent flags for Android 12+ security requirements
- **Error Handling**: Enhanced error messages with specific guidance for permission issues

### Fixed

- **Android 14 Crashes**: Service now properly declares and uses foreground service types
- **Android 13 Notifications**: Fixed notification display issues with POST_NOTIFICATIONS permission
- **Android 12 PendingIntent**: Fixed security exception with proper FLAG_IMMUTABLE usage
- **Memory Leaks**: Fixed Handler memory leaks with proper cleanup in onDestroy
- **Channel Management**: Fixed static channel creation flag causing update issues
- **Context Usage**: Fixed excessive getApplicationContext() usage

### Security

- **PendingIntent Security**: Main notification intents now use FLAG_IMMUTABLE (security best practice)
- **Service Export**: Services now properly declared with `android:exported="false"`
- **Permission Validation**: Runtime checks for all required permissions before service start

### API

**No Breaking Changes to Existing API** - 100% feature parity maintained:

- `register()` - Unchanged
- `start(config)` - Added optional `serviceType` field (required for Android 14+)
- `update(config)` - Added optional `serviceType` field
- `stop()` - Unchanged
- `stopAll()` - Unchanged
- `is_running()` - Unchanged
- `add_task(task, options)` - Unchanged
- `update_task(task, options)` - Unchanged
- `remove_task(taskId)` - Unchanged
- `is_task_running(taskId)` - Unchanged
- `remove_all_tasks()` - Unchanged
- `get_task(taskId)` - Unchanged
- `get_all_tasks()` - Unchanged
- `cancel_notification(id)` - Unchanged
- `eventListener(callback)` - Unchanged

**New API:**
- `checkPostNotificationsPermission()` - Check if POST_NOTIFICATIONS permission is granted

### Documentation

- Complete README with usage examples
- Comprehensive API reference
- Step-by-step migration guide (MIGRATION.md)
- Code examples for common use cases
- Troubleshooting guide
- Android 13/14 compliance documentation

### Developer Experience

- Automatic AndroidManifest.xml configuration via postinstall
- Clear error messages with actionable guidance
- TypeScript autocomplete for all APIs
- Detailed inline documentation (JSDoc)
- Example code for all features

---

## Version History

### [1.0.0] - Initial TurboModule Release
First public release of the TurboModule-based foreground service library with full Android 14 compliance.

### Previous Versions (Legacy Package)
For history of `@kirenpaul/rn-foreground-service` (v2.x and earlier), see the legacy package repository.

---

## Upgrade Guide

See [MIGRATION.md](./MIGRATION.md) for detailed instructions on migrating from v2.x to v3.x.

## Support

- **Issues**: https://github.com/paulkiren/react-native-foreground-service-turbo/issues
- **Discussions**: https://github.com/paulkiren/react-native-foreground-service-turbo/discussions
