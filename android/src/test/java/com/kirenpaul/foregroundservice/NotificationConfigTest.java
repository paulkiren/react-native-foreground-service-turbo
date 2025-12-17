package com.kirenpaul.foregroundservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationConfig
 */
@RunWith(RobolectricTestRunner.class)
public class NotificationConfigTest {

    @Mock
    private Context mockContext;

    @Mock
    private PackageManager mockPackageManager;

    @Mock
    private ApplicationInfo mockApplicationInfo;

    private Bundle mockMetadata;

    @Before
    public void setUp() throws PackageManager.NameNotFoundException {
        MockitoAnnotations.openMocks(this);

        mockMetadata = new Bundle();

        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
        when(mockContext.getPackageName()).thenReturn("com.test.app");

        mockApplicationInfo.metaData = mockMetadata;

        when(mockPackageManager.getApplicationInfo(anyString(), anyInt()))
                .thenReturn(mockApplicationInfo);
    }

    @Test
    public void testGetChannelName_fromMetadata() {
        mockMetadata.putString(
                "com.kirenpaul.foregroundservice.notification_channel_name",
                "Custom Channel Name"
        );

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals("Custom Channel Name", config.getChannelName());
    }

    @Test
    public void testGetChannelName_defaultWhenNotInMetadata() {
        // Don't put anything in metadata

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals("Foreground Service", config.getChannelName());
    }

    @Test
    public void testGetChannelName_defaultWhenEmptyString() {
        mockMetadata.putString(
                "com.kirenpaul.foregroundservice.notification_channel_name",
                ""
        );

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals("Foreground Service", config.getChannelName());
    }

    @Test
    public void testGetChannelDescription_fromMetadata() {
        mockMetadata.putString(
                "com.kirenpaul.foregroundservice.notification_channel_description",
                "Custom Description"
        );

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals("Custom Description", config.getChannelDescription());
    }

    @Test
    public void testGetChannelDescription_defaultWhenNotInMetadata() {
        // Don't put anything in metadata

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals("Persistent notification for foreground service", config.getChannelDescription());
    }

    @Test
    public void testGetChannelDescription_defaultWhenEmptyString() {
        mockMetadata.putString(
                "com.kirenpaul.foregroundservice.notification_channel_description",
                ""
        );

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals("Persistent notification for foreground service", config.getChannelDescription());
    }

    @Test
    public void testGetNotificationColor_returnsMinusOneWhenNotSet() {
        // Don't put anything in metadata

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals(-1, config.getNotificationColor());
    }

    @Test
    public void testGetNotificationColor_returnsMinusOneOnError() throws PackageManager.NameNotFoundException {
        // Simulate error when getting application info
        when(mockPackageManager.getApplicationInfo(anyString(), anyInt()))
                .thenThrow(new PackageManager.NameNotFoundException());

        NotificationConfig config = new NotificationConfig(mockContext);

        assertEquals(-1, config.getNotificationColor());
    }

    @Test
    public void testMetadataIsCached() throws PackageManager.NameNotFoundException {
        NotificationConfig config1 = new NotificationConfig(mockContext);
        NotificationConfig config2 = new NotificationConfig(mockContext);

        // getApplicationInfo should only be called once due to caching
        verify(mockPackageManager, times(1)).getApplicationInfo(anyString(), anyInt());
    }

    @Test
    public void testHandlesNullMetadata() throws PackageManager.NameNotFoundException {
        mockApplicationInfo.metaData = null;

        when(mockPackageManager.getApplicationInfo(anyString(), anyInt()))
                .thenReturn(mockApplicationInfo);

        NotificationConfig config = new NotificationConfig(mockContext);

        // Should use defaults when metadata is null
        assertEquals("Foreground Service", config.getChannelName());
        assertEquals("Persistent notification for foreground service", config.getChannelDescription());
        assertEquals(-1, config.getNotificationColor());
    }
}
