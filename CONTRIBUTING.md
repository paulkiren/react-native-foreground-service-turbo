# Contributing to React Native Foreground Service Turbo

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to this project.

---

## 📋 **Table of Contents**

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [CI/CD](#cicd)

---

## Code of Conduct

This project follows a Code of Conduct. By participating, you are expected to uphold this code:

- Be respectful and inclusive
- Accept constructive criticism gracefully
- Focus on what is best for the community
- Show empathy towards other community members

---

## Getting Started

### Prerequisites

- Node.js 16.x or higher
- npm or yarn
- Git
- Android Studio (for Android development)
- Java 17 (for Android builds)

### Fork and Clone

1. **Fork the repository** on GitHub

2. **Clone your fork:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/react-native-foreground-service-turbo.git
   cd react-native-foreground-service-turbo
   ```

3. **Add upstream remote:**
   ```bash
   git remote add upstream https://github.com/paulkiren/react-native-foreground-service-turbo.git
   ```

4. **Install dependencies:**
   ```bash
   npm install
   ```

5. **Verify setup:**
   ```bash
   npm test
   npm run typescript
   npm run lint
   ```

---

## Development Workflow

### 1. Create a Branch

Always create a new branch for your work:

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

**Branch naming conventions:**
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions or fixes
- `chore/` - Maintenance tasks

### 2. Make Changes

Follow the [Coding Standards](#coding-standards) below.

### 3. Test Your Changes

Run all checks before committing:

```bash
# Run tests
npm test

# Check types
npm run typescript

# Lint code
npm run lint

# Build package
npm run prepare
```

### 4. Commit Changes

Use [semantic commit messages](https://www.conventionalcommits.org/):

```bash
git add .
git commit -m "feat: add new notification action button"
```

**Commit message format:**
```
<type>: <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Test changes
- `chore`: Maintenance tasks
- `ci`: CI/CD changes

**Examples:**
```
feat: add support for custom notification sounds
fix: resolve race condition in service start
docs: update installation instructions
refactor: simplify task manager logic
test: add tests for notification helper
```

### 5. Keep Your Branch Updated

Regularly sync with upstream:

```bash
git fetch upstream
git rebase upstream/main
```

### 6. Push Changes

```bash
git push origin your-branch-name
```

---

## Pull Request Process

### 1. Create Pull Request

- Go to your fork on GitHub
- Click "New pull request"
- Select your branch
- Fill out the PR template

### 2. PR Title Format

Use semantic format (same as commits):

```
feat: add custom notification sounds
fix: resolve service restart issue
docs: improve API documentation
```

### 3. PR Description

Include:
- **What**: What does this PR do?
- **Why**: Why is this change needed?
- **How**: How does it work?
- **Testing**: How was it tested?
- **Screenshots**: If UI changes (N/A for this library)

**Example:**
```markdown
## What
Adds support for custom notification sounds.

## Why
Users requested ability to use custom sounds for different notification types.

## How
- Added `sound` parameter to NotificationConfig
- Updated NotificationHelper to use custom sound URIs
- Added sound validation in Android native code

## Testing
- Added unit tests for sound parameter validation
- Tested with custom sound files on Android 10-14
- Verified backward compatibility (sound parameter optional)

## Checklist
- [x] Tests added and passing
- [x] Documentation updated
- [x] CHANGELOG.md updated
- [x] No breaking changes
```

### 4. CI Checks

Your PR will automatically run:
- ✅ Tests on Node 20.x (LTS)
- ✅ TypeScript type checking
- ✅ ESLint checks
- ✅ Package build validation
- ✅ Security scans
- ✅ Test coverage report

**All checks must pass before merge.**

### 5. Code Review

- Address reviewer feedback
- Push additional commits to your branch
- Request re-review when ready

### 6. Merge

Once approved and all checks pass:
- Maintainer will merge your PR
- Your changes will be included in the next release

---

## Coding Standards

### TypeScript

- Use TypeScript for all code
- Enable strict mode
- Document public APIs with JSDoc

**Example:**
```typescript
/**
 * Start the foreground service with notification
 *
 * @param config - Service configuration
 * @returns Promise that resolves when service starts
 * @throws Error if permissions not granted
 *
 * @example
 * ```typescript
 * await ForegroundService.start({
 *   id: 1,
 *   title: 'My Service',
 *   message: 'Running...',
 *   serviceType: 'dataSync'
 * });
 * ```
 */
static async start(config: StartServiceConfig): Promise<void> {
  // Implementation
}
```

### Code Style

- Use 2 spaces for indentation
- Use single quotes for strings
- Add trailing commas
- Max line length: 100 characters
- Use `const` over `let` when possible

**Run linter to auto-fix:**
```bash
npm run lint -- --fix
```

### File Organization

```
src/
├── index.tsx              # Main exports
├── ForegroundServiceManager.ts  # Core logic
├── types.ts               # TypeScript types
└── __tests__/             # Tests
    └── ForegroundServiceManager.test.ts

android/
└── src/main/java/com/kirenpaul/foregroundservice/
    ├── Constants.java
    ├── ForegroundService.java
    └── ...
```

### Naming Conventions

- **Classes**: PascalCase (`ForegroundServiceManager`)
- **Functions**: camelCase (`startService`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRIES`)
- **Interfaces**: PascalCase with descriptive names (`StartServiceConfig`)
- **Types**: PascalCase (`ServiceType`)

---

## Testing

### Write Tests

All new features and bug fixes must include tests:

```typescript
describe('ForegroundService', () => {
  it('should start service with valid config', async () => {
    await ForegroundService.start({
      id: 1,
      title: 'Test',
      message: 'Testing',
      serviceType: 'dataSync'
    });

    expect(NativeForegroundService.startService).toHaveBeenCalled();
  });

  it('should throw error if config invalid', async () => {
    await expect(
      ForegroundService.start({} as any)
    ).rejects.toThrow();
  });
});
```

### Run Tests

```bash
# Run all tests
npm test

# Watch mode
npm run test:watch

# With coverage
npm run test:coverage
```

### Coverage Requirements

- Target: 70% coverage minimum
- CI will report coverage on PRs
- View coverage: `open coverage/lcov-report/index.html`

---

## CI/CD

All PRs trigger automated checks. See [CI_CD.md](./CI_CD.md) for details.

### What Gets Checked

- ✅ Tests on multiple Node versions
- ✅ TypeScript type checking
- ✅ ESLint validation
- ✅ Package build
- ✅ Security audit
- ✅ Test coverage

### Troubleshooting CI Failures

**Tests fail in CI but pass locally:**
```bash
# Match CI environment
nvm use 20
rm -rf node_modules package-lock.json
npm install
npm test
```

**Linting errors:**
```bash
npm run lint -- --fix
```

**Type errors:**
```bash
npm run typescript
```

---

## Documentation

### Update Documentation

If your changes affect the API or user experience:

1. **Update README.md** - API changes, new features
2. **Update CHANGELOG.md** - Add entry under "Unreleased"
3. **Update JSDoc comments** - Keep code documented
4. **Update TypeScript types** - Keep types accurate

### Documentation Style

- Use clear, concise language
- Include code examples
- Add links to related documentation
- Use proper markdown formatting

---

## Release Process

Maintainers handle releases, but here's the process:

1. **Update version** in `package.json`
2. **Update CHANGELOG.md** with release notes
3. **Commit and tag:**
   ```bash
   git commit -m "chore: release v3.1.0"
   git tag v3.1.0
   git push origin main --tags
   ```
4. **Create GitHub Release** - Triggers automatic NPM publish
5. **Verify on NPM** - Check package published correctly

---

## Questions?

- **Bugs**: [Open an issue](https://github.com/paulkiren/react-native-foreground-service-turbo/issues)
- **Features**: [Start a discussion](https://github.com/paulkiren/react-native-foreground-service-turbo/discussions)
- **Help**: Check [README.md](./README.md) or open a discussion

---

## Recognition

Contributors will be:
- Listed in release notes
- Added to GitHub contributors
- Credited in CHANGELOG.md (for significant contributions)

---

## Thank You!

Your contributions make this project better for everyone. Thank you for taking the time to contribute! 🎉

---

**Happy Coding!** 🚀
