package com.rentouw;

import java.io.*;
import java.util.Properties;

public class Config {
  /**
   * Give back the location of the root folder..
   *
   * @return location of the root folder as string.
   */
  public String getPropValues() {
    File configFile = new File("config.properties");
    String rootLocation = null;
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

  /**
   * Write value to file.
   *
   * @param value String value you want to save in the file.
   * @param name  String name of value you are going to write.
   */
  public void writeConfig(String value, String name) {
    File configFile = new File("config.properties");

    try {
      Properties props = new Properties();
      props.setProperty(name, value);
      FileWriter writer = new FileWriter(configFile);
      // write to the file and set comment on top.
      props.store(writer, "root location");
      writer.close();
    } catch (IOException ex) {
      ex.getMessage();
    }
  }
}
