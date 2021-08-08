package io.github.sylllys.cucumber.endPoints;

import io.github.sylllys.cucumber.bluePrints.HttpRequestActions;
import io.github.sylllys.cucumber.bluePrints.TestEndpoint;
import io.github.sylllys.cucumber.utilities.GlobalVariables;
import io.github.sylllys.cucumber.utilities.JSONFactory;
import io.restassured.http.Cookie;

public class ApplicationTestEndpoint extends TestEndpoint {

  TestEndPointDetails endPointDetails;

  public void setEndPointDetails(TestEndPointDetails endPointDetails) {
    this.endPointDetails = endPointDetails;
  }

  @Override
  public void initializePayload() throws Exception {

    if (endPointDetails.getInitialize() != null && endPointDetails.getInitialize().size() > 0) {
      for (String key : endPointDetails.getInitialize().keySet()) {
        String var_prefix = key.startsWith(".") ? endPointDetails.getName() + ".request" : "";
        GlobalVariables.initialize( var_prefix + key,
            endPointDetails.getInitialize().get(key));
      }
    }

  }

  @Override
  public void constructRequest() throws Exception {

    super.constructRequest(endPointDetails.getUrl(), endPointDetails.getHeaders(),
        endPointDetails.getParameters(), endPointDetails.getBody());

    if (endPointDetails.getCookies() != null && endPointDetails.getCookies().size() > 0) {
      for (String cookie : endPointDetails.getCookies().keySet()) {
        Cookie cookieObject = new Cookie.Builder(cookie, endPointDetails.getCookies().get(cookie))
            .build();
        super.request.cookie(cookieObject);
      }
    }

  }

  @Override
  public void sendRequest() throws Exception {

    super.sendRequest(HttpRequestActions.valueOf(endPointDetails.getMethod()));

    if (endPointDetails.getSave() != null) {
      for (Integer statusCode : endPointDetails.getSave().keySet()) {

        if (statusCode == super.getResponse().getStatusCode()) {

          String tuples[] = endPointDetails.getSave().get(statusCode).split(",");

          for (String tuple : tuples) {
            GlobalVariables.initialize(endPointDetails.getName() + ".response." + tuple,
                JSONFactory.getValue(super.getResponse().getBody().asString(), tuple));
          }

        }

      }
    }
  }
}
