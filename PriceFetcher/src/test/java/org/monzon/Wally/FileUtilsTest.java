package org.monzon.Wally;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.razorvine.pickle.Unpickler;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

    @Test
    public void testUnpickleFile() throws IOException {
        byte[] pickleContent = "test".getBytes();
        InputStream pickleStream = new ByteArrayInputStream(pickleContent);

        Unpickler unpickler =  mock(Unpickler.class);
        when(unpickler.load(any(InputStream.class))).thenReturn("test");

        FileUtils fileUtilsMock = spy(new FileUtils(unpickler));
        doReturn(pickleStream).when(fileUtilsMock).getPickleStream(any(String.class));

        Object result = fileUtilsMock.unpickleFile("test");
        assertNotNull(result);
    }

    @Test
    public void testUnpickleFileExceptionReturnsNull() throws IOException {
        Unpickler unpickler = mock(Unpickler.class);
        when(unpickler.load(any(InputStream.class))).thenThrow(IOException.class);
        FileUtils fileUtilsMock = spy(new FileUtils(unpickler));
        Object result = fileUtilsMock.unpickleFile("test");
        assert(result==null);
    }
}
