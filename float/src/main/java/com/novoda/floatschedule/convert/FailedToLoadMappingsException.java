package com.novoda.floatschedule.convert;

import java.io.IOException;

public class FailedToLoadMappingsException extends RuntimeException {

    FailedToLoadMappingsException(IOException cause) {
        super(cause);
    }
}
