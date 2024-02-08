package org.monzon.Wally;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:test.properties")
public class MainIntegrationTest {

    @MockBean
    SqsMessenger sqs;

    @MockBean
    OkHttpClient okHttpClient;

    @MockBean
    Request.Builder requestBuilder;

    @MockBean
    TaskOkHttp taskOkHttp;

    @Test
    public void testRun() throws Exception {

        when(taskOkHttp.createTaskOkHttp()).thenReturn(taskOkHttp);
        when(taskOkHttp.call()).thenReturn(new Wmdata("upc_store_retailer", "123", "123", "WM", 1, 19.99, 1.99, 1));

        Main main = new Main(taskOkHttp, sqs);
        main.run();

        verify(sqs, times(1)).sendBatchMessages(any(ArrayList.class));

    }
}
