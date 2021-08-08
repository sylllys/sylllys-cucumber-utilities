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
  public static final String CONFIG_DIRECTORIES_KEYWORD = "suite.config.dirs";


  public static final String YAML_PATH_KEYWORD = "details ";
  public static final String REQUEST_HEADER_KEYWORD = "header ";
  public static final String REQUEST_PARAMETER_KEYWORD = "param ";
  public static final String REQUEST_BODY_KEYWORD = "body ";
  public static final String REQUEST_BODY_EDIT_KEYWORD = "body.edit ";

  public static String getExecutionEnvironment() {
    return System.getProperty(ENVIRONMENT_KEYWORD);
  }
}
