package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.web.hooks.model.CommitComment;
import com.novoda.github.reports.web.hooks.model.GithubAction;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class CommitCommentCoverterUnsupportedActionTest {

    @Parameters(name = "{index}: unsupported action={0}")
    public static Collection<Object> data() {
        return Arrays.asList(new Object[]{
                GithubAction.EDITED,
                GithubAction.ADDED,
                GithubAction.ASSIGNED,
                GithubAction.OPENED,
                GithubAction.PUBLISHED,
                GithubAction.REOPENED,
                GithubAction.SYNCHRONIZE,
                GithubAction.DELETED,
                GithubAction.UNASSIGNED
        });
    }

    @Mock
    private CommitComment mockCommitComment;

    @Parameterized.Parameter(0)
    public GithubAction unsupportedAction;

    @InjectMocks
    private CommitCommentConverter converter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test(expected = ConverterException.class)
    public void givenACommitCommentWithAnUnsupportedAction_whenConverting_thenThrowsException() throws ConverterException {
        given(mockCommitComment.getAction()).willReturn(unsupportedAction);

        converter.convertFrom(mockCommitComment);
    }
}
