package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;

import java.io.OutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutputWriterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private OutputStream mockOutputStream;

    private OutputWriter outputWriter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        outputWriter = new OutputWriter(mockOutputStream, new Gson());
    }

    @Test
    public void whenOutputException_thenTheExceptionIsWrappedAndThrown() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("java.lang.Exception: ooops...");

        outputWriter.outputException(new Exception("ooops..."));
    }

    @Test
    public void whenClose_thenCloseTheOutputStream() throws Exception {

        outputWriter.close();

        verify(mockOutputStream).close();
    }

}
