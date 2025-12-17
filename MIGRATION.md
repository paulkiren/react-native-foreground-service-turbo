# Migration Guide: v2.x → v3.x (TurboModule)

This guide helps you migrate from `@kirenpaul/rn-foreground-service` (v2.x) to `@kirenpaul/rn-foreground-service-turbo` (v3.x).

## Overview of Changes

The v3.x release is a complete rewrite using React Native's TurboModule architecture with full Android 13/14 compliance.

### What's New

- ✅ TurboModule architecture (React Native New Architecture)
- ✅ Android 14 foreground service type support
- ✅ Android 13 POST_NOTIFICATIONS permission handling
- ✅ Proper PendingIntent flags (Android 12+)
- ✅ Modern context management
- ✅ TypeScript-first with full type definitions
- ✅ 100% feature parity with v2.x

### Breaking Changes

1. **Minimum React Native version:** 0.68.0+ (was 0.59.0+)
2. **Android minSdk:** 25 (was 21)
3. **Android targetSdk:** 34 (was 31)
4. **Runtime permission required:** POST_NOTIFICATIONS for Android 13+
5. **Service type required:** Must specify `serviceType` for Android 14+

## Step-by-Step Migration

### Step 1: Update Package

```bash
# Uninstall old package
npm uninstall @kirenpaul/rn-foreground-service

# Install new package
npm install @kirenpaul/rn-foreground-service-turbo
```

Or with Yarn:

```bash
yarn remove @kirenpaul/rn-foreground-service
yarn add @kirenpaul/rn-foreground-service-turbo
```

### Step 2: Update Imports

**Before (v2.x):**
```typescript
import ReactNativeForegroundService from '@kirenpaul/rn-foreground-service';
```

**After (v3.x):**
```typescript
import ForegroundService from '@kirenpaul/rn-foreground-service-turbo';
```

### Step 3: Update AndroidManifest.xml

The postinstall script should handle this automatically, but verify:

**Add these permissions:**
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
```

**Update service declarations:**
```xml
<service
  android:name="com.kirenpaul.foregroundservice.ForegroundService"
  android:exported="false"
  android:foregroundServiceType="dataSync|location|mediaPlayback" />
```

**Update package name in meta-data:**
```xml
<meta-data
  android:name="com.kirenpaul.foregroundservice.notification_channel_name"
  android:value="Foreground Service" />
```

### Step 4: Request POST_NOTIFICATIONS Permission

**NEW in v3.x** - Required for Android 13+:

```typescript
import { PermissionsAndroid, Platform } from 'react-native';

async function requestNotificationPermission() {
  if (Platform.OS === 'android' && Platform.Version >= 33) {
    const result = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
    );
    return result === PermissionsAndroid.RESULTS.GRANTED;
  }
  return true;
}

// Use before starting service:
const hasPermission = await requestNotificationPermission();
if (!hasPermission) {
  console.error('Permission denied');
  return;
}
```

### Step 5: Add Service Type (Android 14+)

**NEW in v3.x** - Required for Android 14+:

**Before (v2.x):**
```typescript
await ReactNativeForegroundService.start({
  id: 1,
  title: 'My Service',
  message: 'Running...'
});
```

**After (v3.x):**
```typescript
await ForegroundService.start({
  id: 1,
  title: 'My Service',
  message: 'Running...',
  serviceType: 'dataSync' // NEW: Required for Android 14+
});
```

**Service Type Options:**
- `'dataSync'` - For data synchronization tasks (most common)
- `'location'` - For location tracking (requires `FOREGROUND_SERVICE_LOCATION` permission)
- `'mediaPlayback'` - For media playback (requires `FOREGROUND_SERVICE_MEDIA_PLAYBACK` permission)

### Step 6: Update build.gradle (app level)

**Ensure you have:**
```gradle
android {
    compileSdkVersion 34

    defaultConfig {
        minSdkVersion 25
        targetSdkVersion 34
    }
}
```

### Step 7: Update Registration (If using different naming)

The registration remains the same:

```typescript
// index.js
import ForegroundService from '@kirenpaul/rn-foreground-service-turbo';
import { AppRegistry } from 'react-native';
import { name as appName } from './app.json';
import App from './App';

ForegroundService.register();
AppRegistry.registerComponent(appName, () => App);
```

## API Changes

### No Changes (100% Compatible)

All these methods work exactly the same:

- `register()`
- `stop()`
- `stopAll()`
- `is_running()`
- `add_task(task, options)`
- `update_task(task, options)`
- `remove_task(taskId)`
- `is_task_running(taskId)`
- `remove_all_tasks()`
- `get_task(taskId)`
- `get_all_tasks()`
- `cancel_notification(id)`
- `eventListener(callback)`

### Modified Methods

#### `start(config)` and `update(config)`

**New field:**
- `serviceType` (string) - Required for Android 14+

**Before:**
```typescript
await ReactNativeForegroundService.start({
  id: 1,
  title: 'Service',
  message: 'Running'
});
```

**After:**
```typescript
await ForegroundService.start({
  id: 1,
  title: 'Service',
  message: 'Running',
  serviceType: 'dataSync' // NEW
});
```

## Code Comparison

### Complete Before/After Example

**Before (v2.x):**
```typescript
import ReactNativeForegroundService from '@kirenpaul/rn-foreground-service';

async function startService() {
  // Start service (no permission check needed)
  await ReactNativeForegroundService.start({
    id: 1,
    title: 'My Service',
    message: 'Running...'
    // No serviceType needed
  });

  // Add task
  ReactNativeForegroundService.add_task(
    async () => {
      await doWork();
    },
    {
      delay: 5000,
      onLoop: true,
      taskId: 'work-task'
    }
  );
}
```

**After (v3.x):**
```typescript
import ForegroundService from '@kirenpaul/rn-foreground-service-turbo';
import { PermissionsAndroid, Platform } from 'react-native';

async function startService() {
  // 1. Request permission (Android 13+)
  if (Platform.OS === 'android' && Platform.Version >= 33) {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
    );
    if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
      console.error('Permission denied');
      return;
    }
  }

  // 2. Start service with serviceType (Android 14+)
  await ForegroundService.start({
    id: 1,
    title: 'My Service',
    message: 'Running...',
    serviceType: 'dataSync' // NEW: Required
  });

  // 3. Add task (unchanged)
  ForegroundService.add_task(
    async () => {
      await doWork();
    },
    {
      delay: 5000,
      onLoop: true,
      taskId: 'work-task'
    }
  );
}
```

## TypeScript Usage

v3.x is TypeScript-first with full type definitions:

```typescript
import ForegroundService, {
  StartServiceConfig,
  TaskOptions,
  NotificationClickEvent
} from '@kirenpaul/rn-foreground-service-turbo';

const config: StartServiceConfig = {
  id: 1,
  title: 'Typed Service',
  message: 'Full TypeScript support',
  serviceType: 'dataSync',
  progress: {
    max: 100,
    curr: 50
  },
  button: {
    text: 'Action',
    onPressEvent: 'action'
  }
};

await ForegroundService.start(config);
```

## Compatibility Table

| Feature | v2.x | v3.x |
|---------|------|------|
| React Native | 0.59+ | 0.68+ |
| Android minSdk | 21 | 25 |
| Android targetSdk | 31 | 34 |
| TurboModule | ❌ | ✅ |
| Android 12 Support | ⚠️ Partial | ✅ Full |
| Android 13 Support | ⚠️ No permission | ✅ Full |
| Android 14 Support | ❌ Broken | ✅ Full |
| POST_NOTIFICATIONS | ❌ | ✅ |
| Service Types | ❌ | ✅ |
| TypeScript | Partial | Full |

## Testing Checklist

After migration, test:

- [ ] Service starts successfully
- [ ] Notification displays correctly
- [ ] Permission request works (Android 13+)
- [ ] Service type is correct (Android 14+)
- [ ] Tasks execute as expected
- [ ] Notification buttons work
- [ ] Event listener receives events
- [ ] Service stops cleanly
- [ ] No crashes on Android 14

## Common Migration Issues

### Issue: "POST_NOTIFICATIONS permission not granted"

**Cause:** Permission not requested before starting service

**Solution:** Request permission first:
```typescript
const granted = await PermissionsAndroid.request(
  PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
);
```

### Issue: "serviceType is required for Android 14+"

**Cause:** Missing `serviceType` parameter

**Solution:** Add serviceType to config:
```typescript
await ForegroundService.start({
  // ... other config
  serviceType: 'dataSync'
});
```

### Issue: Service crashes immediately on start

**Cause:** AndroidManifest.xml not configured correctly

**Solution:**
1. Verify service declarations have `android:foregroundServiceType`
2. Run `npm install` to trigger postinstall script
3. Manually check AndroidManifest.xml

### Issue: TurboModule not found

**Cause:** React Native version < 0.68 or build cache issues

**Solution:**
1. Verify RN version: `npx react-native --version`
2. Clean build: `cd android && ./gradlew clean`
3. Rebuild: `npx react-native run-android`

## Gradual Migration Strategy

If you have a large app, you can migrate gradually:

### Phase 1: Side-by-Side Installation

Both packages can be installed simultaneously:

```bash
npm install @kirenpaul/rn-foreground-service-turbo
# Keep old package for now
```

### Phase 2: Migrate New Features

Use the new library for new features:

```typescript
// Old code - keep using v2.x
import OldService from '@kirenpaul/rn-foreground-service';

// New code - use v3.x
import NewService from '@kirenpaul/rn-foreground-service-turbo';
```

### Phase 3: Complete Migration

Once confident, remove the old package:

```bash
npm uninstall @kirenpaul/rn-foreground-service
```

## Need Help?

- **Issues:** https://github.com/paulkiren/rn-foreground-service-turbo/issues
- **Discussions:** https://github.com/paulkiren/rn-foreground-service-turbo/discussions
- **Documentation:** https://github.com/paulkiren/rn-foreground-service-turbo#readme

## Rollback Plan

If you need to rollback:

```bash
npm uninstall @kirenpaul/rn-foreground-service-turbo
npm install @kirenpaul/rn-foreground-service@2.2.0
```

Then revert your code changes.

---

**Migration completed?** ✅ Your app now has full Android 14 compliance with TurboModule support!
