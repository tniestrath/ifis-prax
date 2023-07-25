package com.analysetool.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
        private static final String CONFIG_FILE_PATH = "config.properties";
        private static final ConfigReader instance = new ConfigReader();

        private Properties properties;

        private ConfigReader() {
            properties = new Properties();
            try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static ConfigReader getInstance() {
            return instance;
        }

        public String getProperty(String key) {
            return properties.getProperty(key);
        }
    }
