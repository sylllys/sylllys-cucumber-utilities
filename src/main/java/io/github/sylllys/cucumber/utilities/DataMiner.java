package io.github.sylllys.cucumber.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/*
 * This class contains functions to refactor the arguments passed in the steps
 *
 * below are the conventions it supports,
 *
 * {env:key} will be replaced with the value mapped to key from the {environment}.properties file
 * {dict:key} will be replaced with the value mapped to key from the dictionary.properties file
 * {file:path} will be replaced with the content of the file with the given path placed under ./src/test/resources/configurations folder
 * {global:key} will be replaced with the value mapped to key from the global hashmap
 *
 * and many more data generators, {NEW_LINE} will be replaced with a new line separator {NULL} will
 * be replaced with null {GENERATE_TXT#} will be replaced with a random text of length #
 * {GENERATE_NUM#} will be replaced with a random number of length # {TIMESTAMP_format} will be
 * replaced with the current time stamp in the requested format {CURRENTDATE_format_MORE#} will be
 * replaced with the current time stamp after adding # number of days, in the requested format
 * {CURRENTDATE_format_LESS#} will be replaced with the current time stamp after subtracting #
 * number of days, in the requested format
 */
public class DataMiner {

  public static Object retrieveGlobalVariable(String str) {

    Pattern pattern = Pattern.compile("\\{" + HardCodes.GLOBAL_KEYWORD + ":(.*?)\\}");
    Matcher matcher = pattern.matcher(str);
    if (matcher.find()) {
      return GlobalVariables.forceGet(matcher.group(1));
    }

    return null;
  }


  public static String refactor(String rawData) throws Exception {

    rawData = refactorKeywordExpressions(rawData);
    rawData = refactorSubstituteExpressions(rawData);

    if (rawData != null) {
      rawData = refactorAutoGeneratorExpressions(rawData);
    }

    return rawData;

  }

  public static String refactorKeywordExpressions(String rawData) throws Exception {

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
              Configurations.getEnvironmentVariable(key.toString()));
        }
      }

      if (rawData.matches("(?s).*\\{" + HardCodes.ENVIRONMENT_KEYWORD + ":.*\\}.*")) {
        throw new Exception(
            "Environment variable(s) did not refactored as expected. Here is the erroneous data:"
                + rawData);
      }

      for (int i = 0; i < 5; i++) {
        for (Object key : Configurations.getDictionary().keySet()) {
          rawData = rawData.replace("{" + HardCodes.DICTIONARY_KEYWORD + ":" + key + "}",
              Configurations.getDictionaryVariable(key.toString()));
        }
      }

      if (rawData.matches("(?s).*\\{" + HardCodes.DICTIONARY_KEYWORD + ":.*\\}.*")) {
        throw new Exception(
            "Dictionary variable(s) did not refactored as expected. Here is the erroneous data:"
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
          rawData = rawData.replace("{" + sysKeyWithColon + sysKeyName + "}", sysval);
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
          throw new Exception("No System property with key:" + sysKeyName);
        } else {
          rawData = rawData.replace("{" + sysEnvVKeyWithColon + sysKeyName + "}", sysval);
        }
      }

      for (int i = 0; i < 5; i++) {
        for (String key : GlobalVariables.hashmap.keySet()) {
          Object glbl = GlobalVariables.forceGet(key);
          if (glbl == null) {
            rawData = rawData.replace("{" + HardCodes.GLOBAL_KEYWORD + ":" + key + "}", "");
          } else {
            if (glbl.getClass().getName().equals(String.class.getName())) {
              rawData = rawData.replace("{" + HardCodes.GLOBAL_KEYWORD + ":" + key + "}",
                  glbl.toString());
            }
          }
        }
      }

      if (rawData.matches("(?s).*\\{" + HardCodes.GLOBAL_KEYWORD + ":.*\\}.*")) {
        throw new Exception(
            "Global variable(s) did not refactored as expected. Here is the erroneous data:"
                + rawData);
      }
    } while (!DataMiner.isDataRefactored(rawData));

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
    rawData = rawData.replace("\"{NULL}\"", "taf-null");
    rawData = rawData.replace("{NULL}", "taf-null");
    rawData = rawData.replace("\"{null}\"", "taf-null");
    rawData = rawData.replace("{null}", "taf-null");
    rawData = rawData.replace("\"taf-null\"", "null");
    rawData = rawData.replace("taf-null", "null");
    rawData = rawData.replace("{.}", new java.io.File(".").getCanonicalPath());
    rawData = rawData.replace("{..}", new java.io.File("..").getCanonicalPath());
    rawData = rawData.replace("{EMPTY}", "");
    rawData = rawData.replace("{SPACE}", " ");

    return rawData;
  }

  public static String refactorAutoGeneratorExpressions(String rawData) {
    while (rawData.matches("(?s).*\\{SPACE_.*\\}.*")) {
      int startIndex = rawData.indexOf("{SPACE_") + "{SPACE_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String gen = rawData.substring(startIndex, endIndex);

      int genLength = Integer.parseInt(gen.substring(0, gen.length()));
      rawData = rawData.replaceFirst("\\{SPACE_" + gen + "\\}",
          StringUtils.repeat(" ", genLength));
    }

    while (rawData.matches("(?s).*\\{GENERATE_.*\\}.*")) {
      int startIndex = rawData.indexOf("{GENERATE_") + "{GENERATE_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String gen = rawData.substring(startIndex, endIndex);
      String genCode = gen.substring(0, 3);
      String genLengthStr = gen.substring(3, gen.length());
      if (genLengthStr.contains(":")) {
        int min = Integer.parseInt(genLengthStr.split(":")[0]);
        int max = Integer.parseInt(genLengthStr.split(":")[1]);
        if (genCode.equalsIgnoreCase("NUM")) {
          rawData = rawData.replaceFirst("\\{GENERATE_" + gen + "\\}",
              Generator.generateRandomInteger(min, max));
        }
      } else {
        int genLength = Integer.parseInt(genLengthStr);
        if (genCode.equalsIgnoreCase("NUM")) {
          rawData = rawData.replaceFirst("\\{GENERATE_" + gen + "\\}",
              Generator.generateRandomInteger(genLength));
        } else if (genCode.equalsIgnoreCase("TXT")) {
          rawData = rawData.replaceFirst("\\{GENERATE_" + gen + "\\}",
              Generator.generateRandomString(genLength));
        }
      }
    }

    while (rawData.matches("(?s).*\\{TIMESTAMP_.*\\}.*")) {
      int startIndex = rawData.indexOf("{TIMESTAMP_") + "{TIMESTAMP_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String format = rawData.substring(startIndex, endIndex);

      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
      sdf.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));

      rawData =
          rawData.replaceAll("\\{TIMESTAMP_" + format + "\\}", sdf.format(new java.util.Date()));

    }

    while (rawData.matches("(?s).*\\{CURRENTDATE_.*\\}.*")) {
      int startIndex = rawData.indexOf("{CURRENTDATE_") + "{CURRENTDATE_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String residual = rawData.substring(startIndex, endIndex);
      String format = residual.split("_")[0];
      String action = residual.split("_")[1];
      int change = Integer.parseInt(action.substring(4));

      String substitute = null;

      java.util.Calendar c = java.util.Calendar.getInstance();
      c.setTime(new java.util.Date());

      switch (action.substring(0, 4).toUpperCase()) {

        case "MORE":
          c.add(java.util.Calendar.DATE, change);
          break;
        case "ADDY":
          c.add(java.util.Calendar.YEAR, change);
          break;
        case "ADDM":
          c.add(java.util.Calendar.MONTH, change);
          break;
        case "LESS":
          c.add(java.util.Calendar.DATE, change * -1);
          break;
        case "SUBY":
          c.add(java.util.Calendar.YEAR, change * -1);
          break;
        case "SUBM":
          c.add(java.util.Calendar.MONTH, change * -1);
          break;
      }

      if (format.equalsIgnoreCase("IN-MILLISECONDS")) {
        substitute = String.valueOf(c.getTimeInMillis());
      } else if (format.equalsIgnoreCase("IN-SECONDS")) {
        substitute = String.valueOf(c.getTimeInMillis() / 1000);
      } else {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        substitute = sdf.format(c.getTime());
      }
      rawData = rawData.replaceAll("\\{CURRENTDATE_" + residual + "\\}", substitute);
    }

    while (rawData.matches("(?s).*\\{UPPERCASE_.*\\}.*")) {
      int startIndex = rawData.indexOf("{UPPERCASE_") + "{UPPERCASE_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String strU = rawData.substring(startIndex, endIndex);
      rawData = rawData.replaceAll("\\{UPPERCASE_" + strU + "\\}", strU.toUpperCase());
    }

    while (rawData.matches("(?s).*\\{LOWERCASE_.*\\}.*")) {
      int startIndex = rawData.indexOf("{LOWERCASE_") + "{LOWERCASE_".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String strL = rawData.substring(startIndex, endIndex);
      rawData = rawData.replaceAll("\\{LOWERCASE_" + strL + "\\}", strL.toLowerCase());
    }

    while (rawData.matches("(?s).*\\{ENCRYPT_BASE64:.*\\}.*")) {
      int startIndex = rawData.indexOf("{ENCRYPT_BASE64:") + "{ENCRYPT_BASE64:".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String data = rawData.substring(startIndex, endIndex);
      rawData = rawData.replaceAll("\\{ENCRYPT_BASE64:" + data + "\\}",
          new String(Base64.encodeBase64(data.getBytes())));
    }

    while (rawData.matches("(?s).*\\{DECRYPT_BASE64:.*\\}.*")) {
      int startIndex = rawData.indexOf("{DECRYPT_BASE64:") + "{DECRYPT_BASE64:".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String data = rawData.substring(startIndex, endIndex);
      rawData = rawData.replaceAll("\\{DECRYPT_BASE64:" + data + "\\}",
          new String(Base64.decodeBase64(data.getBytes())));
    }

    while (rawData.matches("(?s).*\\{ENCRYPT_SHA512HEX:.*\\}.*")) {
      int startIndex = rawData.indexOf("{ENCRYPT_SHA512HEX:") + "{ENCRYPT_SHA512HEX:".length();
      int endIndex = rawData.indexOf("}", startIndex);
      String data = rawData.substring(startIndex, endIndex);
      rawData = rawData.replaceAll("\\{ENCRYPT_SHA512HEX:" + data + "\\}",
          DigestUtils.sha512Hex(data.getBytes()));
    }

    return rawData;
  }

  static boolean isDataRefactored(String data) {

    return data == null ? true
        : !data.matches("(?s).*\\{" + HardCodes.ENVIRONMENT_KEYWORD + ":.*\\}.*|(?s).*\\{"
            + HardCodes.FILE_KEYWORD + ":.*\\}.*|(?s).*\\{" + HardCodes.DICTIONARY_KEYWORD
            + ":.*\\}.*|(?s).*\\{" + HardCodes.SYSPROP_KEYWORD + ":.*\\}.*|(?s).*\\{"
            + HardCodes.SYSENVV_KEYWORD + ":.*\\}.*|(?s).*\\{"
            + HardCodes.GLOBAL_KEYWORD + ":.*\\}.*");

  }
}
