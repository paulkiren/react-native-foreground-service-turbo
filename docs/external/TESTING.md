# Testing Guide

This document provides comprehensive information about the test suite for the React Native Foreground Service TurboModule library.

## Overview

The library includes two types of tests:

1. **JavaScript/TypeScript Tests** - Testing the React Native layer using Jest
2. **Java Unit Tests** - Testing the Android native implementation using JUnit, Mockito, and Robolectric

## Test Coverage

### JavaScript Tests

**File:** `src/__tests__/ForegroundServiceManager.test.ts`

**Coverage:**
- Service lifecycle (register, start, stop, stopAll)
- Service state management
- Permission checking (POST_NOTIFICATIONS)
- Configuration validation and conversion
- Task management (add, update, remove, get)
- Task execution and scheduling
- Event listeners
- Platform-specific behavior (Android/iOS)
- Error handling

**Key Test Cases:**
- ✅ Service registration and headless task setup
- ✅ Starting service with valid/invalid configs
- ✅ Permission checks for Android 13+
- ✅ Service type validation for Android 14+
- ✅ Task execution with callbacks (onSuccess, onError)
- ✅ Task scheduling with delay and looping
- ✅ Notification updates
- ✅ Multiple task execution in parallel
- ✅ iOS platform no-op behavior

### Java Tests

#### ForegroundServiceModuleTest.java

**Coverage:**
- TurboModule interface implementation
- Service start/stop operations
- Configuration validation
- Permission checking integration
- Task execution
- Notification management

**Key Test Cases:**
- ✅ Module constants (MIN_SDK, TARGET_SDK)
- ✅ Service start with valid/invalid configs
- ✅ Missing required fields (id, title, message)
- ✅ Android 14 service type validation
- ✅ Permission checks
- ✅ Service stop and stopAll
- ✅ Notification updates
- ✅ Task execution with config
- ✅ Cancel notification

#### PermissionCheckerTest.java

**Coverage:**
- Android 13+ POST_NOTIFICATIONS permission
- Android 14+ foreground service type permissions
- Permission error messages

**Key Test Cases:**
- ✅ POST_NOTIFICATIONS check on Android 12 (not required)
- ✅ POST_NOTIFICATIONS check on Android 13+ (required)
- ✅ Base FOREGROUND_SERVICE permission check
- ✅ Service type permissions (location, mediaPlayback)
- ✅ Permission error message generation
- ✅ Multiple Android version scenarios

#### ServiceTypeManagerTest.java

**Coverage:**
- Service type flag conversion
- Required permission mapping
- Additional permission requirements

**Key Test Cases:**
- ✅ Service type flag mapping (dataSync, location, mediaPlayback)
- ✅ Null/unknown type handling (defaults to dataSync)
- ✅ Required permission strings
- ✅ Additional permission requirements check
- ✅ Service type constants

#### NotificationConfigTest.java

**Coverage:**
- AndroidManifest.xml metadata reading
- Configuration defaults
- Error handling

**Key Test Cases:**
- ✅ Channel name from metadata
- ✅ Channel description from metadata
- ✅ Notification color from metadata
- ✅ Default values when metadata missing
- ✅ Metadata caching
- ✅ Null metadata handling

## Running Tests

### Prerequisites

```bash
# Install dependencies
npm install

# For Android tests, ensure you have Android SDK and Gradle installed
```

### JavaScript Tests

```bash
# Run all JavaScript tests
npm test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
```

### Java Tests

```bash
# Run all Android unit tests
npm run test:android

# Or directly with Gradle
cd android && ./gradlew test

# Run specific test class
cd android && ./gradlew test --tests ForegroundServiceModuleTest

# Run tests with coverage
cd android && ./gradlew testDebugUnitTest jacocoTestReport
```

### Run All Tests

```bash
# Run both JavaScript and Java tests
npm run test:all
```

## Test Structure

```
rn-foreground-service-turbo/
├── src/
│   └── __tests__/
│       └── ForegroundServiceManager.test.ts    # JS tests
├── android/
│   └── src/
│       └── test/
│           └── java/
│               └── com/kirenpaul/foregroundservice/
│                   ├── ForegroundServiceModuleTest.java
│                   ├── PermissionCheckerTest.java
│                   ├── ServiceTypeManagerTest.java
│                   └── NotificationConfigTest.java
├── jest.config.js                               # Jest configuration
├── jest.setup.js                                # Jest setup/mocks
└── TESTING.md                                   # This file
```

## Coverage Goals

We aim for the following test coverage:

- **JavaScript:** 70%+ coverage for branches, functions, lines, statements
- **Java:** 80%+ coverage for critical paths

Current coverage can be viewed by running:

```bash
# JavaScript coverage
npm run test:coverage

# Java coverage
cd android && ./gradlew testDebugUnitTest jacocoTestReport
# Report: android/build/reports/jacoco/test/html/index.html
```

## Writing New Tests

### JavaScript Tests

When adding new functionality to `ForegroundServiceManager.ts`, add corresponding tests to `src/__tests__/ForegroundServiceManager.test.ts`:

```typescript
describe('MyNewFeature', () => {
  it('should do something specific', async () => {
    // Arrange
    const mockData = { /* ... */ };

    // Act
    const result = await ForegroundServiceManager.myNewMethod(mockData);

    // Assert
    expect(result).toBe(expectedValue);
    expect(NativeForegroundService.someMethod).toHaveBeenCalledWith(/* ... */);
  });
});
```

### Java Tests

When adding new Java classes or methods, create corresponding test files in `android/src/test/java/`:

```java
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
public class MyNewClassTest {

    @Mock
    private Context mockContext;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Setup
    }

    @Test
    public void testMyMethod() {
        // Arrange
        MyNewClass instance = new MyNewClass(mockContext);

        // Act
        int result = instance.myMethod();

        // Assert
        assertEquals(expectedValue, result);
    }
}
```

## Mocking Strategy

### JavaScript Mocks

Located in `jest.setup.js`:

- `NativeModules.ForegroundService` - Mocked TurboModule
- `NativeEventEmitter` - Mocked event emitter
- `AppRegistry` - Mocked app registry
- `Platform` - Mocked platform detection
- `PermissionsAndroid` - Mocked permissions

### Java Mocks

Using Mockito and Robolectric:

- `@Mock` annotation for dependency mocking
- `Robolectric` for Android framework classes
- `@Config` for Android version simulation

## Continuous Integration

Tests should be run on every pull request. Suggested CI configuration:

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  javascript-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm install
      - run: npm test
      - run: npm run test:coverage

  android-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: 'zulu'
          java-version: '11'
      - run: cd android && ./gradlew test
```

## Known Test Limitations

### Current Limitations

1. **Static Mocking**: PermissionCheckerTest requires Mockito inline for static mocking of `ContextCompat`. This is noted in the test file.

2. **Service Integration**: Full integration tests with actual service lifecycle are not included. These would require instrumented tests on a real device.

3. **Notification Visual Tests**: Cannot test actual notification appearance, only configuration.

4. **Event Delivery**: NotificationClickEvent delivery to React Native is not fully integration tested.

### Future Improvements

- [ ] Add instrumented tests for full service lifecycle
- [ ] Add E2E tests using Detox or Appium
- [ ] Add performance benchmarks for task execution
- [ ] Add stress tests for multiple concurrent tasks
- [ ] Add tests for memory leak scenarios
- [ ] Add tests for service crash recovery

## Debugging Tests

### JavaScript Tests

```bash
# Run with Node debugger
node --inspect-brk node_modules/.bin/jest --runInBand

# Run specific test file
npm test ForegroundServiceManager.test.ts

# Run specific test case
npm test -t "should start service with valid config"
```

### Java Tests

```bash
# Run with verbose output
cd android && ./gradlew test --info

# Run with debug logging
cd android && ./gradlew test --debug

# Generate HTML report
cd android && ./gradlew test
# Open: android/build/reports/tests/test/index.html
```

## Test Maintenance

- Tests should be updated whenever functionality changes
- Deprecated features should have their tests removed
- New features MUST include tests
- Breaking changes MUST update relevant tests
- Test coverage should not decrease

## Questions?

For questions about testing:
- Open an issue: https://github.com/paulkiren/rn-foreground-service-turbo/issues
- Check discussions: https://github.com/paulkiren/rn-foreground-service-turbo/discussions
