# Repository Cleanup Action Plan

Quick reference guide for cleaning up the repository based on the analysis.

---

## 🎯 **TL;DR - What Needs Fixing**

1. ⚠️ **Version mismatch** - package.json says `0.1.0`, CHANGELOG says `3.0.0`
2. ⚠️ **Unused CI files** - codecov.yml, labeler.yml, ACTION_VERSIONS.md (no workflows use them)
3. ⚠️ **Outdated docs** - docs/internal has historical notes about removed features
4. ⚠️ **Untracked files** - SECURITY.md and SECURITY_AUDIT_RESOLUTION.md not committed

---

## ✅ **Quick Cleanup Commands**

### Option 1: Keep It Simple (Recommended)

```bash
# 1. Fix version (choose one):
# For first release:
npm version 1.0.0 --no-git-tag-version

# Or keep pre-release:
# (0.1.0 is fine if you want to test first)

# 2. Remove unused CI files
rm codecov.yml
rm .github/labeler.yml
rm .github/ACTION_VERSIONS.md

# 3. Archive outdated docs
mkdir -p docs/archive
mv docs/internal/CICD_SETUP_SUMMARY.md docs/archive/
mv docs/internal/CI_OPTIMIZATION.md docs/archive/
mv docs/internal/RENAME_SUMMARY.md docs/archive/
mv docs/internal/TESTING_STATUS.md docs/archive/
mv docs/internal/TEST_FIXES_APPLIED.md docs/archive/
mv docs/internal/FIXES_SUMMARY.md docs/archive/
mv "docs/internal/TOP PRIORITY RECOMMENDATIONS.md" docs/archive/

# 4. Add archive to gitignore
echo "docs/archive/" >> .gitignore

# 5. Move key docs to root
mv docs/external/TESTING.md ./
mv docs/external/ANDROID_TESTING.md ./

# 6. Update .npmignore
cat >> .npmignore << 'EOF'

# Docs
docs/
SECURITY_AUDIT_RESOLUTION.md
CLEANUP_ACTION_PLAN.md
REPOSITORY_ANALYSIS.md

# CI/CD
.github/

# Coverage
coverage/
EOF

# 7. Commit everything
git add .
git commit -m "chore: clean up repository for v1.0.0 release

- Update version to 1.0.0
- Remove unused CI/CD configuration files
- Archive outdated internal documentation
- Move key docs to repository root
- Add security policy documents
- Update .npmignore for cleaner package"

# 8. Push
git push origin main
```

---

## 📋 **Detailed Steps**

### Step 1: Decide on Version Number

**Current state:**
- `package.json`: `0.1.0`
- `CHANGELOG.md`: References `3.0.0` release

**Choose one:**

**A) First Release → 1.0.0** (Recommended)
```bash
npm version 1.0.0 --no-git-tag-version
# Then update CHANGELOG.md to show 1.0.0 instead of 3.0.0
```

**B) Pre-release → Keep 0.1.0**
```bash
# No change needed
# Update CHANGELOG.md to show 0.1.0 releases
```

**C) Follow existing docs → 3.0.0**
```bash
npm version 3.0.0 --no-git-tag-version
# Keep CHANGELOG.md as-is
```

### Step 2: Remove Unused Files

These files describe features that don't exist:

```bash
# Remove CI/CD config files (no workflows use them)
rm codecov.yml
rm .github/labeler.yml
rm .github/ACTION_VERSIONS.md

# Confirm removal
git status
```

### Step 3: Archive Old Internal Docs

Keep valuable docs, archive historical ones:

```bash
# Create archive directory (not committed to git)
mkdir -p docs/archive

# Move outdated docs
mv docs/internal/CICD_SETUP_SUMMARY.md docs/archive/
mv docs/internal/CI_OPTIMIZATION.md docs/archive/
mv docs/internal/RENAME_SUMMARY.md docs/archive/
mv docs/internal/TESTING_STATUS.md docs/archive/
mv docs/internal/TEST_FIXES_APPLIED.md docs/archive/
mv docs/internal/FIXES_SUMMARY.md docs/archive/
mv "docs/internal/TOP PRIORITY RECOMMENDATIONS.md" docs/archive/

# Add to gitignore so archive isn't committed
echo "docs/archive/" >> .gitignore

# Keep these (valuable for future developers):
# - docs/internal/CRITICAL_FIXES_APPLIED.md
# - docs/internal/IMPLEMENTATION_SUMMARY.md
# - docs/internal/TEST_SUMMARY.md
```

### Step 4: Reorganize External Docs

Move important user docs to root:

```bash
# Move to root for better visibility
mv docs/external/TESTING.md ./
mv docs/external/ANDROID_TESTING.md ./

# Keep in docs/external (reference materials):
# - docs/external/MIGRATION_GUIDE.md
# - docs/external/MIGRATION.md
# - docs/external/DEPRECATION_NOTICE.md
# - docs/external/CI_CD.md (needs updating though)
```

### Step 5: Update .npmignore

Exclude internal docs from npm package:

```bash
# Edit .npmignore or append:
cat >> .npmignore << 'EOF'

# Documentation (keep only README, CHANGELOG, LICENSE)
docs/
SECURITY_AUDIT_RESOLUTION.md
CLEANUP_ACTION_PLAN.md
REPOSITORY_ANALYSIS.md
TESTING.md
ANDROID_TESTING.md
CONTRIBUTING.md
MIGRATION_GUIDE.md

# CI/CD
.github/

# Test coverage
coverage/

# Development files
**/__tests__/
**/__mocks__/
**/__fixtures__/
jest.config.js
jest.setup.js
.eslintrc.js
.eslintignore
.prettierrc.json
babel.config.js
tsconfig.json
EOF
```

### Step 6: Commit Untracked Files

```bash
# Add new files
git add SECURITY.md
git add SECURITY_AUDIT_RESOLUTION.md

# Check status
git status
```

### Step 7: Final Commit

```bash
git add .
git commit -m "chore: clean up repository for first release

Breaking changes:
- Update version to 1.0.0 (or your chosen version)

Changes:
- Remove unused CI/CD configuration files (codecov.yml, labeler.yml)
- Archive outdated internal documentation
- Move key user docs to repository root
- Add security policy and audit resolution
- Update .npmignore to exclude internal docs
- Improve package publishing configuration"

git push origin main
```

---

## 📦 **What Will Be Published to NPM**

After cleanup, `npm publish` will include:

```
@kirenpaul/react-native-foreground-service-turbo/
├── android/           # Native code
├── lib/               # Built JavaScript
├── src/               # TypeScript source
├── turbomodule-specs/ # TurboModule spec
├── scripts/           # Setup scripts
├── README.md          # Main docs
├── CHANGELOG.md       # Version history
├── LICENSE            # MIT license
└── package.json       # Package metadata
```

**Will NOT include** (thanks to .npmignore):
- docs/ folder
- .github/ folder
- Test files
- Coverage reports
- Internal documentation
- Development config files

**Package size:** ~100-200KB (small and efficient!)

---

## 🔍 **Verification Checklist**

After cleanup, verify:

```bash
# 1. Check version matches everywhere
grep -r "version" package.json CHANGELOG.md

# 2. Test build
npm run prepare

# 3. Test publish (dry run)
npm pack --dry-run

# 4. Check what will be published
npm pack
tar -tzf *.tgz | head -20
rm *.tgz

# 5. Verify tests still pass
npm test

# 6. Check git status
git status

# 7. Verify .npmignore works
npm publish --dry-run
```

---

## 🚀 **Ready to Publish?**

Once cleanup is done:

```bash
# Option 1: Automatic (push to main → auto-publish)
git push origin main
# Your GitHub Action will automatically publish!

# Option 2: Manual
npm publish --access public
```

---

## 📊 **Before vs After**

### Before Cleanup
```
22 markdown files
- Outdated CI/CD docs
- Version mismatch (0.1.0 vs 3.0.0)
- Unused config files
- Untracked security files
- Mixed doc organization
```

### After Cleanup
```
~12-15 markdown files
✅ Version consistent
✅ No unused files
✅ Security files tracked
✅ Docs well-organized
✅ Ready for release
```

---

## ⚠️ **Common Issues**

### Issue: "Cannot find module after cleanup"
**Solution:** Run `npm install` and `npm run prepare`

### Issue: "Tests fail after moving files"
**Solution:** Update import paths in tests if you moved files

### Issue: "Package too large"
**Solution:** Check .npmignore is properly excluding docs/ and .github/

### Issue: "Workflow fails after removing files"
**Solution:** The simple publish.yml doesn't use codecov.yml or labeler.yml, so it should work fine

---

## 💡 **Tips**

1. **Test before publishing:**
   ```bash
   npm pack
   tar -tzf *.tgz
   ```

2. **Keep backup:**
   ```bash
   git branch backup-before-cleanup
   ```

3. **Incremental approach:**
   - Do steps 1-3 first
   - Test
   - Then do steps 4-7

4. **Ask for review:**
   - Create PR instead of pushing directly
   - Get team feedback

---

## ✅ **Quick Validation Commands**

Run these to ensure everything is good:

```bash
# Version consistency
grep version package.json
grep -A 2 "\[" CHANGELOG.md | head -5

# Build works
npm run prepare && npm test

# Package size check
npm pack --dry-run

# Git status
git status

# What will be published
npm publish --dry-run --access public
```

---

**Last Updated:** December 17, 2025
**Status:** Ready for cleanup
**Estimated Time:** 10-15 minutes
