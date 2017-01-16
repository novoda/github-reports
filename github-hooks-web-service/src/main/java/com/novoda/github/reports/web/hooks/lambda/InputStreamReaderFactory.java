package com.novoda.github.reports.web.hooks.lambda;

import java.io.InputStream;
import java.io.InputStreamReader;

class InputStreamReaderFactory {

    InputStreamReader createFor(InputStream inputStream) {
        return new InputStreamReader(inputStream);
    }

}
