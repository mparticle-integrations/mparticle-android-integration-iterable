package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.iterable.iterableapi.IterableApi;
import com.iterable.iterableapi.IterableConfig;
import com.iterable.iterableapi.IterableConstants;
import com.iterable.iterableapi.IterableFirebaseMessagingService;
import com.iterable.iterableapi.IterableHelper;
import com.mparticle.AttributionResult;
import com.mparticle.MParticle;
import com.mparticle.identity.MParticleUser;
import com.mparticle.kits.iterable.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class IterableKit extends KitIntegration implements KitIntegration.ActivityListener, KitIntegration.ApplicationStateListener, KitIntegration.IdentityListener, KitIntegration.PushListener {
    private Set<String> previousLinks = new HashSet<String>();

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        try {
            checkForAttribution();
            IterableConfig.Builder configBuilder = new IterableConfig.Builder();
            configBuilder.setPushIntegrationName(settings.get("gcmIntegrationName"));
            IterableApi.initialize(context, settings.get("apiKey"), configBuilder.build());
            initIntegrationAttributes();
        } catch(Exception e) {
            Log.e("IterableKit", "Iterable Kit initialization exception", e);
        }
        return null;
    }

    @Override
    public String getName() {
        return "Iterable";
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optedOut) {
        return null;
    }

    private void initIntegrationAttributes() {
        HashMap<String, String> integrationAttributes = new HashMap<>();
        integrationAttributes.put("kitVersionCode", String.valueOf(BuildConfig.VERSION_CODE));
        integrationAttributes.put("sdkVersion", com.iterable.iterableapi.BuildConfig.ITERABLE_SDK_VERSION);
        setIntegrationAttributes(integrationAttributes);
    }

    private void checkForAttribution() {
        WeakReference<Activity> activity = getKitManager().getCurrentActivity();
        if (activity != null && activity.get() != null) {
            String currentLink = activity.get().getIntent().getDataString();
            if (currentLink != null && !currentLink.isEmpty() && !previousLinks.contains(currentLink)) {
                previousLinks.add(currentLink);
                IterableHelper.IterableActionHandler clickCallback = new IterableHelper.IterableActionHandler() {
                    @Override
                    public void execute(String result) {
                        if (!KitUtils.isEmpty(result)) {
                            AttributionResult attributionResult = new AttributionResult().setLink(result);
                            attributionResult.setServiceProviderId(getConfiguration().getKitId());
                            getKitManager().onResult(attributionResult);
                        }
                    }
                };

                IterableApi.getAndTrackDeeplink(currentLink, clickCallback);
            }
        }
    }

    @Override
    public List<ReportingMessage> onActivityCreated(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStarted(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityResumed(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityPaused(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStopped(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityDestroyed(Activity activity) {
        return null;
    }

    @Override
    public void onApplicationForeground() {
        checkForAttribution();
    }

    @Override
    public void onApplicationBackground() {

    }

    @Override
    public void onIdentifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateIdentity(mParticleUser);
    }

    @Override
    public void onLoginCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateIdentity(mParticleUser);
    }

    @Override
    public void onLogoutCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
        updateIdentity(mParticleUser);
    }

    @Override
    public void onModifyCompleted(MParticleUser mParticleUser, FilteredIdentityApiRequest filteredIdentityApiRequest) {
    }

    @Override
    public void onUserIdentified(MParticleUser mParticleUser) {
        updateIdentity(mParticleUser);
    }

    private void updateIdentity(MParticleUser mParticleUser) {
        Map<MParticle.IdentityType, String> userIdentities = mParticleUser.getUserIdentities();
        String email = userIdentities.get(MParticle.IdentityType.Email);
        String userId = userIdentities.get(MParticle.IdentityType.CustomerId);

        if (email != null && !email.isEmpty()) {
            IterableApi.getInstance().setEmail(email);
        }
        else if (userId != null && !userId.isEmpty()) {
            IterableApi.getInstance().setUserId(userId);
        }
        else {
            // No identifier, log out
            IterableApi.getInstance().setEmail(null);
        }
    }

    @Override
    public boolean willHandlePushMessage(Intent intent) {
        Bundle extras = intent.getExtras();
        return extras != null && extras.containsKey(IterableConstants.ITERABLE_DATA_KEY);
    }

    @Override
    public void onPushMessageReceived(Context context, Intent intent) {
        IterableFirebaseMessagingService.handleMessageReceived(context, new RemoteMessage(intent.getExtras()));
    }

    @Override
    public boolean onPushRegistration(String instanceId, String senderId) {
        IterableApi.getInstance().registerForPush();
        return true;
    }
}