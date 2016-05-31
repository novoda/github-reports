package com.novoda.github.reports.batch.network;

import okhttp3.OkHttpClient;

interface HttpClientFactory {

    OkHttpClient createClient();

}
