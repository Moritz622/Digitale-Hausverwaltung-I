package dev.hausfix.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    public PropertyLoader(){

    }

    public Properties getProperties(String path){
        Properties properties = new Properties();

        try(InputStream input = new FileInputStream(path)){
            properties.load(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

}
