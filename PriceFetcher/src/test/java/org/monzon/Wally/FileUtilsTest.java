package org.monzon.Wally;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

import net.razorvine.pickle.Unpickler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class FileUtilsTest {

    @MockBean
    Unpickler unpickler;

    @Autowired
    FileUtils utils;

    @Test
    public void testUnpickleFile() throws IOException {
        when(unpickler.load(any(InputStream.class))).thenReturn("test");

        Object result = utils.unpickleFile();
        assertNotNull(result);
    }

    @Test
    public void testUnpickleFileExceptionReturnsNull() throws IOException {
        when(unpickler.load(any(InputStream.class))).thenThrow(IOException.class);
        Object result = utils.unpickleFile();
        assert(result==null);
    }
}
