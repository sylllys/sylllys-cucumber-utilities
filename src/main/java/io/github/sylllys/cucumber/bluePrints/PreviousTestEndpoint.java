package io.github.sylllys.cucumber.bluePrints;

import io.github.sylllys.cucumber.hooks.UtilityHooks;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.net.URL;

public class PreviousTestEndpoint {

  public static Response response;
  public static RequestSpecification request;
  public static URL url;
  public static Method httpMethod;

  static void details(RequestSpecification req, Response res, URL endPoint, Method method) {

    response = res;
    request = req;
    url = endPoint;
    httpMethod = method;
  }

  public static void sendRequest() throws Exception {

    response = request.request(httpMethod, url);

    UtilityHooks.extractAPIDetailsIntoLogs();
  }


}
