package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
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
    public void givenANonClassifiableEvent_whenClassifying_thenThrowsException() throws ClassificationException {
        given(mockRule.check(mockEvent)).willReturn(false);

        eventClassifier.classify(mockEvent);
    }

    @Test
    public void givenAnEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        given(mockRule.check(mockEvent)).willReturn(true);

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(ANY_EVENT_TYPE, actual);
    }

    @Test(expected = ClassificationException.class)
    public void givenNoRules_whenClassifyingAnEvent_thenThrowsException() throws Exception {
        eventClassifier = new WebhookEventClassifier(Collections.emptyMap());

        eventClassifier.classify(mockEvent);
    }
}
