package io.github.sylllys.cucumber.utilities;

import static org.junit.Assert.assertTrue;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/*
 * This class contains code to read values from json tuple, verify json tuple exists, asserts value
 * in json tuple.
 */
public class JSONFactory {

  public static String getValue(String jsonContent, String path)
      throws PathNotFoundException {
    Object content = JsonPath.read(jsonContent, path);
    return content == null ? null : content.toString();
  }

  public static Object get(String jsonContent, String path)
      throws PathNotFoundException {
    Object content = JsonPath.read(jsonContent, path);
    return content;
  }

  public static boolean isExists(String jsonString, String path)
      throws PathNotFoundException {

    try {

      try {
        net.minidev.json.JSONArray actualList = (net.minidev.json.JSONArray) get(jsonString, path);

        if (actualList.size() == 0) {
          throw new PathNotFoundException();
        }
      } catch (ClassCastException e) {
        getValue(jsonString, path);
      }
    } catch (PathNotFoundException e) {
      return false;
    }

    return true;
  }

  public static boolean assertValue(String jsonString, String path, String expectedValue)
      throws Exception {

    expectedValue = DataMiner.refactor(expectedValue);

    try {
      String actualValue = getValue(jsonString, path);
      assertTrue("JSON value @ path:" + path + " is not as expected:" + expectedValue + ", actual:"
          + actualValue, expectedValue.equals(actualValue));
    } catch (PathNotFoundException e) {
      assertTrue("JSON @ path:" + path + " does not exists", false);
      return false;
    }

    return true;
  }

  public static boolean assertMatch(String jsonString, String path, String regex) throws Exception {

    try {
      String actualValue = getValue(jsonString, path);
      assertTrue(
          "JSON value @ path:" + path + " is not as expected:" + regex + ", actual:" + actualValue,
          actualValue.matches(regex));
    } catch (PathNotFoundException e) {
      assertTrue("JSON @ path:" + path + " does not exists", false);
      return false;
    }

    return true;
  }

  public static boolean assertContains(String jsonString, String path, String expectedValue)
      throws Exception {

    expectedValue = DataMiner.refactor(expectedValue);

    try {
      String actualValue = getValue(jsonString, path);
      assertTrue("JSON value @ path:" + path + " is not as expected:" + expectedValue + ", actual:"
          + actualValue, actualValue.contains(expectedValue));
    } catch (PathNotFoundException e) {
      assertTrue("JSON @ path:" + path + " does not exists", false);
      return false;
    }

    return true;
  }

  public static boolean assertNotContains(String jsonString, String path, String expectedValue)
      throws Exception {

    expectedValue = DataMiner.refactor(expectedValue);

    try {
      String actualValue = getValue(jsonString, path);
      assertTrue("JSON value @ path:" + path + " should not contain :" + expectedValue + ", actual:"
          + actualValue, !actualValue.contains(expectedValue));
    } catch (PathNotFoundException e) {
      assertTrue("JSON @ path:" + path + " does not exists", false);
      return false;
    }

    return true;
  }

  public static boolean assertItem(String jsonString, String path, String item) throws Exception {

    try {
      net.minidev.json.JSONArray actualList = (net.minidev.json.JSONArray) get(jsonString, path);
      assertTrue(
          "Array list @ path:" + path + " does not have item:" + item + ", actual list:"
              + actualList,
          actualList.toString().contains(item));
    } catch (PathNotFoundException e) {
      assertTrue("JSON @ path:" + path + " does not exists", false);
      return false;
    }

    return true;
  }

  public static boolean assertNoItem(String jsonString, String path, String item) throws Exception {

    try {
      net.minidev.json.JSONArray actualList = (net.minidev.json.JSONArray) get(jsonString, path);
      assertTrue(
          "Array list @ path:" + path + " does have item:" + item + ", actual list:"
              + actualList,
          !actualList.toString().contains(item));
    } catch (PathNotFoundException e) {
      assertTrue("JSON @ path:" + path + " does not exists", false);
      return false;
    }

    return true;
  }
}
