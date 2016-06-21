package com.novoda.github.reports.floatschedule.network;

public class FloatServiceContainer {

    private static final FloatApiService floatService = FloatServiceFactory.newInstance().createService();

    private FloatServiceContainer() {
        // non instantiable
    }

    public static FloatApiService getFloatService() {
        return floatService;
    }
}
