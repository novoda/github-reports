package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.ReviewComment;
import com.novoda.github.reports.web.hooks.model.GithubAction;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReviewCommentConverterTest {

    private static final long ANY_USER_ID = 88L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final String ANY_REPOSITORY_NAME = "dasRep0";
    private static final boolean ANY_IS_PRIVATE_REPOSITORY = false;

    @InjectMocks
    private ReviewCommentConverter converter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAReviewComment_whenConverting_thenConvertsSuccessfully() throws ConverterException {
        ReviewComment reviewComment = givenAReviewComment();

        Event actual = converter.convertFrom(reviewComment);

        assertThat(actual).isEqualToComparingFieldByField(buildExpectedEvent());
    }

    private Event buildExpectedEvent() {
        return Event.create(ANY_COMMENT_ID,
                            ANY_REPOSITORY_ID,
                            ANY_USER_ID,
                            ANY_USER_ID,
                            EventType.PULL_REQUEST_COMMENT,
                            ANY_DATE);
    }

    private ReviewComment givenAReviewComment() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY);
        return new ReviewComment(githubComment, githubRepository, GithubAction.CREATED);
    }
}
