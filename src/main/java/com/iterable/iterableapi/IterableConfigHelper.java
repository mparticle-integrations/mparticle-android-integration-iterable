package com.iterable.iterableapi;

public class IterableConfigHelper {
    public static IterableConfig.Builder createConfigBuilderFromIterableConfig(IterableConfig config) {
        IterableConfig.Builder builder = new IterableConfig.Builder();
        if (config != null) {
            builder.setPushIntegrationName(config.pushIntegrationName);
            builder.setUrlHandler(config.urlHandler);
            builder.setCustomActionHandler(config.customActionHandler);
            builder.setAutoPushRegistration(config.autoPushRegistration);
            builder.setCheckForDeferredDeeplink(config.checkForDeferredDeeplink);
            builder.setLogLevel(config.logLevel);
            builder.setInAppHandler(config.inAppHandler);
            builder.setInAppDisplayInterval(config.inAppDisplayInterval);
        }
        return builder;
    }
}
