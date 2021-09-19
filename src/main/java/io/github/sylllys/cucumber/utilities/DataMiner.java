package io.github.sylllys.cucumber.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/*
 * This class contains functions to refactor the arguments passed in the steps
 *
 * below are the conventions it supports,
 *
 * {env:key} will be replaced with the value mapped to key from the {environment}.properties file
 * {file:path} will be replaced with the content of the file with the given path placed under ./src/test/resources/configurations folder
 * {global:key} will be replaced with the value mapped to key from the global hashmap
 *
 * and many more data generators
 */
public class DataMiner {


  public static String refactor(String rawData) throws Exception {

    rawData = refactorKeywordExpressions(rawData);
    rawData = refactorSubstituteExpressions(rawData);

    return rawData;

  }

  public static String refactorStaticKeywordExpressions(String rawData) throws Exception {

    if (rawData == null) {
      return null;
    }

    do {
      while (rawData.matches("(?s).*\\{" + HardCodes.FILE_KEYWORD + ":.*\\}.*")) {
        int startIndex = rawData.indexOf("{" + HardCodes.FILE_KEYWORD + ":")
            + new String("{" + HardCodes.FILE_KEYWORD + ":").length();
        int endIndex = rawData.indexOf("}", startIndex);
        String fileName = rawData.substring(startIndex, endIndex);

        InputStream in = DataMiner.class
            .getResourceAsStream(HardCodes.configurationDirectoryName + fileName);
        if (in != null) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));

          byte[] replacement = org.apache.commons.io.IOUtils.toString(reader).getBytes();

          rawData = rawData.replace("{" + HardCodes.FILE_KEYWORD + ":" + fileName + "}",
              new String(replacement));
        } else {
          throw new Exception(
              "File variable(s) not found:"
                  + fileName);
        }
      }

      for (int i = 0; i < 5; i++) {
        for (Object key : Configurations.getEnv().keySet()) {
          rawData = rawData.replace("{" + HardCodes.ENVIRONMENT_KEYWORD + ":" + key + "}",
              Configurations.getEnvironmentVariable(key.toString()).replaceAll("\\\\", "\\\\\\\\"));
        }
      }

      if (rawData.matches("(?s).*\\{" + HardCodes.ENVIRONMENT_KEYWORD + ":.*\\}.*")) {
        throw new Exception(
            "Suite environment variable(s) did not refactored as expected. Here is the erroneous data:"
                + rawData);
      }

      String sysKeyWithColon = HardCodes.SYSPROP_KEYWORD + ":";
      while (rawData.matches("(?s).*\\{" + sysKeyWithColon + ".*\\}.*")) {
        int startIndex =
            rawData.indexOf("{" + sysKeyWithColon) + new String("{" + sysKeyWithColon).length();
        int endIndex = rawData.indexOf('}', startIndex);
        String sysKeyName = rawData.substring(startIndex, endIndex);
        String sysval = System.getProperty(sysKeyName);

        if (sysval == null) {
          throw new Exception("No System property with key:" + sysKeyName);
        } else {
          rawData = rawData.replace("{" + sysKeyWithColon + sysKeyName + "}",
              sysval.replaceAll("\\\\", "\\\\\\\\"));
        }
      }

      String sysEnvVKeyWithColon = HardCodes.SYSENVV_KEYWORD + ":";
      while (rawData.matches("(?s).*\\{" + sysEnvVKeyWithColon + ".*\\}.*")) {
        int startIndex = rawData.indexOf("{" + sysEnvVKeyWithColon)
            + new String("{" + sysEnvVKeyWithColon).length();
        int endIndex = rawData.indexOf('}', startIndex);
        String sysKeyName = rawData.substring(startIndex, endIndex);
        String sysval = System.getenv(sysKeyName);

        if (sysval == null) {
          throw new Exception("No System environment variable with key:" + sysKeyName);
        } else {
          rawData = rawData.replace("{" + sysEnvVKeyWithColon + sysKeyName + "}",
              sysval.replaceAll("\\\\", "\\\\\\\\"));
        }
      }
    } while (!isStaticDataRefactored(rawData));

    return rawData;
  }

  public static String refactorGlobalKeywordExpressions(String rawData) throws Exception {

    if (rawData == null) {
      return null;
    }

    do {
      for (int i = 0; i < 5; i++) {
        for (String key : GlobalVariables.hashmap.keySet()) {
          String glbl = GlobalVariables.get(key);
          rawData = rawData.replace("{" + HardCodes.GLOBAL_KEYWORD + ":" + key + "}", glbl);
        }
      }

      if (rawData.matches("(?s).*\\{" + HardCodes.GLOBAL_KEYWORD + ":.*\\}.*")) {
        throw new Exception(
            "Global variable(s) did not refactored as expected. Here is the erroneous data:"
                + rawData);
      }
    } while (!isDynamicDataRefactored(rawData));

    return rawData;
  }

  public static String refactorKeywordExpressions(String rawData) throws Exception {

    if (rawData == null) {
      return null;
    }

    do {
      rawData = refactorStaticKeywordExpressions(rawData);
      rawData = refactorGlobalKeywordExpressions(rawData);
      rawData = refactorAutoGeneratorExpressions(rawData);

    } while (!isDataRefactored(rawData));
    return rawData;
  }

  public static String refactorSubstituteExpressions(String rawData) throws IOException {

    if (rawData == null) {
      return null;
    }

    if (rawData.equalsIgnoreCase("{null}")) {
      return null;
    }

    rawData = rawData.replace("{NEW_LINE}", System.getProperty("line.separator"));
    rawData = rawData.replace("{LINE_FEED}", "\n");
    rawData = rawData.replace("{CARRIAGE_RETURN}", "\r");
    rawData = rawData.replace("\"{NULL}\"", "set-me-null");
    rawData = rawData.replace("{NULL}", "set-me-null");
    rawData = rawData.replace("\"{null}\"", "set-me-null");
    rawData = rawData.replace("{null}", "set-me-null");
    rawData = rawData.replace("\"set-me-null\"", "null");
    rawData = rawData.replace("set-me-null", "null");
    rawData = rawData.replace("{.}", new java.io.File(".").getCanonicalPath());
    rawData = rawData.replace("{..}", new java.io.File("..").getCanonicalPath());
    rawData = rawData.replace("{EMPTY}", "");
    rawData = rawData.replace("{SPACE}", " ");

    while (rawData.matches("(?s).*\\{SPACE_.*\\}.*")) {
      int startIndex = rawData.indexOf("{SPACE_") + "{SPACE_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String gen = rawData.substring(startIndex, endIndex);

      int genLength = Integer.parseInt(gen.substring(0, gen.length()));
      rawData = rawData.replaceFirst("\\{SPACE_" + gen + "\\}",
          StringUtils.repeat(" ", genLength));
    }

    return rawData;
  }

  public static String refactorAutoGeneratorExpressions(String rawData) throws Exception {

    String autoGenColon = HardCodes.AUTO_GENERATOR_KEYWORD + ":";
    while (rawData.matches("(?s).*\\{" + autoGenColon + ".*\\}.*")) {
      int startIndex = rawData.indexOf("{" + autoGenColon)
          + new String("{" + autoGenColon).length();
      int endIndex = rawData.indexOf('}', startIndex);
      String autoGenExpressions = rawData.substring(startIndex, endIndex);

      String replacement = null;

      for (String autoGenExpression : autoGenExpressions.split("->")) {
        String[] autoGenDetails = autoGenExpression.split("_", 2);
        switch (autoGenDetails[0].toLowerCase()) {
          case "text":
            replacement = Generator.generateRandomString(Integer.parseInt(autoGenDetails[1]));
            break;
          case "number":
            if (autoGenDetails[1].contains(":")) {

              String[] infoMaxMin = autoGenDetails[1].split(":", 2);
              int min = Integer.parseInt(infoMaxMin[0]);
              int max = Integer.parseInt(infoMaxMin[1]);

              replacement = Generator.generateRandomInteger(max, min);
            } else {

              replacement = Generator.generateRandomInteger(Integer.parseInt(autoGenDetails[1]));
            }
            break;
          case "timestamp":
            replacement = Generator.generateTimeStamp(autoGenDetails[1]);
            break;
          case "uppercase":
            replacement = replacement.toUpperCase();
            break;
          case "lowercase":
            replacement = replacement.toLowerCase();
            break;
          case "encrypt-base64":
            replacement = new String(Base64.encodeBase64(replacement.getBytes()));
            break;
          case "decrypt-base64":
            replacement = new String(Base64.decodeBase64(replacement.getBytes()));
            break;
          case "encrypt-sha512hex":
            replacement = DigestUtils.sha512Hex(replacement.getBytes());
            break;
          default:
            replacement = autoGenExpression;
        }
      }

      rawData =
          rawData.replace("{" + autoGenColon + autoGenExpressions + "}", replacement);
    }

    return rawData;
  }

  static boolean isDataRefactored(String data) {

    return data == null ? true
        : isStaticDataRefactored(data) && isDynamicDataRefactored(data);

  }

  static boolean isStaticDataRefactored(String data) {

    return data == null ? true
        : !data.matches("(?s).*\\{" + HardCodes.ENVIRONMENT_KEYWORD + ":.*\\}.*|(?s).*\\{"
            + HardCodes.FILE_KEYWORD + ":.*\\}.*|(?s).*\\{" + HardCodes.SYSPROP_KEYWORD
            + ":.*\\}.*|(?s).*\\{"
            + HardCodes.SYSENVV_KEYWORD + ":.*\\}.*");

  }

  static boolean isDynamicDataRefactored(String data) {

    return data == null ? true
        : !data.matches("(?s).*\\{"
            + HardCodes.GLOBAL_KEYWORD + ":.*\\}.*");

  }
}
