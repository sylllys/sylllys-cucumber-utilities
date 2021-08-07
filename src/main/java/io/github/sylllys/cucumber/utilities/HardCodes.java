package io.github.sylllys.cucumber.utilities;

/*
 * This class contains all hard code values that are used across the project
 */
public class HardCodes {

  public static final String configurationDirectoryName = "/configurations/";
  public static final String configurationFilesDirectory =
      "./src/test/resources" + configurationDirectoryName;
  public static final String dictionaryFileName = "dictionary.properties";

  public static final String GLOBAL_KEYWORD = "global";
  public static final String ENVIRONMENT_KEYWORD = "env";
  public static final String FILE_KEYWORD = "file";
  public static final String DICTIONARY_KEYWORD = "dict";
  public static final String SYSPROP_KEYWORD = "sysprop";
  public static final String SYSENVV_KEYWORD = "sysenvv";
  public static final String CONFIG_DIRECTORIES_KEYWORD = "sylllys.config.directories";

  public static String getExecutionEnvironment() {
    return System.getProperty("env");
  }
}
