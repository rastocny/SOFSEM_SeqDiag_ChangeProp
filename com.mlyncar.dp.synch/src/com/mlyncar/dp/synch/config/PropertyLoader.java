package com.mlyncar.dp.synch.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.synch.exception.ConfigurationException;

public class PropertyLoader {

    private Properties properties;
    private final static String CONFIG_PATH_PROPERTY_NAME = "resources/synchronization.properties";
    private static PropertyLoader instance;
    private final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);
    
    public static PropertyLoader getInstance() throws ConfigurationException {
        if (instance == null) {
            instance = new PropertyLoader();
        }
        return instance;
    }

    private PropertyLoader() throws ConfigurationException {

        try {
        	URL url = new URL("platform:/config/com.mlyncar.dp.synch/synchronization.properties");
        	InputStream configFileStream = url.openConnection().getInputStream();
        	if(configFileStream==null) {
        		logger.debug("Unable to load config from eclipse platfrom, using classpath configuration");
                configFileStream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH_PROPERTY_NAME);
        	}
            this.properties = new Properties();
            properties.load(configFileStream);
        } catch(FileNotFoundException ex) {
            this.properties = new Properties();
            InputStream configFileStream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH_PROPERTY_NAME);
            try {
                properties.load(configFileStream);
            } catch(IOException exx) {
            	exx.printStackTrace();
                throw new ConfigurationException("Unable to load configuration file: " + ex.getMessage(), ex);
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
            throw new ConfigurationException("Unable to load configuration file: " + ex.getMessage(), ex);
        }
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
}
