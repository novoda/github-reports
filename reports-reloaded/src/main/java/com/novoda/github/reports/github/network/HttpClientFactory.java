package com.novoda.github.reports.github.network;

import okhttp3.OkHttpClient;

public interface HttpClientFactory {

    OkHttpClient createClient();

}
