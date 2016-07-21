package com.novoda.floatschedule.convert;

public class InvalidFloatDateException extends Exception {

    InvalidFloatDateException(String date, Throwable cause) {
        super(String.format("The date \"%s\" is not valid according to the Float format (yyyy-MM-dd).", date), cause);
    }

}
