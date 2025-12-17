# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2025-12-17

### Added - Initial Release

- **TurboModule Architecture**: Complete implementation using React Native TurboModules for New Architecture support
- **Android 14 Compliance**: Full support for foreground service types (dataSync, location, mediaPlayback)
- **Android 13 Support**: POST_NOTIFICATIONS permission checking and handling
- **Android 12+ PendingIntent Flags**: Proper FLAG_IMMUTABLE and FLAG_MUTABLE usage
- **TypeScript Support**: Full TypeScript definitions for all APIs
- **Service Type Manager**: Class for handling Android 14+ service type requirements
- **Permission Checker**: Class for runtime permission validation
- **Auto Setup**: Postinstall script automatically configures AndroidManifest.xml
- **Comprehensive Documentation**: Complete README, API docs, and migration guide
- **Task Management System**: Parallel task execution with 500ms sampling interval
- **Event Handling**: Notification button click events with proper event delivery

### Features

- **Service Lifecycle Management**
  - `register()` - Register headless task runner
  - `start(config)` - Start foreground service with notification
  - `update(config)` - Update running notification
  - `stop()` - Stop service (with optional task preservation)
  - `stopAll()` - Force stop service and clear all tasks
  - `is_running()` - Check service state

- **Task Management**
  - `add_task(task, options)` - Add async task to queue
  - `update_task(task, options)` - Update existing task
  - `remove_task(taskId)` - Remove task by ID
  - `is_task_running(taskId)` - Check task state
  - `remove_all_tasks()` - Clear all tasks
  - `get_task(taskId)` - Get task details
  - `get_all_tasks()` - Get all tasks

- **Notification Management**
  - `cancel_notification(id)` - Cancel specific notification
  - `eventListener(callback)` - Listen for notification events

### Technical Details

- **Minimum Requirements**
  - React Native 0.68.0+
  - Android SDK 25+ (Android 7.1+)
  - Node.js 16+

- **Build Configuration**
  - Target SDK: 34 (Android 14)
  - Compile SDK: 34
  - Build Tools: 34.0.0
  - AGP: 8.1.0
  - Java: 11

- **Dependencies**
  - AndroidX Core: 1.12.0
  - WorkManager: 2.9.0

### Security

- PendingIntent with FLAG_IMMUTABLE for security compliance
- Services declared with `android:exported="false"`
- Runtime permission validation for all operations

---

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

- PendingIntent with FLAG_IMMUTABLE for security compliance
- Services declared with `android:exported="false"`
- Runtime permission validation for all operations

---

## Future Releases

This is the initial beta release (0.1.0). Future updates will include:
- Bug fixes and stability improvements
- Additional features based on community feedback
- Performance optimizations
- Enhanced documentation and examples

The package will be promoted to 1.0.0 once it has been thoroughly tested in production environments.### Documentation

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
