package org.monzon.Wally;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class SqsMessengerTest {

    @Test
    public void testConvertObjectToMessage(){

        SqsMessenger sqs = mock(SqsMessenger.class);
        Wmdata mockObject = mock(Wmdata.class);
        var mockedObjects = new ArrayList<Wmdata>();
        mockedObjects.add(mockObject);

        when(sqs.sendMessage(any(String.class), any(String.class))).thenReturn(true);
        sqs.sendBatchMessages(mockedObjects);

        verify(sqs, times(1)).sendBatchMessages(mockedObjects);
    }
}
