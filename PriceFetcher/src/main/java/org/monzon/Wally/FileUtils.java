package org.monzon.Wally;

import net.razorvine.pickle.Unpickler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FileUtils {

    @Autowired
    private Unpickler unpickler;

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public Object unpickleFile() {
        Object unpickledObject;

        try{
            InputStream is = getPickleStream("/upc_price.pickle");
            if (is != null) {
                unpickledObject = unpickler.load(is);
            } else {
                logger.error("File Not Found");
                return null;
            }
        } catch (Exception e){
            logger.error("Error reading file", e);
            return null;
        }

        return unpickledObject;
    }
    public InputStream getPickleStream(String path){
        return FileUtils.class.getResourceAsStream(path);
    }
}
