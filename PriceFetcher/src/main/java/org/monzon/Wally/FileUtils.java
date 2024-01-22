package org.monzon.Wally;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    protected static Object unpickleFile(String filePath){
        Object unpickledObject;
        String completePath = Config.class.getClassLoader().getResource(filePath).getPath();
        try(InputStream is = new FileInputStream(completePath)){
            net.razorvine.pickle.Unpickler unpickler = new net.razorvine.pickle.Unpickler();
            unpickledObject = unpickler.load(is);
        }catch(FileNotFoundException e){
            logger.log(java.util.logging.Level.SEVERE, "File Not Found", e);
            return null;
        }catch(IOException e){
            logger.log(java.util.logging.Level.SEVERE, "File Not Accessible", e);
            return null;
        }
        return unpickledObject;
    }
}
