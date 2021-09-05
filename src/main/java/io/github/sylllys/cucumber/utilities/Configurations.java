package io.github.sylllys.cucumber.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    if (System.getProperty(HardCodes.SUITE_CONFIG_DETAILS_KEYWORD) != null) {

      ObjectMapper mapper = new ObjectMapper();
      String suiteConfigProperty = System.getProperty(HardCodes.SUITE_CONFIG_DETAILS_KEYWORD);
      suiteConfigProperty = (suiteConfigProperty.startsWith("'") || suiteConfigProperty.startsWith("\"")) ? suiteConfigProperty.substring(1, suiteConfigProperty.length()-1) : suiteConfigProperty;
      suiteConfigProperty = suiteConfigProperty.replaceAll("([\\w]+)[ ]*:[ ]*([\\w]+)", "\"$1\":\"$2\"");
      SuiteConfigDetails[] suiteConfigDetails = mapper.readValue(suiteConfigProperty , SuiteConfigDetails[].class);

      for (SuiteConfigDetails suiteConfigDetail : suiteConfigDetails) {

        loadData(suiteConfigDetail.getDir() + "/", suiteConfigDetail.getEnv());
      }
    }

    dataLoaded = true;
  }

  private static void loadData(String configDir, String envName) throws IOException {

    if (!"none".equalsIgnoreCase(envName)) {

      InputStream env = Configurations.class.getResourceAsStream(
          HardCodes.configurationDirectoryName + configDir + envName
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
