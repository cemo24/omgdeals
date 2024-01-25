package org.monzon.Wally;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    protected static Object unpickleFile(String filePath) {
        Object unpickledObject;

        try (InputStream is = FileUtils.class.getResourceAsStream(filePath)) {
            if (is != null) {
                net.razorvine.pickle.Unpickler unpickler = new net.razorvine.pickle.Unpickler();
                unpickledObject = unpickler.load(is);
            } else {
                logger.error("File Not Found: {}", filePath);
                return null;
            }
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
            return null;
        }

        return unpickledObject;
    }
}
