# Android Testing Guide

## Overview

This library includes Java unit tests for the Android native implementation. However, due to the nature of React Native library modules, running these tests requires special setup.

## Current Status

✅ **JavaScript Tests** - Fully working with `npm test`
⚠️ **Android Unit Tests** - Require manual setup (see below)

## Why Android Tests Don't Run Out-of-the-Box

React Native library modules depend on:
1. `react-native` Android library
2. Parent project structure
3. Proper Gradle configuration

When run standalone, the module can't resolve React Native dependencies.

## Option 1: Run Tests in Android Studio (Recommended)

This is the easiest way to run Android tests:

### Setup

1. Open Android Studio
2. Open the `android` directory as a project
3. Wait for Gradle sync to complete
4. If you see dependency errors, add this to your `local.properties`:
   ```properties
   sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
   ```

### Run Tests

**Run all tests:**
1. Right-click on `src/test/java` directory
2. Select "Run 'Tests in 'rn-foreground-service-turbo''"

**Run specific test:**
1. Navigate to test file (e.g., `ForegroundServiceModuleTest.java`)
2. Click the green play button next to the class or test method
3. Select "Run Test"

**Run with coverage:**
1. Right-click on test directory or file
2. Select "Run 'Tests' with Coverage"

## Option 2: Integration with Example App

The proper way to test a React Native library is within a parent app:

### Setup Example App

1. Create an example React Native app:
   ```bash
   cd ..
   npx react-native init ExampleApp
   cd ExampleApp
   ```

2. Link the library locally:
   ```bash
   npm install ../rn-foreground-service-turbo
   ```

3. Run Android tests from the example app:
   ```bash
   cd android
   ./gradlew :rn-foreground-service-turbo:test
   ```

## Option 3: Mock React Native Dependencies

For standalone testing, you can mock React Native:

### Create Mock Build

1. Create `android/libs` directory
2. Create a stub `react-android.jar` with minimal interfaces
3. Update `build.gradle` to use local libs

This is complex and not recommended unless necessary.

## What's Tested

Despite setup complexity, the Android tests cover:

- ✅ **ForegroundServiceModuleTest** (18 tests)
  - TurboModule interface
  - Service operations
  - Validation logic
  - Permission checking

- ✅ **PermissionCheckerTest** (13 tests)
  - POST_NOTIFICATIONS permission (Android 13+)
  - Foreground service type permissions (Android 14+)
  - Error messages

- ✅ **ServiceTypeManagerTest** (15 tests)
  - Service type mapping
  - Permission requirements
  - Default handling

- ✅ **NotificationConfigTest** (9 tests)
  - Metadata reading
  - Default values
  - Error handling

**Total: 55 Android unit tests**

## Alternative: Focus on JavaScript Tests

Since JavaScript tests provide excellent coverage and are easier to run:

### Coverage Comparison

| Layer | JS Tests | Android Tests | Complexity |
|-------|----------|---------------|------------|
| Service API | ✅ 100% | ✅ 100% | Low |
| Task Management | ✅ 100% | ⚠️ Indirect | Low |
| Permissions | ✅ Mocked | ✅ Unit | Medium |
| Android Framework | ❌ N/A | ✅ Robolectric | High |

### Recommendation

For most development:
1. **Run JavaScript tests**: `npm test`
2. **Manual testing**: Test on actual devices
3. **Android Studio**: Run Android tests when needed

## CI/CD Considerations

For continuous integration:

### Option A: Skip Android Tests

```yaml
# .github/workflows/test.yml
android-tests:
  runs-on: ubuntu-latest
  steps:
    - name: Skip Android tests
      run: echo "Android tests require manual setup"
```

### Option B: Use Example App

```yaml
android-tests:
  runs-on: ubuntu-latest
  steps:
    - name: Create example app
      run: npx react-native init ExampleApp
    - name: Link library
      run: cd ExampleApp && npm install ..
    - name: Run tests
      run: cd ExampleApp/android && ./gradlew :rn-foreground-service-turbo:test
```

### Option C: Docker with React Native

```yaml
android-tests:
  runs-on: ubuntu-latest
  container:
    image: reactnativecommunity/react-native-android
  steps:
    - name: Run tests
      run: cd android && ./gradlew test
```

## Troubleshooting

### "Could not find react-android"

**Cause:** React Native not in classpath

**Solution:** Use Android Studio or example app approach

### "Task ':test' not found"

**Cause:** Running from wrong directory

**Solution:** Ensure you're in the `android` directory

### "SDK location not found"

**Cause:** Android SDK path not configured

**Solution:** Create `local.properties`:
```properties
sdk.dir=/path/to/android/sdk
```

On macOS:
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

## Summary

| Method | Difficulty | Setup Time | Recommended |
|--------|-----------|------------|-------------|
| Android Studio | Easy | 5 min | ✅ Yes |
| Example App | Medium | 10 min | ⚠️ For CI |
| Standalone | Hard | 30+ min | ❌ No |

**Best Practice:** Run JavaScript tests regularly (`npm test`) and Android tests in Android Studio when modifying native code.

## Questions?

- **JS Tests:** See [TESTING.md](./TESTING.md)
- **Quick Start:** See [TESTS_QUICKSTART.md](./TESTS_QUICKSTART.md)
- **Issues:** Open on [GitHub](https://github.com/paulkiren/rn-foreground-service-turbo/issues)
