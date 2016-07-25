package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class WebhookEventClassifierTest {

    private static final EventType ANY_EVENT_TYPE = EventType.COMMIT_COMMENT;

    @Mock
    private ClassificationRule mockRule;

    @Mock
    private GithubWebhookEvent mockEvent;

    private WebhookEventClassifier eventClassifier;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        eventClassifier = new WebhookEventClassifier(Collections.singletonMap(ANY_EVENT_TYPE, mockRule));
    }

    @Test(expected = ClassificationException.class)
    public void givenAnNonClassifiableEvent_whenClassifying_thenThrowsException() throws ClassificationException {
        when(mockRule.check(mockEvent)).thenReturn(false);

        eventClassifier.classify(mockEvent);
    }

    @Test
    public void givenAnEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        when(mockRule.check(mockEvent)).thenReturn(true);

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(ANY_EVENT_TYPE, actual);
    }
}
