# Package Name Change Notice

## 📦 New Package Name

This package has been renamed to follow React Native naming conventions:

**Old Name:** `@kirenpaul/rn-foreground-service-turbo`
**New Name:** `@kirenpaul/react-native-foreground-service-turbo`

---

## ✨ Why the Change?

The package name has been updated to:
- ✅ Follow React Native community naming conventions (`react-native-*`)
- ✅ Improve discoverability in npm searches
- ✅ Maintain consistency with other React Native packages
- ✅ Keep the scoped namespace for security and branding

---

## 🚀 Migration Instructions

### If you're using the old package name:

The old package name `@kirenpaul/rn-foreground-service-turbo` will continue to work, but please migrate to the new name for future updates.

### How to Migrate:

**Step 1: Uninstall the old package**
```bash
npm uninstall @kirenpaul/rn-foreground-service-turbo
# or
yarn remove @kirenpaul/rn-foreground-service-turbo
```

**Step 2: Install the new package**
```bash
npm install @kirenpaul/react-native-foreground-service-turbo
# or
yarn add @kirenpaul/react-native-foreground-service-turbo
```

**Step 3: Update your imports**
```typescript
// Before
import ForegroundService from '@kirenpaul/rn-foreground-service-turbo';

// After
import ForegroundService from '@kirenpaul/react-native-foreground-service-turbo';
```

**Step 4: Clean and rebuild**
```bash
# Clean cache
rm -rf node_modules
npm install  # or yarn install

# Clean Android build
cd android && ./gradlew clean && cd ..

# Rebuild your app
npm run android  # or yarn android
```

---

## 📝 No Code Changes Required

**Good news!** The API remains exactly the same. Only the package name has changed. Your existing code will work without modifications after updating the import statements.

---

## ⚠️ Deprecation Timeline

| Date | Action |
|------|--------|
| December 17, 2025 | New package name announced |
| January 17, 2026 | Old package marked as deprecated on npm |
| March 17, 2026 | Old package will no longer receive updates |
| June 17, 2026 | Old package may be unpublished |

**Please migrate before January 17, 2026 to ensure continued updates and support.**

---

## 🔗 Resources

- **New Package**: https://www.npmjs.com/package/@kirenpaul/react-native-foreground-service-turbo
- **GitHub**: https://github.com/paulkiren/react-native-foreground-service-turbo
- **Migration Guide**: [MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md)
- **Changelog**: [CHANGELOG.md](./CHANGELOG.md)
- **Issues**: https://github.com/paulkiren/react-native-foreground-service-turbo/issues

---

## ❓ Questions?

If you have questions or encounter issues during migration:

1. Check the [Migration Guide](./MIGRATION_GUIDE.md)
2. Search existing [GitHub Issues](https://github.com/paulkiren/react-native-foreground-service-turbo/issues)
3. Open a new issue if needed

---

## 🙏 Thank You

Thank you for using this library! We appreciate your patience during this transition to a more discoverable package name.

The new naming convention will help more developers find and use this library, benefiting the entire React Native community.

---

**Last Updated:** December 17, 2025
**Effective:** Immediately
**Package Version:** 3.0.0+
