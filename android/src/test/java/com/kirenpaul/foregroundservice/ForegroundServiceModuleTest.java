package com.kirenpaul.foregroundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ForegroundServiceModule
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14
public class ForegroundServiceModuleTest {

    @Mock
    private ReactApplicationContext mockReactContext;

    @Mock
    private Promise mockPromise;

    @Mock
    private PermissionChecker mockPermissionChecker;

    private ForegroundServiceModule module;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock ReactApplicationContext
        when(mockReactContext.getApplicationContext()).thenReturn(mockReactContext);
        when(mockReactContext.getPackageName()).thenReturn("com.test.app");

        module = new ForegroundServiceModule(mockReactContext);
    }

    @Test
    public void testGetName() {
        assertEquals("ForegroundService", module.getName());
    }

    @Test
    public void testGetConstants() {
        var constants = module.getConstants();

        assertNotNull(constants);
        assertEquals(25, constants.get("MIN_SDK"));
        assertEquals(34, constants.get("TARGET_SDK"));
    }

    @Test
    public void testStartService_withValidConfig() {
        // Create mock config
        WritableMap config = Arguments.createMap();
        config.putInt("id", 1);
        config.putString("title", "Test Service");
        config.putString("message", "Testing");
        config.putString("serviceType", "dataSync");

        // Mock permission checker
        PermissionChecker permissionChecker = mock(PermissionChecker.class);
        when(permissionChecker.hasPostNotificationsPermission()).thenReturn(true);
        when(permissionChecker.hasForegroundServicePermission(anyString())).thenReturn(true);

        // Mock startForegroundService
        ComponentName mockComponentName = new ComponentName(mockReactContext, ForegroundService.class);
        when(mockReactContext.startForegroundService(any(Intent.class))).thenReturn(mockComponentName);
        when(mockReactContext.startService(any(Intent.class))).thenReturn(mockComponentName);

        module.startService((ReadableMap) config, mockPromise);

        // Verify promise resolved
        verify(mockPromise).resolve(null);
        verify(mockPromise, never()).reject(anyString(), anyString());
    }

    @Test
    public void testStartService_withMissingId() {
        WritableMap config = Arguments.createMap();
        config.putString("title", "Test Service");
        config.putString("message", "Testing");

        module.startService((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                contains("id is required")
        );
    }

    @Test
    public void testStartService_withMissingTitle() {
        WritableMap config = Arguments.createMap();
        config.putInt("id", 1);
        config.putString("message", "Testing");

        module.startService((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                contains("title is required")
        );
    }

    @Test
    public void testStartService_withMissingMessage() {
        WritableMap config = Arguments.createMap();
        config.putInt("id", 1);
        config.putString("title", "Test");

        module.startService((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                contains("message is required")
        );
    }

    @Test
    public void testStartService_withNullConfig() {
        module.startService(null, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                contains("config is null")
        );
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void testStartService_requiresServiceTypeOnAndroid14() {
        WritableMap config = Arguments.createMap();
        config.putInt("id", 1);
        config.putString("title", "Test Service");
        config.putString("message", "Testing");
        // Missing serviceType

        module.startService((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                contains("serviceType is required for Android 14+")
        );
    }

    @Test
    public void testStopService() {
        ComponentName mockComponentName = new ComponentName(mockReactContext, ForegroundService.class);
        when(mockReactContext.startService(any(Intent.class))).thenReturn(mockComponentName);

        module.stopService(mockPromise);

        // Verify intent was created with correct action
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockReactContext).startService(intentCaptor.capture());

        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(Constants.ACTION_FOREGROUND_SERVICE_STOP, capturedIntent.getAction());

        // Verify promise resolved
        verify(mockPromise).resolve(null);
    }

    @Test
    public void testStopServiceAll() {
        ComponentName mockComponentName = new ComponentName(mockReactContext, ForegroundService.class);
        when(mockReactContext.startService(any(Intent.class))).thenReturn(mockComponentName);

        module.stopServiceAll(mockPromise);

        // Verify intent was created with correct action
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockReactContext).startService(intentCaptor.capture());

        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(Constants.ACTION_FOREGROUND_SERVICE_STOP_ALL, capturedIntent.getAction());

        // Verify promise resolved
        verify(mockPromise).resolve(null);
    }

    @Test
    public void testUpdateNotification_withValidConfig() {
        WritableMap config = Arguments.createMap();
        config.putInt("id", 1);
        config.putString("title", "Updated Title");
        config.putString("message", "Updated Message");

        ComponentName mockComponentName = new ComponentName(mockReactContext, ForegroundService.class);
        when(mockReactContext.startService(any(Intent.class))).thenReturn(mockComponentName);

        module.updateNotification((ReadableMap) config, mockPromise);

        // Verify intent was created with correct action
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockReactContext).startService(intentCaptor.capture());

        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(Constants.ACTION_UPDATE_NOTIFICATION, capturedIntent.getAction());

        // Verify promise resolved
        verify(mockPromise).resolve(null);
    }

    @Test
    public void testUpdateNotification_withInvalidConfig() {
        WritableMap config = Arguments.createMap();
        // Missing required fields

        module.updateNotification((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                anyString()
        );
    }

    @Test
    public void testIsRunning_whenServiceNotRunning() {
        // Service instance is null
        module.isRunning(mockPromise);

        // Verify promise resolved with 0
        verify(mockPromise).resolve(0);
    }

    @Test
    public void testRunTask_withValidConfig() {
        WritableMap config = Arguments.createMap();
        config.putString("taskName", "myTask");
        config.putInt("delay", 5000);

        ComponentName mockComponentName = new ComponentName(mockReactContext, ForegroundService.class);
        when(mockReactContext.startService(any(Intent.class))).thenReturn(mockComponentName);

        module.runTask((ReadableMap) config, mockPromise);

        // Verify intent was created with correct action
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockReactContext).startService(intentCaptor.capture());

        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(Constants.ACTION_FOREGROUND_RUN_TASK, capturedIntent.getAction());

        // Verify promise resolved
        verify(mockPromise).resolve(null);
    }

    @Test
    public void testRunTask_withMissingTaskName() {
        WritableMap config = Arguments.createMap();
        config.putInt("delay", 5000);
        // Missing taskName

        module.runTask((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                eq("taskName is required")
        );
    }

    @Test
    public void testRunTask_withMissingDelay() {
        WritableMap config = Arguments.createMap();
        config.putString("taskName", "myTask");
        // Missing delay

        module.runTask((ReadableMap) config, mockPromise);

        // Verify promise rejected
        verify(mockPromise).reject(
                eq(Constants.ERROR_INVALID_CONFIG),
                eq("delay is required")
        );
    }

    @Test
    public void testCancelNotification() {
        double notificationId = 1.0;

        // Mock NotificationManager
        when(mockReactContext.getSystemService(anyString()))
                .thenReturn(mock(android.app.NotificationManager.class));

        module.cancelNotification(notificationId, mockPromise);

        // Verify promise resolved
        verify(mockPromise).resolve(null);
    }

    @Test
    public void testCheckPostNotificationsPermission() {
        module.checkPostNotificationsPermission(mockPromise);

        // Verify promise resolved with boolean
        verify(mockPromise).resolve(any(Boolean.class));
    }
}
