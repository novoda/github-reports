package com.novoda.floatschedule.reader;

import com.novoda.floatschedule.convert.JsonMapReader;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UsersReaderTest {

    @Mock
    private JsonMapReader<Map<String, String>> mockJsonMapReader;

    private UsersReader usersReader;

    @Before
    public void setUp() {
        initMocks(this);
        usersReader = new UsersReader(mockJsonMapReader);
    }

    @Test
    public void givenJsonMapReadReadsContentFromFile_whenReadingUsers_thenTheUsersAreRead() throws Exception {
        Map<String, String> content = new HashMap<>(1);
        content.put("chave", "valor");
        when(mockJsonMapReader.readFromResource("users.json")).thenReturn(content);

        usersReader.read();

        assertTrue(usersReader.hasContent());
    }

}
