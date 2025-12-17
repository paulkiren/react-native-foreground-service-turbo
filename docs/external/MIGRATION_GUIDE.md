# Migration Guide - v3.x Critical Fixes

This guide helps you migrate to the latest version of `@kirenpaul/react-native-foreground-service-turbo` which includes critical bug fixes for stability, memory management, and event delivery.

## Overview

**Version:** 3.x (with critical fixes)
**Migration Difficulty:** Easy (mostly automatic)
**Breaking Changes:** 1 behavioral change (task cleanup)
**Required Actions:** Minimal - review task cleanup behavior

---

## What's Changed?

### ✅ Automatic Improvements (No Action Required)

These fixes are applied automatically and require no code changes:

1. **Race Condition Prevention** - Multiple rapid `start()` calls are now safely handled
2. **Native Resource Cleanup** - Handler callbacks are properly cleared on service stop
3. **Notification Events Now Work** - Button clicks and notification taps now properly trigger event listeners
4. **Service State Synchronization** - JS state now syncs with actual native service state

### ⚠️ Behavioral Change (Review Required)

**Task Cleanup on Service Stop:**

**Before (Old Behavior):**
```typescript
await ForegroundService.stop();
// Tasks remained in memory - could resume on restart
```

**After (New Behavior):**
```typescript
await ForegroundService.stop();
// Tasks are now cleared by default - safer for most use cases
```

---

## Migration Steps

### Step 1: Update Dependencies

```bash
npm install @kirenpaul/react-native-foreground-service-turbo@latest
# or
yarn add @kirenpaul/react-native-foreground-service-turbo@latest
```

### Step 2: Review Task Cleanup Behavior

**If you DON'T need tasks to persist after stop:**

No changes needed! The new behavior is what you want.

```typescript
// Your existing code works perfectly
await ForegroundService.stop();
```

**If you DO need tasks to persist after stop:**

Update your `stop()` calls to explicitly keep tasks:

```typescript
// Old code
await ForegroundService.stop();

// New code - keep tasks for later resume
await ForegroundService.stop({ clearTasks: false });
```

### Step 3: Test Notification Events

If you were experiencing issues with notification button clicks not working, they should now work automatically!

**Before (wasn't working):**
```typescript
useEffect(() => {
  const cleanup = ForegroundService.eventListener((event) => {
    console.log('Event:', event); // Never fired
  });
  return cleanup;
}, []);
```

**After (now works!):**
```typescript
useEffect(() => {
  const cleanup = ForegroundService.eventListener((event) => {
    console.log('Event:', event); // Now properly fires!

    if (event.button) {
      handleButtonClick(event.button);
    }
  });
  return cleanup;
}, []);
```

### Step 4: Remove Workarounds

If you implemented any workarounds for the bugs, you can now remove them:

**Remove race condition workarounds:**
```typescript
// OLD - No longer needed
let isStarting = false;
if (!isStarting) {
  isStarting = true;
  await ForegroundService.start(config);
  isStarting = false;
}

// NEW - Just call start() directly
await ForegroundService.start(config);
```

**Remove manual task cleanup:**
```typescript
// OLD - No longer needed
ForegroundService.tasks = {}; // Manual cleanup

// NEW - Automatic cleanup
await ForegroundService.stopAll();
```

---

## Testing Your Migration

### Test 1: Multiple Start Calls (Race Condition)

```typescript
// Should not cause any issues
await ForegroundService.start(config);
await ForegroundService.start(config); // Safely ignored
await ForegroundService.start(config); // Safely ignored
```

**Expected Result:** Service starts once, subsequent calls are ignored with console log.

### Test 2: Task Cleanup

```typescript
// Start service and add tasks
await ForegroundService.start(config);
ForegroundService.add_task(() => console.log('Task'), { taskId: 'test' });

console.log('Before stop:', ForegroundService.get_all_tasks());
// Should show: { test: { ... } }

await ForegroundService.stopAll();

console.log('After stop:', ForegroundService.get_all_tasks());
// Should show: {}
```

**Expected Result:** Tasks are cleared after `stopAll()`.

### Test 3: Notification Event Delivery

```typescript
// Set up event listener
const cleanup = ForegroundService.eventListener((event) => {
  console.log('✅ Event received:', event);
  Alert.alert('Notification clicked!', JSON.stringify(event));
});

// Start service with notification button
await ForegroundService.start({
  id: 1,
  title: 'Test Service',
  message: 'Click the button below',
  button: true,
  buttonText: 'Test Button',
  buttonOnPress: 'test-button-pressed',
});

// Click notification button in system tray
// Should now trigger the event listener!
```

**Expected Result:** Clicking notification button triggers event listener.

### Test 4: Service Restart After App Restart

```typescript
// Check if service is already running (e.g., after app restart)
await ForegroundService.start(config);
// Should automatically detect if already running and sync state
```

**Expected Result:** No duplicate services, state properly synchronized.

---

## Breaking Changes in Detail

### `stop()` Method - Task Cleanup Behavior

**Type:** Behavioral Change
**Impact:** Low - Most apps will benefit from the new behavior
**Action Required:** Only if you need tasks to persist

**Before:**
```typescript
interface StopOptions {}
static async stop(): Promise<void>
```

**After:**
```typescript
interface StopOptions {
  clearTasks?: boolean; // Default: true
}
static async stop(options?: StopOptions): Promise<void>
```

**Migration Examples:**

```typescript
// Scenario 1: Stop service and clear tasks (default)
await ForegroundService.stop();
// or explicitly
await ForegroundService.stop({ clearTasks: true });

// Scenario 2: Stop service but keep tasks for later resume
await ForegroundService.stop({ clearTasks: false });

// Scenario 3: Force stop and always clear tasks
await ForegroundService.stopAll(); // Always clears tasks
```

---

## Common Migration Scenarios

### Scenario 1: Music Player App

**Use Case:** Stop service when user pauses music, but resume same track later

```typescript
// When user pauses
await ForegroundService.stop({ clearTasks: false }); // Keep track info

// When user resumes
await ForegroundService.start(config); // Resume with saved tasks
```

### Scenario 2: Download Manager

**Use Case:** Stop service when downloads complete, clear all state

```typescript
// When all downloads complete
await ForegroundService.stopAll(); // Clear all download tasks
```

### Scenario 3: Fitness Tracker

**Use Case:** Stop service when workout ends, clear workout data

```typescript
// When workout ends
await ForegroundService.stop(); // Default: clear workout tasks
```

### Scenario 4: Navigation App

**Use Case:** Stop service when destination reached, keep destination history

```typescript
// When navigation ends
await ForegroundService.stop({ clearTasks: false }); // Keep history
```

---

## Rollback Instructions

If you encounter issues and need to rollback:

```bash
# Reinstall previous version (if known)
npm install @kirenpaul/rn-foreground-service-turbo@2.x.x

# Or use git to revert
git checkout HEAD~1 -- node_modules/@kirenpaul/rn-foreground-service-turbo
```

**Note:** Rolling back is not recommended as it reintroduces critical bugs.

---

## FAQ

### Q: Will my existing code break?

**A:** No, unless you explicitly relied on tasks persisting after `stop()`. In that case, add `{ clearTasks: false }` to your `stop()` calls.

### Q: Do I need to update Android native code?

**A:** No, all changes are internal. Just update the npm package.

### Q: Do I need to update iOS code?

**A:** No, this library is Android-only.

### Q: Will notification events work without code changes?

**A:** Yes! If you already have event listeners set up, they will now work automatically.

### Q: Can I test the fixes before deploying?

**A:** Yes! Follow the testing steps in the "Testing Your Migration" section above.

### Q: What if I find a bug in the fixes?

**A:** Please open an issue on GitHub with:
- Reproduction steps
- Expected vs actual behavior
- Device/Android version
- Relevant logs

---

## Version Compatibility

| Library Version | React Native | Android API | Notes |
|----------------|--------------|-------------|-------|
| 3.x (with fixes) | 0.68+ | 23+ (6.0+) | Recommended |
| 2.x (before fixes) | 0.68+ | 23+ (6.0+) | Has critical bugs |

---

## Additional Resources

- **Testing Guide:** See [TESTING.md](./TESTING.md) for comprehensive testing instructions
- **Fix Details:** See [CRITICAL_FIXES_APPLIED.md](./CRITICAL_FIXES_APPLIED.md) for technical details
- **API Documentation:** See [README.md](./README.md) for full API reference
- **Changelog:** See [CHANGELOG.md](./CHANGELOG.md) for version history

---

## Support

If you need help with migration:

1. Review this guide carefully
2. Check [CRITICAL_FIXES_APPLIED.md](./CRITICAL_FIXES_APPLIED.md) for technical details
3. Test your app thoroughly before deployment
4. Open an issue on GitHub if you encounter problems

---

## Summary Checklist

Use this checklist to ensure smooth migration:

- [ ] Updated to latest version via npm/yarn
- [ ] Reviewed task cleanup behavior for your use case
- [ ] Updated `stop()` calls if tasks need to persist
- [ ] Tested multiple rapid `start()` calls
- [ ] Tested notification button event delivery
- [ ] Removed any workarounds for old bugs
- [ ] Tested app restart scenarios
- [ ] Tested on real device (recommended)
- [ ] Updated any documentation/comments in your code
- [ ] Deployed to staging/beta for real-world testing

---

**🎉 Congratulations!** Your app now has:
- ✅ Thread-safe service state management
- ✅ Proper memory management
- ✅ Working notification events
- ✅ Better resource cleanup
- ✅ More stable foreground service

Enjoy the improved stability and functionality!
