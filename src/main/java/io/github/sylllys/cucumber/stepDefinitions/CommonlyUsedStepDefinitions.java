package io.github.sylllys.cucumber.stepDefinitions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.github.sylllys.cucumber.utilities.DataMiner;
import io.github.sylllys.cucumber.utilities.GlobalVariables;
import io.github.sylllys.cucumber.utilities.JSONFactory;
import io.github.sylllys.cucumber.bluePrints.PreviousTestEndpoint;
import io.restassured.response.Response;
import java.util.Map;
import java.util.regex.Pattern;

public class CommonlyUsedStepDefinitions {

  @Then("^print (.*)$")
  public void print(String text) throws Exception {
    System.out.println(DataMiner.refactor(text));
  }

  @Given("^\\{global:(.*)\\} as (.*)$")
  public void setGlobal(String key, String value) throws Exception {
    GlobalVariables.put(key, value);
  }

  @Then("^verify response code is (.*)$")
  public void verifyResponseCode(int expectedResponseCode) throws Exception {

    Response response = PreviousTestEndpoint.response;

    assertTrue("reponse code is not as expected:" + expectedResponseCode + ", actual:"
        + response.getStatusCode(), response.getStatusCode() == expectedResponseCode);

  }

  @Then("^verify response body is JSON with tuple\\(s\\):(.*)$")
  public void verifyJSONResponseBody(String tuples) throws Exception {

    Response response = PreviousTestEndpoint.response;

    String regex = "(?<!\\\\)" + Pattern.quote(",");

    for (String tuple : tuples.split(regex)) {

      String key = tuple.split("=", 2)[0];
      String value = tuple.split("=", 2)[1].replace("\\,", ",");

      if (value.equalsIgnoreCase("does not exists") || value.equalsIgnoreCase("doesn't exists")) {
        assertFalse("JSON tuple:" + key + " exists",
            JSONFactory.isExists(response.getBody().asString(), key));
      } else if (value.startsWith("contains ")) {
        JSONFactory.assertContains(response.getBody().asString(), key, value.substring(9));
      } else if (value.startsWith("!contains ")) {
        JSONFactory.assertNotContains(response.getBody().asString(), key, value.substring(10));
      } else if (value.startsWith("regex ")) {
        JSONFactory.assertMatch(response.getBody().asString(), key, value.substring(6));
      } else if (value.startsWith("has item ")) {
        JSONFactory.assertItem(response.getBody().asString(), key, value.substring(9));
      } else if (value.startsWith("does not have item ")) {
        JSONFactory.assertNoItem(response.getBody().asString(), key, value.substring(19));
      } else {
        JSONFactory.assertValue(response.getBody().asString(), key, value);
      }

    }
  }

  @Then("^verify response body is JSON with tuple\\(s\\)$")
  public void verifyJSONResponseBody(Map<String, String> tuples) throws Exception {

    Response response = PreviousTestEndpoint.response;
    verifyJSONResponseBody(tuples, response.getBody().asString());
  }

  private void verifyJSONResponseBody(Map<String, String> tuples, String body) throws Exception {

    for (String key : tuples.keySet()) {

      String value = DataMiner.refactor(tuples.get(key));

      if (value == null) {
        assertTrue("JSON tuple:" + key + " is not null", JSONFactory.getValue(body, key) == null);
      } else if (value.equalsIgnoreCase("does not exists") || value
          .equalsIgnoreCase("doesn't exists")) {
        assertFalse("JSON tuple:" + key + " exists",
            JSONFactory.isExists(body, key));
      } else if (value.startsWith("contains ")) {
        JSONFactory.assertContains(body, key, value.substring(9));
      } else if (value.startsWith("!contains ")) {
        JSONFactory.assertNotContains(body, key, value.substring(10));
      } else if (value.startsWith("regex ")) {
        JSONFactory.assertMatch(body, key, value.substring(6));
      } else if (value.startsWith("has item ")) {
        JSONFactory.assertItem(body, key, value.substring(9));
      } else if (value.startsWith("does not have item ")) {
        JSONFactory.assertNoItem(body, key, value.substring(19));
      } else {
        JSONFactory.assertValue(body, key, value);
      }

    }

  }

  @Then("^verify response body is text with content (.*)$")
  public void verifyTextResponseBody(String contentList) throws Exception {

    String delim = "(?<!\\\\)" + Pattern.quote(",");

    Response response = PreviousTestEndpoint.response;
    String responseContent = response.getBody().asString().trim();

    responseContent = responseContent
        .replaceAll("(\\r\\n|\\r|\\n)", System.getProperty("line.separator"));

    for (String content : contentList.split(delim)) {

      content = DataMiner.refactor(content.trim());

      if (content.startsWith("contains ")) {
        assertTrue("response body does not contain text:" + content.substring(9),
            responseContent.contains(content.substring(9)));
      } else if (content.startsWith("!contains ")) {
        assertFalse("response body does contain text:" + content.substring(10),
            responseContent.contains(content.substring(10)));
      } else if (content.startsWith("regex ")) {
        assertTrue("response body does not matches regex:" + content.substring(6),
            responseContent.matches(content.substring(6)));
      } else {
        assertTrue("response body does not equals text:" + content,
            responseContent.equals(content));
      }

    }
  }

  @Then("^wait for (\\d+) seconds$")
  public void waitForAccruals(int waitTime) throws Exception {
    Thread.sleep(1000 * waitTime);
  }

  @Then("^resend request for (\\d+) time\\(s\\), until JSON response body has")
  public void resendRequest(int times, Map<String, String> tuples) throws Exception {

    do {
      try {
        verifyJSONResponseBody(tuples);
        return;
      } catch (java.lang.AssertionError e) {
        Thread.sleep(5 * 1000);
        PreviousTestEndpoint.sendRequest();
        times--;
      }
    } while (times > 0);

    throw new Exception(
        "response is not as expected even after resending the request for multiple time(s)");
  }
}
