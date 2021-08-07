package io.github.sylllys.cucumber.utilities;

import java.util.Random;

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
}
