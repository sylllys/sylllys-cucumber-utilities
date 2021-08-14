package io.github.sylllys.cucumber.bluePrints;

import io.github.sylllys.cucumber.utilities.DataMiner;
import io.github.sylllys.cucumber.utilities.HardCodes;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class TestEndpoint {

  protected URL endPointURL = null;
  protected Response response = null;
  protected RequestSpecification request = null;
  protected Map<String, String> urlQueryParameters = null;

  public Response getResponse() {
    return this.response;
  }

  public abstract void initializePayload() throws Exception;

  public abstract void constructRequest() throws Exception;

  protected void constructRequest(String url, HashMap<String, String> requestHeaders,
      HashMap<String, String> queryParams, Object body) throws Exception {

    request = RestAssured.given();

    endPointURL = new URL(DataMiner.refactor(url));

    if (queryParams != null) {
      for (String key : queryParams.keySet()) {
        queryParams.put(key, DataMiner.refactor(queryParams.get(key)));
      }

      urlQueryParameters = new HashMap<String, String>(queryParams);

      while (queryParams.values().remove(null)) {
        ;
      }

      urlQueryParameters = queryParams;

      request.queryParams(queryParams);
    }

    if (requestHeaders != null) {
      for (String key : requestHeaders.keySet()) {
        requestHeaders.put(key, DataMiner.refactor(requestHeaders.get(key)));
      }

      while (requestHeaders.values().remove(null)) {
        ;
      }

      request.headers(requestHeaders);
    }

    if (body != null) {

      if (requestHeaders != null && requestHeaders.containsKey("content-type") && requestHeaders
          .get("content-type")
          .contains("multipart/form-data")) {

        HashMap<String, String> formData = (HashMap<String, String>) body;

        for (String key : formData.keySet()) {
          if (key.equals("file") || key.equals("files")) {

            String filePaths = DataMiner.refactor(formData.get(key));

            for (String filePath : filePaths.split(";")) {
              request.contentType(requestHeaders.get("content-type"))
                  .multiPart(key, new File(
                      HardCodes.configurationFilesDirectory + filePath));
            }
          } else {

            request.contentType(requestHeaders.get("content-type"))
                .multiPart(key, DataMiner.refactor(formData.get(key)));
          }
        }
      } else {

        request.body(DataMiner.refactor(body.toString()));
      }
    }
  }

  public abstract void sendRequest() throws Exception;

  protected void sendRequest(Method httpMethod) throws Exception {

    response = request.request(httpMethod, endPointURL);

    PreviousTestEndpoint.details(request, response, endPointURL, httpMethod);

  }

}
