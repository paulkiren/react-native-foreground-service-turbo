package com.kirenpaul.foregroundservice;

import android.content.pm.ServiceInfo;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Unit tests for ServiceTypeManager
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
public class ServiceTypeManagerTest {

    @Test
    public void testGetServiceTypeFlag_dataSync() {
        int flag = ServiceTypeManager.getServiceTypeFlag("dataSync");

        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC, flag);
    }

    @Test
    public void testGetServiceTypeFlag_location() {
        int flag = ServiceTypeManager.getServiceTypeFlag("location");

        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION, flag);
    }

    @Test
    public void testGetServiceTypeFlag_mediaPlayback() {
        int flag = ServiceTypeManager.getServiceTypeFlag("mediaPlayback");

        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK, flag);
    }

    @Test
    public void testGetServiceTypeFlag_null_defaultsToDataSync() {
        int flag = ServiceTypeManager.getServiceTypeFlag(null);

        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC, flag);
    }

    @Test
    public void testGetServiceTypeFlag_unknown_defaultsToDataSync() {
        int flag = ServiceTypeManager.getServiceTypeFlag("unknownType");

        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC, flag);
    }

    @Test
    public void testGetRequiredPermission_dataSync() {
        String permission = ServiceTypeManager.getRequiredPermission("dataSync");

        assertEquals("android.permission.FOREGROUND_SERVICE_DATA_SYNC", permission);
    }

    @Test
    public void testGetRequiredPermission_location() {
        String permission = ServiceTypeManager.getRequiredPermission("location");

        assertEquals("android.permission.FOREGROUND_SERVICE_LOCATION", permission);
    }

    @Test
    public void testGetRequiredPermission_mediaPlayback() {
        String permission = ServiceTypeManager.getRequiredPermission("mediaPlayback");

        assertEquals("android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK", permission);
    }

    @Test
    public void testGetRequiredPermission_null_defaultsToDataSync() {
        String permission = ServiceTypeManager.getRequiredPermission(null);

        assertEquals("android.permission.FOREGROUND_SERVICE_DATA_SYNC", permission);
    }

    @Test
    public void testGetRequiredPermission_unknown_defaultsToDataSync() {
        String permission = ServiceTypeManager.getRequiredPermission("unknownType");

        assertEquals("android.permission.FOREGROUND_SERVICE_DATA_SYNC", permission);
    }

    @Test
    public void testRequiresAdditionalPermissions_dataSync_returnsFalse() {
        boolean requires = ServiceTypeManager.requiresAdditionalPermissions("dataSync");

        assertFalse(requires);
    }

    @Test
    public void testRequiresAdditionalPermissions_location_returnsTrue() {
        boolean requires = ServiceTypeManager.requiresAdditionalPermissions("location");

        assertTrue(requires);
    }

    @Test
    public void testRequiresAdditionalPermissions_mediaPlayback_returnsTrue() {
        boolean requires = ServiceTypeManager.requiresAdditionalPermissions("mediaPlayback");

        assertTrue(requires);
    }

    @Test
    public void testRequiresAdditionalPermissions_null_returnsFalse() {
        boolean requires = ServiceTypeManager.requiresAdditionalPermissions(null);

        assertFalse(requires);
    }

    @Test
    public void testRequiresAdditionalPermissions_unknown_returnsFalse() {
        boolean requires = ServiceTypeManager.requiresAdditionalPermissions("unknownType");

        assertFalse(requires);
    }

    @Test
    public void testTypeDataSync_constant() {
        assertEquals("dataSync", ServiceTypeManager.TYPE_DATA_SYNC);
    }

    @Test
    public void testTypeLocation_constant() {
        assertEquals("location", ServiceTypeManager.TYPE_LOCATION);
    }

    @Test
    public void testTypeMediaPlayback_constant() {
        assertEquals("mediaPlayback", ServiceTypeManager.TYPE_MEDIA_PLAYBACK);
    }
}
