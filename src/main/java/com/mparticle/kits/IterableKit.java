package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mparticle.AttributionResult;
import com.mparticle.MParticle;
import com.mparticle.activity.MPActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class IterableKit extends KitIntegration implements KitIntegration.ActivityListener, KitIntegration.ApplicationStateListener {


    private String attributionUrl;
    private Set<String> previousLinks = new HashSet<String>();

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        checkForAttribution();
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

    private void checkForAttribution() {
        Activity activity = MParticle.getInstance().getAppStateManager().getCurrentActivity().get();
        if (activity != null) {
            String currentLink = activity.getIntent().getDataString();
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
}