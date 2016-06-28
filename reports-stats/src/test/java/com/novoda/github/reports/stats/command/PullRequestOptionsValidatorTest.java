package com.novoda.github.reports.stats.command;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class PullRequestOptionsValidatorTest {

    private static final List<String> NULL_PROJECTS = null;
    private static final List<String> EMPTY_PROJECTS = Collections.emptyList();
    private static final List<String> ANY_VALID_PROJECTS = Collections.singletonList("pt");
    private static final List<String> NULL_REPOSITORIES = null;
    private static final List<String> EMPTY_REPOSITORIES = Collections.emptyList();
    private static final List<String> ANY_VALID_REPOSITORIES = Collections.singletonList("github-reports");
    private static final List<String> ANY_TEAM_USERS = Collections.emptyList();
    private static final List<String> ANY_PROJECT_USERS = Collections.emptyList();
    private static final List<String> ANY_USERS = Collections.emptyList();
    private static final PullRequestOptionsGroupBy ANY_GROUP_BY = PullRequestOptionsGroupBy.NONE;
    private static final boolean ANY_WITH_AVERAGE = true;

    private PullRequestOptionsValidator validator;

    @Before
    public void setUp() {
        validator = new PullRequestOptionsValidator();
    }

    @Test
    public void givenOptionsWithProjectsAndNullRepositories_whenValidate_thenValidate() {
        PullRequestOptions options = new PullRequestOptions(
                ANY_VALID_PROJECTS,
                NULL_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertTrue(actual);
    }

    @Test
    public void givenOptionsWithProjectsAndEmptyRepositories_whenValidate_thenValidate() {
        PullRequestOptions options = new PullRequestOptions(
                ANY_VALID_PROJECTS,
                EMPTY_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertTrue(actual);
    }

    @Test
    public void givenOptionsWithRepositoriesAndNullProjects_whenValidate_thenValidate() {
        PullRequestOptions options = new PullRequestOptions(
                NULL_PROJECTS,
                ANY_VALID_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertTrue(actual);
    }

    @Test
    public void givenOptionsWithRepositoriesAndEmptyProjects_whenValidate_thenValidate() {
        PullRequestOptions options = new PullRequestOptions(
                EMPTY_PROJECTS,
                ANY_VALID_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertTrue(actual);
    }

    @Test
    public void givenOptionsWithRepositoriesAndProjects_whenValidate_thenDoNotValidate() {
        PullRequestOptions options = new PullRequestOptions(
                ANY_VALID_PROJECTS,
                ANY_VALID_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertFalse(actual);
    }

    @Test
    public void givenOptionsWithEmptyRepositoriesAndEmptyProjects_whenValidate_thenValidate() {
        PullRequestOptions options = new PullRequestOptions(
                EMPTY_PROJECTS,
                EMPTY_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertTrue(actual);
    }

    @Test
    public void givenOptionsWithNullRepositoriesAndNullProjects_whenValidate_thenValidate() {
        PullRequestOptions options = new PullRequestOptions(
                NULL_PROJECTS,
                NULL_REPOSITORIES,
                ANY_TEAM_USERS,
                ANY_PROJECT_USERS,
                ANY_USERS,
                ANY_GROUP_BY,
                ANY_WITH_AVERAGE
        );

        boolean actual = validator.validate(options);

        assertTrue(actual);
    }
}
