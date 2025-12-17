# CI/CD Documentation

Complete guide to the Continuous Integration and Continuous Deployment setup for this project.

---

## 📋 **Table of Contents**

- [Overview](#overview)
- [CI/CD Workflows](#cicd-workflows)
- [Setup Instructions](#setup-instructions)
- [Workflow Details](#workflow-details)
- [Badges](#badges)
- [Troubleshooting](#troubleshooting)

---

## Overview

This project uses **GitHub Actions** for CI/CD - completely free for open-source projects with 2,000 minutes/month.

### What's Automated

- ✅ **Testing**: Jest tests on multiple Node.js versions
- ✅ **Linting**: ESLint code quality checks
- ✅ **Type Checking**: TypeScript validation
- ✅ **Building**: Package build verification
- ✅ **Publishing**: Automated NPM releases
- ✅ **Coverage**: Codecov integration
- ✅ **Security**: Dependency scanning and audits
- ✅ **PR Validation**: Automated PR checks

### Cost

**$0/month** - All services used are free for open-source:
- GitHub Actions: 2,000 minutes/month
- Codecov: Free for open-source
- npm: Free for public packages

---

## CI/CD Workflows

### 1. **CI Workflow** (`.github/workflows/ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`

**Jobs:**
- Test on Node.js 20.x
- TypeScript type checking
- ESLint linting
- Build package
- Validate package
- Android lint checks
- Upload coverage to Codecov

**Duration:** ~2-3 minutes

---

### 2. **Publish Workflow** (`.github/workflows/publish.yml`)

**Triggers:**
- GitHub Release created
- Manual workflow dispatch

**Jobs:**
- Run all tests
- Build package
- Check if version already published
- Publish to NPM
- Create GitHub release (if manual)
- Send notifications

**Duration:** ~2-3 minutes

**Requirements:**
- `NPM_TOKEN` secret configured
- Version in `package.json` updated

---

### 3. **PR Validation** (`.github/workflows/pr-validation.yml`)

**Triggers:**
- Pull request opened, synchronized, or reopened

**Jobs:**
- Validate PR title format (semantic versioning)
- Check PR size (files & lines changed)
- Auto-label based on files changed
- Comment on new PRs with checklist
- Security scan (npm audit)
- Dependency review
- Validate Android manifest

**Duration:** ~2-3 minutes

---

## Setup Instructions

### Step 1: Enable GitHub Actions

GitHub Actions is enabled by default for public repos. No setup needed!

### Step 2: Configure NPM Token

For automated publishing to NPM:

1. **Generate NPM Token:**
   ```bash
   npm login
   npm token create
   ```

2. **Add to GitHub Secrets:**
   - Go to: Repository → Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `NPM_TOKEN`
   - Value: Your NPM token
   - Click "Add secret"

### Step 3: Configure Codecov (Optional but Recommended)

For test coverage reporting:

1. **Sign up at [codecov.io](https://codecov.io) with GitHub**

2. **Enable this repository** in Codecov dashboard

3. **Get Codecov token:**
   - Go to repository settings in Codecov
   - Copy the upload token

4. **Add to GitHub Secrets:**
   - Name: `CODECOV_TOKEN`
   - Value: Your Codecov token

**Note:** Codecov often works without a token for public repos, but adding it ensures reliability.

### Step 4: Verify Workflows

Push to `main` branch or create a PR to trigger workflows:

```bash
git add .
git commit -m "ci: add CI/CD workflows"
git push origin main
```

Check workflow runs:
- Go to: Repository → Actions tab
- View workflow runs and logs

---

## Workflow Details

### CI Workflow Testing

Tests run on Node.js 20.x (LTS):

```yaml
- name: Setup Node.js 20.x
  uses: actions/setup-node@v4
  with:
    node-version: '20.x'
```

**Why Node 20.x only?**
- Node 20 is the current LTS version
- Saves CI minutes (important for free tier)
- React Native 0.68+ works best with Node 16+
- Most developers use Node 18 or 20

### Publishing Process

**Automatic on Release:**
```bash
# 1. Update version in package.json
npm version patch  # or minor/major

# 2. Commit and push
git push origin main --tags

# 3. Create GitHub Release
# Go to: Repository → Releases → Create new release
# Select your tag (e.g., v3.0.0)
# Click "Publish release"

# 4. Workflow automatically publishes to NPM!
```

**Manual Publishing:**
```bash
# Trigger workflow manually
# Go to: Actions → Publish to NPM → Run workflow
```

### PR Validation Details

**Semantic PR Titles:**
```
✅ feat: add new feature
✅ fix: resolve bug in service
✅ docs: update README
✅ chore: update dependencies

❌ Added new feature
❌ Fix bug
❌ Update
```

**Allowed types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style (formatting)
- `refactor`: Code refactoring
- `perf`: Performance improvement
- `test`: Tests
- `chore`: Maintenance
- `ci`: CI/CD changes
- `build`: Build system
- `revert`: Revert changes

---

## Badges

Add these badges to your README for visibility:

### CI Status Badge
```markdown
[![CI](https://github.com/paulkiren/react-native-foreground-service-turbo/actions/workflows/ci.yml/badge.svg)](https://github.com/paulkiren/react-native-foreground-service-turbo/actions/workflows/ci.yml)
```

### Codecov Badge
```markdown
[![codecov](https://codecov.io/gh/paulkiren/react-native-foreground-service-turbo/branch/main/graph/badge.svg)](https://codecov.io/gh/paulkiren/react-native-foreground-service-turbo)
```

### NPM Version Badge
```markdown
[![NPM Version](https://img.shields.io/npm/v/@kirenpaul/react-native-foreground-service-turbo.svg)](https://www.npmjs.com/package/@kirenpaul/react-native-foreground-service-turbo)
```

---

## Troubleshooting

### Issue: Tests Failing in CI but Pass Locally

**Cause:** Environment differences

**Solution:**
```bash
# Match CI Node version locally
nvm use 20

# Clear cache
rm -rf node_modules package-lock.json
npm install

# Run tests
npm test
```

### Issue: NPM Publish Fails with "401 Unauthorized"

**Cause:** Invalid or expired NPM token

**Solution:**
1. Generate new NPM token:
   ```bash
   npm token create
   ```

2. Update GitHub secret `NPM_TOKEN`

3. Re-run workflow

### Issue: Codecov Upload Fails

**Cause:** Missing or invalid `CODECOV_TOKEN`

**Solution:**
1. Get token from [codecov.io](https://codecov.io)
2. Add as GitHub secret: `CODECOV_TOKEN`
3. Re-run workflow

**Note:** For public repos, Codecov often works without token. Check Codecov dashboard.

### Issue: Workflow Not Triggering

**Cause:** Workflow file syntax error or wrong triggers

**Solution:**
1. Validate YAML syntax:
   ```bash
   # Use a YAML validator online or:
   yamllint .github/workflows/ci.yml
   ```

2. Check workflow runs:
   - Repository → Actions → Check for errors

3. Verify branch names in workflow triggers match your branches

### Issue: Android Lint Fails

**Cause:** Missing Gradle wrapper or configuration

**Solution:**
This is expected for libraries. The Android lint job is marked as `continue-on-error: true`, so it won't fail the build.

---

## Advanced Configuration

### Running Workflows Locally

Test workflows before pushing using [act](https://github.com/nektos/act):

```bash
# Install act
brew install act  # macOS
# or check: https://github.com/nektos/act#installation

# Run CI workflow locally
act -j test

# Run specific job
act -j build
```

### Custom Workflow Triggers

Modify `.github/workflows/ci.yml` to add custom triggers:

```yaml
on:
  push:
    branches: [main, develop, 'feature/**']
  pull_request:
    branches: [main, develop]
  schedule:
    - cron: '0 0 * * 0'  # Weekly on Sunday
```

### Caching Dependencies

All workflows use npm caching for faster runs:

```yaml
- uses: actions/setup-node@v4
  with:
    cache: 'npm'
```

This caches `node_modules` between runs, reducing install time from ~60s to ~10s.

---

## Monitoring

### GitHub Actions Dashboard

View workflow runs:
- Repository → Actions tab
- Filter by workflow, branch, status
- View logs for debugging

### Codecov Dashboard

View coverage reports:
- [codecov.io/gh/paulkiren/react-native-foreground-service-turbo](https://codecov.io/gh/paulkiren/react-native-foreground-service-turbo)
- Track coverage trends
- View coverage by file
- Compare branches

### NPM Package

View published versions:
- [npmjs.com/package/@kirenpaul/react-native-foreground-service-turbo](https://www.npmjs.com/package/@kirenpaul/react-native-foreground-service-turbo)
- Download statistics
- Version history

---

## Best Practices

### 1. Keep Tests Fast
- Target: < 5 minutes total CI time
- Use test parallelization
- Mock external dependencies

### 2. Fail Fast
- Run linting before tests
- Run type checking early
- Stop on first major error

### 3. Cache Aggressively
- Cache `node_modules`
- Cache Gradle dependencies
- Cache build outputs

### 4. Monitor CI Minutes
- GitHub provides 2,000 free minutes/month
- Check usage: Settings → Billing → Usage
- Optimize workflows if nearing limit

### 5. Secure Secrets
- Never commit secrets to code
- Use GitHub Secrets for sensitive data
- Rotate tokens periodically

---

## CI/CD Checklist

Before pushing to production:

- [ ] All tests pass locally
- [ ] Version bumped in `package.json`
- [ ] CHANGELOG.md updated
- [ ] NPM token configured in GitHub secrets
- [ ] Codecov token configured (optional)
- [ ] README badges updated
- [ ] Workflows validated with act (optional)
- [ ] Branch protection rules enabled (recommended)

---

## Additional Resources

- **GitHub Actions Docs**: https://docs.github.com/en/actions
- **Codecov Docs**: https://docs.codecov.com
- **npm Publishing**: https://docs.npmjs.com/cli/v9/commands/npm-publish
- **Semantic Versioning**: https://semver.org

---

## Summary

**Your CI/CD is now fully automated and FREE!** 🎉

Every push and PR will:
- ✅ Run tests automatically
- ✅ Check code quality
- ✅ Validate package builds
- ✅ Report test coverage
- ✅ Scan for security issues

Every release will:
- ✅ Publish to NPM automatically
- ✅ Create GitHub release notes
- ✅ Notify on success

**Total cost:** $0/month for open-source! 💰

---

**Last Updated:** December 17, 2025
**Maintained by:** Kiren Paul
