package com.novoda.github.reports.network;

import okhttp3.OkHttpClient;

public interface HttpClientFactory {

    OkHttpClient createClient();

}
