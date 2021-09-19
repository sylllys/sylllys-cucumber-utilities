package io.github.sylllys.cucumber.stepDefinitions;

import static org.junit.Assert.assertTrue;

import io.cucumber.java.en.Given;
import io.github.sylllys.cucumber.bluePrints.PreviousTestEndpoint;
import io.github.sylllys.cucumber.services.HTTPApplicationService;
import io.restassured.response.Response;
import java.util.List;

public class ApplicationRequestStepDefinitions {

  @Given("^a request is sent to end point")
  public void sendRequest(List<String> requestDetails) throws Exception {

    HTTPApplicationService httpApplicationService = new HTTPApplicationService();
    httpApplicationService.constructAndSendRequest(requestDetails);
  }

  @Given("^a request is sent to end point, expecting success response code")
  public void sendRequestSucess(List<String> requestDetails) throws Exception {

    sendRequest(requestDetails);

    Response response = PreviousTestEndpoint.response;

    assertTrue("reponse code is not 2xx:"
        + response.getStatusCode(), response.getStatusCode() / 100 == 2);
  }

  @Given("^a request is sent to end point, expecting client error response code")
  public void sendRequestNotSucess(List<String> requestDetails) throws Exception {

    sendRequest(requestDetails);

    Response response = PreviousTestEndpoint.response;

    assertTrue("reponse code is not 4xx:"
        + response.getStatusCode(), response.getStatusCode() / 100 == 4);
  }

}
