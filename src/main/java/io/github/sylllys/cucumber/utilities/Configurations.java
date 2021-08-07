package io.github.sylllys.cucumber.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * This class contains code to read environment configuration file based on system variable:execEnv,
 * if exists. And also code to read dictionary.properties file, if exists.
 *
 * Also, it contain functions to retrieve the key value pairs stored in the above mentioned files.
 */
public class Configurations {

  static Properties env = new Properties();
  static Properties dict = new Properties();
  static Properties dictBackUp = null;
  private static boolean dataLoaded = false;

  private static void loadData() throws IOException {

    loadData("");

    if (System.getProperty(HardCodes.CONFIG_DIRECTORIES_KEYWORD) != null) {
      for (String configDirs : System.getProperty(HardCodes.CONFIG_DIRECTORIES_KEYWORD)
          .split(",")) {

        loadData(configDirs + "/");
      }
    }

    dictBackUp = (Properties) dict.clone();

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

    InputStream dict = Configurations.class.getResourceAsStream(
        HardCodes.configurationDirectoryName + configDir + HardCodes.dictionaryFileName);

    if (dict != null) {
      Configurations.dict.load(dict);
    }


  }

  public static void loadBackup() throws IOException {
    if (dataLoaded) {
      dict = (Properties) dictBackUp.clone();
    } else {
      loadData();
    }
  }

  public static String getDictionaryVariable(String key) throws IOException {

    return dict.getProperty(key);
  }

  public static Object setDictionaryVariable(String key, String value) throws Exception {

    if (!dict.containsKey(key)) {
      throw new Exception("Cannot update, dictionary does not have key:" + key);
    }

    return dict.setProperty(key, DataMiner.refactorAutoGeneratorExpressions(value));
  }

  public static String getEnvironmentVariable(String key) throws IOException {

    return env.getProperty(key);
  }

  static Properties getEnv() throws IOException {

    return env;
  }

  static Properties getDictionary() throws IOException {

    return dict;
  }
}
