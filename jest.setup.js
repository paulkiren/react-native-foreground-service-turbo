// Create mock native module
const mockNativeForegroundService = {
  startService: jest.fn(() => Promise.resolve()),
  stopService: jest.fn(() => Promise.resolve()),
  stopServiceAll: jest.fn(() => Promise.resolve()),
  updateNotification: jest.fn(() => Promise.resolve()),
  isRunning: jest.fn(() => Promise.resolve(0)),
  runTask: jest.fn(() => Promise.resolve()),
  cancelNotification: jest.fn(() => Promise.resolve()),
  checkPostNotificationsPermission: jest.fn(() => Promise.resolve(true)),
  getConstants: jest.fn(() => ({
    MIN_SDK: 25,
    TARGET_SDK: 34,
  })),
};

// Mock React Native modules
jest.mock('react-native', () => ({
  NativeModules: {
    ForegroundService: mockNativeForegroundService,
  },
  NativeEventEmitter: jest.fn().mockImplementation(() => ({
    addListener: jest.fn(() => ({
      remove: jest.fn(),
    })),
    removeAllListeners: jest.fn(),
  })),
  AppRegistry: {
    registerHeadlessTask: jest.fn(),
    registerComponent: jest.fn(),
  },
  Platform: {
    OS: 'android',
    Version: 34,
  },
  PermissionsAndroid: {
    request: jest.fn(() => Promise.resolve('granted')),
    PERMISSIONS: {
      POST_NOTIFICATIONS: 'android.permission.POST_NOTIFICATIONS',
    },
    RESULTS: {
      GRANTED: 'granted',
      DENIED: 'denied',
    },
  },
  TurboModuleRegistry: {
    getEnforcing: jest.fn(() => mockNativeForegroundService),
  },
}));

// Silence console warnings during tests
global.console = {
  ...console,
  warn: jest.fn(),
  error: jest.fn(),
  log: jest.fn(),
};
