package com.novoda.github.reports.github.network;

import okhttp3.OkHttpClient;

interface HttpClientFactory {

    OkHttpClient createClient();

}
