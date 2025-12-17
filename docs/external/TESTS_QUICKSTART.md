# Quick Start: Running Tests

## ⚡ TL;DR

```bash
# Install dependencies (if not done already)
npm install

# Run tests
npm test
```

## 🔧 First Time Setup

If you just pulled this code or cloned the repo:

```bash
# 1. Install all dependencies including test dependencies
npm install

# 2. Verify installation
npm test -- --version
```

## 🧪 Running Tests

### JavaScript/TypeScript Tests

```bash
# Run all tests
npm test

# Run in watch mode (re-runs on file changes)
npm run test:watch

# Generate coverage report
npm run test:coverage
```

### Android Unit Tests

```bash
# Run Android tests
npm run test:android

# Or directly with Gradle
cd android && ./gradlew test
```

### Run Everything

```bash
# Run both JS and Android tests
npm run test:all
```

## 📊 Expected Output

### Successful Test Run

You should see something like:

```
PASS  src/__tests__/ForegroundServiceManager.test.ts
  ForegroundServiceManager
    ✓ register() should register headless task (15ms)
    ✓ start() should start service with valid config (12ms)
    ✓ add_task() should add task with default options (5ms)
    ... (70 more tests)

Test Suites: 1 passed, 1 total
Tests:       73 passed, 73 total
Snapshots:   0 total
Time:        3.456 s
Ran all test suites.

Coverage:
-----------------------------|---------|----------|---------|---------|
File                         | % Stmts | % Branch | % Funcs | % Lines |
-----------------------------|---------|----------|---------|---------|
All files                    |   92.31 |    88.46 |   95.00 |   92.11 |
 ForegroundServiceManager.ts |   95.12 |    91.30 |   97.50 |   94.87 |
 index.tsx                   |   85.71 |    75.00 |   90.00 |   85.00 |
-----------------------------|---------|----------|---------|---------|
```

## 🐛 Troubleshooting

### Issue: `Cannot find module 'jest'`

**Solution:**
```bash
npm install
```

### Issue: `TurboModuleRegistry.getEnforcing is not a function`

**Solution:** This was fixed in the test setup. Make sure you have the latest version of `jest.setup.js` and the test file.

### Issue: Android tests fail with `./gradlew: Permission denied`

**Solution:**
```bash
chmod +x android/gradlew
cd android && ./gradlew test
```

### Issue: Coverage below 70% threshold

This is expected if:
- Tests are skipped (`.skip()`)
- New code added without tests
- Branches not covered

**Solution:** Add more tests or adjust coverage thresholds in `jest.config.js`

## 📁 Coverage Reports

After running `npm run test:coverage`, view the HTML report:

```bash
# macOS
open coverage/lcov-report/index.html

# Linux
xdg-open coverage/lcov-report/index.html

# Windows
start coverage/lcov-report/index.html
```

## 🎯 Running Specific Tests

### Run specific test file

```bash
npm test ForegroundServiceManager.test.ts
```

### Run specific test case

```bash
npm test -t "should start service with valid config"
```

### Run tests matching pattern

```bash
npm test -t "Task Management"
```

## 🔍 Debug Mode

To debug tests in VS Code:

1. Add breakpoint in test file
2. Press F5 or click "Run and Debug"
3. Select "Jest: Debug Current Test"

Or run with Node debugger:

```bash
node --inspect-brk node_modules/.bin/jest --runInBand
```

Then open `chrome://inspect` in Chrome.

## ✅ Pre-commit Checklist

Before committing code:

```bash
# 1. Run linter
npm run lint

# 2. Check TypeScript
npm run typescript

# 3. Run tests
npm test

# 4. Check coverage
npm run test:coverage
```

## 📚 More Information

- **Detailed testing guide:** See [TESTING.md](./TESTING.md)
- **Test summary:** See [TEST_SUMMARY.md](./TEST_SUMMARY.md)
- **CI/CD pipeline:** See [.github/workflows/test.yml](.github/workflows/test.yml)

## 💬 Questions?

- Check [TESTING.md](./TESTING.md) for detailed documentation
- Open an issue on GitHub
- Review test files for examples
