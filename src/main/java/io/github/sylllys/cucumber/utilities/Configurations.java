package io.github.sylllys.cucumber.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * This class contains code to read environment configuration file based on system variable:env, if exists
 * And also code to read global.properties file, if exists.
 *
 * Also, it contain functions to retrieve the key value pairs stored in the above mentioned files.
 */
public class Configurations {

  static Properties env = new Properties();
  static Properties globalProperties = new Properties();
  private static boolean dataLoaded = false;

  private static void loadData() throws IOException {

    if (System.getProperty(HardCodes.CONFIG_DIRECTORIES_KEYWORD) != null) {
      for (String configDirs : System.getProperty(HardCodes.CONFIG_DIRECTORIES_KEYWORD)
          .split(",")) {

        loadData(configDirs + "/");
      }
    }

    dataLoaded = true;
  }

  private static void loadData(String configDir) throws IOException {

    if (HardCodes.getExecutionEnvironment() != null
        && !"none".equalsIgnoreCase(HardCodes.getExecutionEnvironment())) {

      InputStream env = Configurations.class.getResourceAsStream(
          HardCodes.configurationDirectoryName + configDir + HardCodes.getExecutionEnvironment()
              + ".properties");
      if (env != null) {
        Configurations.env.load(env);
      }
    }

    InputStream global = Configurations.class.getResourceAsStream(
        HardCodes.configurationDirectoryName + configDir + HardCodes.globalVariablesFileName);

    if (global != null) {
      Configurations.globalProperties.load(global);
    }


  }

  public static void loadBackup() throws IOException {
    if (!dataLoaded) {
      loadData();
    }

    globalProperties.stringPropertyNames()
        .forEach(key -> GlobalVariables.hashmap.put(key, globalProperties.getProperty(key).replaceAll("\\\\", "\\\\\\\\")));

  }

  public static String getEnvironmentVariable(String key) throws IOException {

    return env.getProperty(key);
  }

  static Properties getEnv() throws IOException {

    return env;
  }
}
