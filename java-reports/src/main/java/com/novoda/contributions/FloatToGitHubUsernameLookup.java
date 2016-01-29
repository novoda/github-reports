package com.novoda.contributions;

import java.util.*;

public class FloatToGitHubUsernameLookup {

    private static final Map<String, String> NOVODA_DEVS_LOOKUP_TABLE = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {
        {
            put("adam brown", "ouchadam");
            put("alex curran", "amlcurran");
            put("alexandros stylianidis", "alexstyl");
            put("andrei catinean", "electryc");
            put("antonio bertucci", "mr-archano");
            put("ataul munim", "ataulm");
            put("benjamin augustin", "dorvaryn");
            put("daniele bonaldo", "danybony");
            put("daniele conti", "fourlastor");
            put("dirk jäckel", "biafra23");
            put("dominic freeston", "dominicfreeston");
            put("ferran garriga ollé", "zegnus");
            put("florian mierzejewski", "florianmski");
            put("juanky soriano", "juankysoriano");
            put("neil hutchinson", "hutch4");
            put("paul blundell", "blundell");
            put("rui teixeira", "takecare");
            put("ryan feline", "mecharyry");
            put("sebastiano poggi", "rock3r");
            put("volker leck", "devisnik");
            put("wagner truppel", "witrup");
            put("xavi rigau", "xrigau");
        }
    };

    private final Map<String, String> lookupTable;

    private static FloatToGitHubUsernameLookup newInstance() {
        return new FloatToGitHubUsernameLookup(NOVODA_DEVS_LOOKUP_TABLE);
    }

    FloatToGitHubUsernameLookup(Map<String, String> lookupTable) {
        this.lookupTable = lookupTable;
    }

    public String getGitHubUsernameFor(String floatProjectName) throws IllegalArgumentException {
        if (lookupTable.containsKey(floatProjectName)) {
            return lookupTable.get(floatProjectName);
        } else {
            throw new IllegalArgumentException("No developer found for " + floatProjectName);
        }
    }

}
