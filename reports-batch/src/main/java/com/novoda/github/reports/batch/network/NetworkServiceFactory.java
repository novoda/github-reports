package com.novoda.github.reports.batch.network;

interface NetworkServiceFactory {

    GithubApiService createService();

}
