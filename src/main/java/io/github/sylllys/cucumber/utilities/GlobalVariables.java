package io.github.sylllys.cucumber.utilities;

import java.util.HashMap;

/*
 * Code to maintain a hashmap to store key, value pairs which can be used as global variables
 *
 * functions to clean, write and read from the global hashmap
 */
public final class GlobalVariables {

  static HashMap<String, String> hashmap = new HashMap<String, String>();

  public static void put(String key, String value) throws Exception {

    hashmap.put(key, DataMiner.refactorAutoGeneratorExpressions(value));
  }

  public static void initialize(String key, String value) throws Exception {

    if (!hashmap.containsKey(key)) {
      put(key, value);
    }
  }

  public static String get(String key) throws Exception {

    if (!hashmap.containsKey(key)) {
      throw new Exception("No global variable found with KEY : " + key);
    }

    return hashmap.get(key);
  }

  public static String forceGet(String key) {

    return hashmap.get(key);
  }

  public static void resetGlobalVariables() {
    hashmap.clear();
  }
}
