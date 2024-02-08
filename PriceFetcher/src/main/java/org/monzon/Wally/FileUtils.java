package org.monzon.Wally;

import net.razorvine.pickle.Unpickler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private  Unpickler unpickler;

    public FileUtils(Unpickler unpickler){
        this.unpickler = unpickler;
    }

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    protected Object unpickleFile(String filePath) {
        Object unpickledObject;

        try{
            InputStream is = getPickleStream("/upc_price.pickle");
            if (is != null) {
//                Unpickler unpickler = new net.razorvine.pickle.Unpickler();
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
    protected InputStream getPickleStream(String path){
        return FileUtils.class.getResourceAsStream(path);
    }
}
