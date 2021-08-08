package io.github.sylllys.cucumber.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.github.sylllys.cucumber.services.HTTPApplicationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationRequestStepDefinitions {

  @Given("^a request is sent to url$")
  public void sendRequest(List<String> requestDetails) throws Exception {

    HTTPApplicationService httpApplicationService = new HTTPApplicationService();
    httpApplicationService.constructAndSendRequest(requestDetails);
  }

}
