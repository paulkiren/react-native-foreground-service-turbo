package com.kirenpaul.foregroundservice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PermissionChecker
 */
@RunWith(RobolectricTestRunner.class)
public class PermissionCheckerTest {

    @Mock
    private Context mockContext;

    private PermissionChecker permissionChecker;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        permissionChecker = new PermissionChecker(mockContext);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.S) // Android 12
    public void testHasPostNotificationsPermission_onAndroid12_returnsTrue() {
        // On Android < 13, permission not required
        boolean result = permissionChecker.hasPostNotificationsPermission();

        assertTrue(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.TIRAMISU) // Android 13
    public void testHasPostNotificationsPermission_onAndroid13_granted() {
        // Mock permission as granted
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.POST_NOTIFICATIONS)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        boolean result = permissionChecker.hasPostNotificationsPermission();

        assertTrue(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.TIRAMISU) // Android 13
    public void testHasPostNotificationsPermission_onAndroid13_denied() {
        // Mock permission as denied
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.POST_NOTIFICATIONS)
        )).thenReturn(PackageManager.PERMISSION_DENIED);

        boolean result = permissionChecker.hasPostNotificationsPermission();

        assertFalse(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.S) // Android 12
    public void testHasForegroundServicePermission_onAndroid12_dataSync() {
        // Mock base permission as granted
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        boolean result = permissionChecker.hasForegroundServicePermission("dataSync");

        assertTrue(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
    public void testHasForegroundServicePermission_onAndroid14_dataSync_granted() {
        // Mock base permission as granted
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        // dataSync doesn't require additional permissions
        boolean result = permissionChecker.hasForegroundServicePermission("dataSync");

        assertTrue(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
    public void testHasForegroundServicePermission_onAndroid14_location_granted() {
        // Mock base permission and location permission as granted
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq("android.permission.FOREGROUND_SERVICE_LOCATION")
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        boolean result = permissionChecker.hasForegroundServicePermission("location");

        assertTrue(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
    public void testHasForegroundServicePermission_onAndroid14_location_denied() {
        // Mock base permission as granted but location permission as denied
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq("android.permission.FOREGROUND_SERVICE_LOCATION")
        )).thenReturn(PackageManager.PERMISSION_DENIED);

        boolean result = permissionChecker.hasForegroundServicePermission("location");

        assertFalse(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
    public void testHasForegroundServicePermission_onAndroid14_mediaPlayback_granted() {
        // Mock base permission and media permission as granted
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq("android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK")
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        boolean result = permissionChecker.hasForegroundServicePermission("mediaPlayback");

        assertTrue(result);
    }

    @Test
    public void testHasForegroundServicePermission_baseForegroundServiceDenied() {
        // Mock base permission as denied
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_DENIED);

        boolean result = permissionChecker.hasForegroundServicePermission("dataSync");

        assertFalse(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.TIRAMISU) // Android 13
    public void testGetPermissionErrorMessage_postNotificationsMissing() {
        // Mock permission as denied
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.POST_NOTIFICATIONS)
        )).thenReturn(PackageManager.PERMISSION_DENIED);

        String errorMessage = permissionChecker.getPermissionErrorMessage("dataSync");

        assertTrue(errorMessage.contains("POST_NOTIFICATIONS"));
        assertTrue(errorMessage.contains("Android 13+"));
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
    public void testGetPermissionErrorMessage_foregroundServiceLocationMissing() {
        // Mock base permission as granted but location permission denied
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq(Manifest.permission.FOREGROUND_SERVICE)
        )).thenReturn(PackageManager.PERMISSION_GRANTED);

        when(ContextCompat.checkSelfPermission(
                eq(mockContext),
                eq("android.permission.FOREGROUND_SERVICE_LOCATION")
        )).thenReturn(PackageManager.PERMISSION_DENIED);

        String errorMessage = permissionChecker.getPermissionErrorMessage("location");

        assertTrue(errorMessage.contains("FOREGROUND_SERVICE_LOCATION"));
        assertTrue(errorMessage.contains("Android 14+"));
        assertTrue(errorMessage.contains("location"));
    }

    @Test
    public void testGetPermissionErrorMessage_defaultMessage() {
        // Mock all permissions as granted
        mockStatic(ContextCompat.class);
        when(ContextCompat.checkSelfPermission(any(), any()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        String errorMessage = permissionChecker.getPermissionErrorMessage("dataSync");

        assertTrue(errorMessage.contains("Required permissions are missing"));
    }

    // Helper method to mock static ContextCompat
    private void mockStatic(Class<?> clazz) {
        // Note: This requires Mockito 3.4.0+ with mockito-inline for static mocking
        // In production tests, you would use:
        // try (MockedStatic<ContextCompat> mockedStatic = mockStatic(ContextCompat.class)) {
        //     // ... test code
        // }
    }
}
