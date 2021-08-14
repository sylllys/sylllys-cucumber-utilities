package io.github.sylllys.cucumber.utilities;

import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/*
 * code to generate random strings and numbers
 */
public class Generator {

  public static String generateRandomString(int length) {
    String str = "";

    for (int i = 0; i < length; i++) {
      Random randomGenerator = new Random();
      str = str + (char) (97 + randomGenerator.nextInt(26));
    }

    return str;
  }

  public static String generateRandomInteger(int length) {
    String str = "";

    for (int i = 0; i < length; i++) {
      Random randomGenerator = new Random();
      str = str + String.valueOf(randomGenerator.nextInt(10));

      if (str.equals("0")) {
        str = "";
        i--;
      }
    }

    return str;
  }

  public static String generateRandomInteger(int min, int max) {
    return String.valueOf(Math.round(min + (Math.random() * (max - min))));
  }

  public static String generateTimeStamp(String details) throws Exception {

    String format = null;
    String zone = null;
    int change = 0;
    String action = "+DAY";

    for (String detail : details.split("_")) {

      String[] prefix = detail.split(":", 2);

      switch (prefix[0].toLowerCase()) {
        case "zone":
          zone = prefix[1];
          break;
        case "format":
          format = prefix[1];
          break;
        case "edit":
          String[] edit = prefix[1].split(":");
          action = edit[0];
          change = Integer.parseInt(edit[1]);
          break;
      }
    }

    java.util.Calendar c = java.util.Calendar.getInstance();
    c.setTime(new Date());

    switch (action.toUpperCase()) {

      case "+DAY":
        c.add(java.util.Calendar.DATE, change);
        break;
      case "+YEAR":
        c.add(java.util.Calendar.YEAR, change);
        break;
      case "+MONTH":
        c.add(java.util.Calendar.MONTH, change);
        break;
      case "-DAY":
        c.add(java.util.Calendar.DATE, change * -1);
        break;
      case "-YEAR":
        c.add(java.util.Calendar.YEAR, change * -1);
        break;
      case "-MONTH":
        c.add(java.util.Calendar.MONTH, change * -1);
        break;
    }

    if (format.equalsIgnoreCase("IN-MILLISECONDS")) {
      return String.valueOf(c.getTimeInMillis());
    } else if (format.equalsIgnoreCase("IN-SECONDS")) {
      return String.valueOf(c.getTimeInMillis() / 1000);
    } else {
      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
      if (zone != null) {
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
      }
      return sdf.format(c.getTime());
    }
  }
}
