package com.novoda.floatschedule.convert;

import java.util.function.Supplier;

public class NoMatchFoundException extends RuntimeException {

    static Supplier<NoMatchFoundException> noMatchFoundExceptionFor(String target) {
        return () -> new NoMatchFoundException(target);
    }

    NoMatchFoundException(String target) {
        super("Unable to find a match for \"" + target + "\". Please check the mappings file and/or your query input.");
    }

}
