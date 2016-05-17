package com.novoda.github.reports.github;

import okhttp3.OkHttpClient;

public interface HttpClientFactory {

    OkHttpClient createClient();

}
