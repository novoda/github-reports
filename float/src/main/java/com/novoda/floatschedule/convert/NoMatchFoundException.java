package com.novoda.floatschedule.convert;

public class NoMatchFoundException extends RuntimeException {

    public NoMatchFoundException(String target) {
        super("Unable to find a match for \"" + target + "\". Please check the mappings file and/or your query input.");
    }
}
