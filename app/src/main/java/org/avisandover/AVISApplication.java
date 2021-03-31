package org.avisandover;

import android.app.Application;

public class AVISApplication extends Application {
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
