package com.novoda.github.reports.service.network;

import okhttp3.OkHttpClient;

interface HttpClientFactory {

    OkHttpClient createClient();

}
