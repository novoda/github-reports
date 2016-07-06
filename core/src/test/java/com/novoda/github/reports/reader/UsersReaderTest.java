package com.novoda.github.reports.reader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class UsersReaderTest {

    @Mock
    private JsonMapReader<Map<String, String>> mockJsonMapReader;

    private UsersReader usersReader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        usersReader = new UsersReader(mockJsonMapReader);
    }

    @Test
    public void givenJsonMapReadReadsContentFromFile_whenReadingUsers_thenTheUsersAreRead() throws Exception {
        Map<String, String> content = new HashMap<>(1);
        content.put("chave", "valor");
        Mockito.when(mockJsonMapReader.readFromResource("users.json")).thenReturn(content);

        usersReader.read();

        assertTrue(usersReader.hasContent());
    }

}
