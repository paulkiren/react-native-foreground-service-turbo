# Security Policy

## Supported Versions

We release patches for security vulnerabilities in the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 3.x.x   | :white_check_mark: |
| < 3.0   | :x:                |

---

## Reporting a Vulnerability

If you discover a security vulnerability, please report it by:

1. **DO NOT** open a public issue
2. Email: kiren.paul@example.com
3. Or use GitHub Security Advisories: [Report a vulnerability](https://github.com/paulkiren/react-native-foreground-service-turbo/security/advisories/new)

### What to Include

- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

### Response Time

- **Acknowledgment:** Within 48 hours
- **Assessment:** Within 7 days
- **Fix & Release:** Within 30 days (for critical issues)

---

## Security Practices

### Automated Scanning

Our CI/CD pipeline includes:
- ✅ `npm audit` on every PR
- ✅ Dependency vulnerability scanning
- ✅ GitHub Dependabot alerts
- ✅ CodeQL analysis (planned)

### Dependency Management

#### Production Dependencies

**Zero vulnerabilities tolerated** in production dependencies (packages that users install).

**Current status:** ✅ No production dependencies with vulnerabilities

#### Development Dependencies

Development dependencies (testing, building, linting tools) are evaluated case-by-case:

- **Low/Moderate:** Acceptable if not exploitable in CI/CD environment
- **High/Critical:** Must be addressed or dependencies removed

**Current approach:**
- Removed `release-it` (had 10 moderate vulnerabilities)
- Using GitHub Actions for publishing instead
- Accepting React Native CLI vulnerabilities (development tooling only)

---

## Known Issues

### React Native Development Dependencies

**Status:** Accepted (Low Risk)
**Affected Package:** `react-native@0.72.0` (devDependency)
**Vulnerabilities:** 5 high severity (ip package - SSRF)
**Risk Assessment:**
- ✅ Development dependency only (not included in published package)
- ✅ Only used during local development
- ✅ Not exposed to end users
- ✅ React Native 0.72 is required for testing compatibility

**Mitigation:**
- Use in isolated development environment only
- Do not expose Metro bundler to untrusted networks
- Update to React Native 0.73+ when testing requirements allow

**Timeline:**
- Monitoring React Native releases
- Will update when 0.73+ reaches stable for our use case
- Regular reviews every quarter

---

## Audit Results

Last audit: December 17, 2025

### Production Dependencies
```
0 vulnerabilities (0 low, 0 moderate, 0 high, 0 critical)
```

### Development Dependencies
```
5 vulnerabilities (all from React Native CLI - accepted)
```

---

## Update Policy

### Immediate Updates

We update immediately for:
- ✅ Critical vulnerabilities in production dependencies
- ✅ High vulnerabilities in production dependencies
- ✅ Any exploitable vulnerability

### Planned Updates

Quarterly reviews for:
- 🔄 Moderate vulnerabilities in dev dependencies
- 🔄 Dependency version updates
- 🔄 Security best practices

### Exceptions

Development dependencies with vulnerabilities are acceptable if:
1. Not included in published package
2. Not exploitable in CI/CD environment
3. Required for essential tooling
4. No suitable alternative available
5. Documented in this policy

---

## Security Features

### Library Design

Our library is designed with security in mind:

- ✅ No network requests
- ✅ No file system access (except Android service)
- ✅ No external API calls
- ✅ Sandboxed Android service
- ✅ Minimal permissions required
- ✅ No eval() or dynamic code execution
- ✅ Type-safe (TypeScript)

### Android Security

- ✅ Services not exported (`android:exported="false"`)
- ✅ Proper PendingIntent flags (FLAG_IMMUTABLE)
- ✅ No SQL injection vectors
- ✅ No WebView usage
- ✅ Minimal permission scope

---

## Disclosure Policy

When a security issue is fixed:

1. **Private Fix:** Develop and test fix privately
2. **Security Advisory:** Create GitHub Security Advisory
3. **Release:** Publish patched version
4. **Disclosure:** Public disclosure 7 days after release
5. **Credit:** Credit reporter in CHANGELOG (if desired)

---

## Security Checklist for Contributors

Before submitting a PR:

- [ ] No secrets in code
- [ ] No unsafe dependencies added
- [ ] `npm audit` shows no new production vulnerabilities
- [ ] No eval() or Function() usage
- [ ] Input validation for user-provided data
- [ ] Proper error handling (no info leaks)
- [ ] Android permissions justified
- [ ] No exported components without need

---

## Resources

- **npm Security Best Practices**: https://docs.npmjs.com/packages-and-modules/securing-your-code
- **OWASP Top 10**: https://owasp.org/www-project-top-ten/
- **GitHub Security**: https://docs.github.com/en/code-security

---

## Contact

- **Security Issues**: kiren.paul@example.com
- **GitHub Security**: [Security Advisories](https://github.com/paulkiren/react-native-foreground-service-turbo/security/advisories)
- **General Issues**: [GitHub Issues](https://github.com/paulkiren/react-native-foreground-service-turbo/issues)

---

**Last Updated:** December 17, 2025
**Next Review:** March 2026
