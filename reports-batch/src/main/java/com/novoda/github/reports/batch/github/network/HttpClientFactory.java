package com.novoda.github.reports.batch.github.network;

import okhttp3.OkHttpClient;

interface HttpClientFactory {

    OkHttpClient createClient();

}
