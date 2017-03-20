package com.mlyncar.dp.synch.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mlyncar.dp.synch.exception.ConfigurationException;

public class PropertyLoader {

    private Properties properties;
    private final static String CONFIG_PATH_PROPERTY_NAME = "resources/synchronization.properties";
    private static PropertyLoader instance;
    
    public static PropertyLoader getInstance() throws ConfigurationException {
    	if(instance == null) {
    		instance = new PropertyLoader();
    	}
    	return instance;
    }
    
    private PropertyLoader() throws ConfigurationException {
        this.properties = new Properties();
        InputStream configFileStream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH_PROPERTY_NAME);
        try {
            properties.load(configFileStream);
        } catch (IOException ex) {
            throw new ConfigurationException("Unable to load configuration file: " + ex.getMessage(), ex);
        }
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
}
