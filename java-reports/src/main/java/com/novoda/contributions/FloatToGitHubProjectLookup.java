package com.novoda.contributions;

import java.util.*;
import java.util.stream.Stream;

public class FloatToGitHubProjectLookup {

    private static final Map<String, List<String>> TABLE = new HashMap<String, List<String>>() {
        {
            put("all4", Collections.singletonList("all-4"));
            put("ccleaner", Collections.singletonList("piriform-ccleaner"));
            put("creators", Collections.singletonList("soundcloud-creators"));
            put("immobilienscout24", Collections.singletonList("TODO EXTERNAL-REPOS"));
            put("oddschecker", Collections.unmodifiableList(Arrays.asList("oddschecker-android", "oddschecker-ios", "oddschecker-apiary")));
            put("sun+", Collections.singletonList("sun-mobile-android"));
            put("the times", Collections.unmodifiableList(Arrays.asList("project-d", "project-d-api")));
            // TODO what about people on INDUCTION
        }
    };

    private final Map<String, List<String>> projectsLookupTable;

    public static FloatToGitHubProjectLookup newInstance() {
        return new FloatToGitHubProjectLookup(TABLE);
    }

    FloatToGitHubProjectLookup(Map<String, List<String>> projectsLookupTable) {
        this.projectsLookupTable = projectsLookupTable;
    }

    public Stream<String> getGitHubProjectsFor(String floatProjectName) {
        String cleanName = cleanName(floatProjectName);

        if (projectsLookupTable.containsKey(cleanName)) {
            return projectsLookupTable.get(cleanName).stream();
        } else {
            return Stream.empty();
        }
    }

    private String cleanName(String input) {
        String cleanUp = input;
        if (input.contains(":")) {
            cleanUp = cleanUp.substring(0, cleanUp.indexOf(":") - 1);
        }
        return cleanUp.toLowerCase();
    }

}
