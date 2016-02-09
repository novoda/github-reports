package com.novoda.reports.organisation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RepoInMemoryDataSource {
    private static final Map<String, List<OrganisationRepo>> CACHE = new HashMap<>();

    public void createRepositories(String organisation, List<OrganisationRepo> repositories) {
        CACHE.put(organisation, repositories);
    }

    public List<OrganisationRepo> readRepositories(String organisation) {
        if (CACHE.containsKey(organisation)) {
            return CACHE.get(organisation);
        } else {
            return Collections.emptyList();
        }
    }
}
