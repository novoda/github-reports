package com.novoda.github.reports.sheets.convert;

import com.novoda.github.reports.sheets.sheet.Entry;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class GithubUsernameRemoverTest {

    private GithubUsernameRemover githubUsernameRemover;

    @Before
    public void setUp() {
        githubUsernameRemover = new GithubUsernameRemover();
    }

    @Test
    public void givenAnEntryWithTheGithubKey_whenRemoving_thenItGetsRemoved()  {
        Entry entry = new Entry("float name", "githubusername: richardmstallman");

        Entry actual = githubUsernameRemover.removeFrom(entry);

        assertThat(actual.getContent(), IsEqualIgnoringCase.equalToIgnoringCase("richardmstallman"));
    }

    @Test
    public void givenAnEntryWithoutTheGithubKey_whenRemoving_thenItDoesNotGetRemoved() {
        Entry entry = new Entry("float name", "gitlabname: richardmstallman");

        Entry actual = githubUsernameRemover.removeFrom(entry);

        assertThat(actual.getContent(), IsEqualIgnoringCase.equalToIgnoringCase("gitlabname: richardmstallman"));
    }
}
