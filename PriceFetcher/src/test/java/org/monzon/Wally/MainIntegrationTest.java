package org.monzon.Wally;

import net.razorvine.pickle.Unpickler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:test.properties")
public class MainIntegrationTest {

    @MockBean
    private SqsMessenger sqs;

    @MockBean
    private OkHttpClient okHttpClient;

    @MockBean
    private Request.Builder requestBuilder;

    @MockBean
    private TaskOkHttp tasks;

    @MockBean
    private Unpickler unpickler;

    @MockBean
    private FileUtils utils;

    @Autowired
    Runner runner;

    @Test
    public void testRun() throws Exception {
        HashMap<String, Double> mockUpcs = new HashMap<>();
        mockUpcs.put("123", 100.00);
        when(utils.unpickleFile()).thenReturn((Object)mockUpcs);
        when(tasks.call()).thenReturn(new Wmdata("upc_store_retailer", "123", "123", "WM", 1, 19.99, 1.99, 1, "test", "img"));

        runner.run();
        verify(sqs, times(7)).sendBatchMessages(anyList());
    }
}
