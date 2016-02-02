package com.novoda.contributions;

import java.util.Map;
import java.util.TreeMap;

public class FloatToGitHubUsername {

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

    private final Map<String, String> usernamesLookupTable;

    public static FloatToGitHubUsername newInstance() {
        return new FloatToGitHubUsername(NOVODA_DEVS_LOOKUP_TABLE);
    }

    FloatToGitHubUsername(Map<String, String> usernamesLookupTable) {
        this.usernamesLookupTable = usernamesLookupTable;
    }

    public String lookup(String floatUsername) throws IllegalArgumentException {
        if (usernamesLookupTable.containsKey(floatUsername)) {
            return usernamesLookupTable.get(floatUsername);
        } else {
            throw new IllegalArgumentException("No developer found for " + floatUsername);
        }
    }

}
