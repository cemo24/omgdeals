package org.monzon.Wally;

import net.razorvine.pickle.Unpickler;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:test.properties")
public class TaskOkHttpTest {

    @MockBean
    SqsMessenger sqsMessenger;

    @MockBean
    OkHttpClient okHttpClient;

    @MockBean
    Request.Builder requestBuilder;

    @MockBean
    FileUtils utils;

    @MockBean
    Unpickler unpickler;

    @Autowired
    private TaskOkHttp taskOkHttp;

    @Test
    public void testCall() throws Exception {

        when(okHttpClient.newBuilder()).thenReturn(null);

        Path gzipPath = Paths.get("src", "test", "java", "org", "monzon", "Wally", "response_replay.txt").toAbsolutePath();

        byte[] decodedBytes = Base64.getDecoder().decode(Files.readAllBytes(gzipPath));
        byte[] gzipData = compressToGzip(new String(decodedBytes));

        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("http://test.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .addHeader("Content-Type", "gzip")
                .body(ResponseBody.create(MediaType.get("application/json"), gzipData))
                .build();

        Call mockCall = mock(Call.class);

        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        Request req = new Request.Builder()
                .url("http://test.com")
                .build();

        when(requestBuilder.build()).thenReturn(req);

        TaskOkHttp thisTask = taskOkHttp.createTaskOkHttp();
        thisTask.setUpc("");
        thisTask.setListPrice(0.0);
        thisTask.setStore("");
        thisTask.setPx_header("");
        thisTask.setProxy(mock(ProxyCreds.class));
        thisTask.setWm_headers(mock(Map.class));

        Wmdata result = thisTask.call();
        assert(result != null);
    }

    private static byte[] compressToGzip(String input) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            gzipOutputStream.write(input.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
}
