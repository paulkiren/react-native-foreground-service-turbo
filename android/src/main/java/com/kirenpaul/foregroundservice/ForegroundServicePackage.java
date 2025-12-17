package com.kirenpaul.foregroundservice;

import com.facebook.react.TurboReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * React Native package for Foreground Service TurboModule
 *
 * This package registers the ForegroundServiceModule with React Native.
 * Extends TurboReactPackage for TurboModule support.
 */
public class ForegroundServicePackage extends TurboReactPackage {

    @Nullable
    @Override
    public NativeModule getModule(String name, @Nonnull ReactApplicationContext reactContext) {
        if (name.equals(ForegroundServiceModule.NAME)) {
            return new ForegroundServiceModule(reactContext);
        }
        return null;
    }

    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return () -> {
            final Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
            moduleInfos.put(
                ForegroundServiceModule.NAME,
                new ReactModuleInfo(
                    ForegroundServiceModule.NAME,
                    ForegroundServiceModule.class.getName(),
                    false, // canOverrideExistingModule
                    false, // needsEagerInit
                    true,  // hasConstants
                    false, // isCxxModule
                    true   // isTurboModule
                )
            );
            return moduleInfos;
        };
    }
}
