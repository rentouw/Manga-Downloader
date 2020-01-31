package com.rentouw;

import java.io.*;
import java.util.Properties;

public class Config {
  public String getPropValues() {
    File configFile = new File("config.properties");
    String rootLocation = "ERROR";
    try {
      FileReader reader = new FileReader(configFile);
      Properties props = new Properties();
      props.load(reader);

      rootLocation = props.getProperty("rootLocation");

      reader.close();
    } catch (FileNotFoundException ex) {
      // file does not exist
    } catch (IOException ex) {
      // I/O error
    }
    return rootLocation;
  }

  public void writeConfig(String value, String name) {
    File configFile = new File("config.properties");

    try {
      Properties props = new Properties();
      props.setProperty(name, value);
      FileWriter writer = new FileWriter(configFile);
      props.store(writer, "root location");
      writer.close();
    } catch (IOException ex) {
      ex.getMessage();
    }
  }
}
