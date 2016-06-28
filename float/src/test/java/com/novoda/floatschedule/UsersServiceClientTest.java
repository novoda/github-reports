package com.novoda.floatschedule;

import com.novoda.floatschedule.reader.UsersReader;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UsersServiceClientTest {

    @Mock
    private UsersReader mockUsersReader;

    @InjectMocks
    private UsersServiceClient usersServiceClient;

    @Before
    public void setUp() {
        initMocks(this);
        Map<String, String> content = new HashMap<>(1);
        content.put("chave mestra", "muito valor");
        content.put("chave", "pouco valor");
        when(mockUsersReader.getContent()).thenReturn(content);
    }

    @Test
    public void givenFileWithUsers_whenGettingAllTheGithubUsernames_thenTheyAreEmitted() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        usersServiceClient.getAllGithubUsers()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("pouco valor", "muito valor");
    }
}
