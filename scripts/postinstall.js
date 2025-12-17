const fs = require('fs');
const path = require('path');

console.log(
  '\n[@kirenpaul/rn-foreground-service-turbo] Running postinstall setup...\n'
);

const projectRoot = process.cwd();

// Paths
const androidManifestPath = path.join(
  projectRoot,
  'android/app/src/main/AndroidManifest.xml'
);
const colorsPath = path.join(
  projectRoot,
  'android/app/src/main/res/values/colors.xml'
);

// Permission template for Android 13+ and 14+
const permissionsTemplate = `
  <!-- Foreground Service Permissions -->
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
  <uses-permission android:name="android.permission.WAKE_LOCK" />
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
`;

// Metadata and service template
const applicationContentTemplate = `
  <!-- Foreground Service Configuration -->
  <meta-data
    android:name="com.kirenpaul.foregroundservice.notification_channel_name"
    android:value="Foreground Service"
  />
  <meta-data
    android:name="com.kirenpaul.foregroundservice.notification_channel_description"
    android:value="Persistent notification for foreground service"
  />
  <meta-data
    android:name="com.kirenpaul.foregroundservice.notification_color"
    android:resource="@color/notification_color"
  />

  <!-- Foreground Services -->
  <service
    android:name="com.kirenpaul.foregroundservice.ForegroundService"
    android:exported="false"
    android:foregroundServiceType="dataSync|location|mediaPlayback" />
  <service
    android:name="com.kirenpaul.foregroundservice.ForegroundServiceTask"
    android:exported="false" />
`;

try {
  // Check if AndroidManifest.xml exists
  if (!fs.existsSync(androidManifestPath)) {
    console.warn(
      '⚠️  Warning: AndroidManifest.xml not found at:',
      androidManifestPath
    );
    console.warn(
      '   Please ensure you have initialized your React Native project.\n'
    );
    process.exit(0);
  }

  let manifest = fs.readFileSync(androidManifestPath, 'utf8');
  let modified = false;

  // Add permissions if not present
  if (!manifest.includes('android.permission.POST_NOTIFICATIONS')) {
    manifest = manifest.replace(
      /(<manifest[^>]*>)/,
      (match) => match + permissionsTemplate
    );
    console.log('✅ Added permissions to AndroidManifest.xml');
    modified = true;
  } else {
    console.log('✓  Permissions already exist in AndroidManifest.xml');
  }

  // Add service declarations if not present
  if (!manifest.includes('com.kirenpaul.foregroundservice.ForegroundService')) {
    manifest = manifest.replace(
      /(<application[^>]*>)/,
      (match) => match + applicationContentTemplate
    );
    console.log('✅ Added service declarations to AndroidManifest.xml');
    modified = true;
  } else {
    console.log('✓  Service declarations already exist in AndroidManifest.xml');
  }

  if (modified) {
    fs.writeFileSync(androidManifestPath, manifest, 'utf8');
  }

  // Create colors.xml if it doesn't exist
  const colorsDir = path.dirname(colorsPath);
  if (!fs.existsSync(colorsDir)) {
    fs.mkdirSync(colorsDir, { recursive: true });
  }

  if (!fs.existsSync(colorsPath)) {
    const colorsContent = `<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="notification_color">#00C4D1</color>
</resources>`;

    fs.writeFileSync(colorsPath, colorsContent, 'utf8');
    console.log('✅ Created colors.xml');
  } else {
    // Check if notification_color exists
    const colorsContent = fs.readFileSync(colorsPath, 'utf8');
    if (!colorsContent.includes('notification_color')) {
      const updatedColors = colorsContent.replace(
        /<\/resources>/,
        '    <color name="notification_color">#00C4D1</color>\n</resources>'
      );
      fs.writeFileSync(colorsPath, updatedColors, 'utf8');
      console.log('✅ Added notification_color to colors.xml');
    } else {
      console.log('✓  colors.xml already configured');
    }
  }

  console.log('\n✅ Setup completed successfully!\n');
  console.log(
    '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
  );
  console.log('  IMPORTANT: Android 13+ Runtime Permission Required');
  console.log(
    '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n'
  );
  console.log(
    'For Android 13+, you must request POST_NOTIFICATIONS permission:\n'
  );
  console.log(
    '  import { PermissionsAndroid, Platform } from "react-native";\n',
  );
  console.log('  if (Platform.OS === "android" && Platform.Version >= 33) {');
  console.log('    await PermissionsAndroid.request(');
  console.log('      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS');
  console.log('    );');
  console.log('  }\n');
  console.log(
    '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
  );
  console.log('  Android 14+ Service Type Required');
  console.log(
    '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n'
  );
  console.log('For Android 14+, specify serviceType when starting service:\n');
  console.log('  await ForegroundService.start({');
  console.log('    id: 1,');
  console.log('    title: "My Service",');
  console.log('    message: "Running...",');
  console.log(
    '    serviceType: "dataSync" // or "location" or "mediaPlayback"',
  );
  console.log('  });\n');
  console.log(
    '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n'
  );
  console.log(
    '📚 Documentation: https://github.com/paulkiren/rn-foreground-service-turbo\n'
  );
} catch (error) {
  console.error('\n❌ Error during setup:', error.message);
  console.error('\n⚠️  Please configure AndroidManifest.xml manually.');
  console.error(
    '   See: https://github.com/paulkiren/rn-foreground-service-turbo#setup\n'
  );
}
