package io.github.sylllys.cucumber.stepDefinitions;

import io.cucumber.java.en.Given;
import io.github.sylllys.cucumber.services.HTTPApplicationService;
import java.util.List;

public class ApplicationRequestStepDefinitions {

  @Given("^a request is sent to end point")
  public void sendRequest(List<String> requestDetails) throws Exception {

    HTTPApplicationService httpApplicationService = new HTTPApplicationService();
    httpApplicationService.constructAndSendRequest(requestDetails);
  }

}
