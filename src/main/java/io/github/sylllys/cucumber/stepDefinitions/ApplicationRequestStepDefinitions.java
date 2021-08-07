package io.github.sylllys.cucumber.stepDefinitions;

import io.cucumber.java.en.When;
import io.github.sylllys.cucumber.services.HTTPApplicationService;
import java.util.HashMap;
import java.util.Map;

public class ApplicationRequestStepDefinitions {

  @When("^a request is sent to end point:(.*), using default data$")
  public void sendRequest(String endPointPath) throws Exception {

    HTTPApplicationService httpApplicationService = new HTTPApplicationService();
    httpApplicationService.constructAndSendRequest(endPointPath);
  }

  @When("^a request is sent to end point:(.*), using custom data$")
  public void sendRequest(String endPointPath, Map<String, String> customData) throws Exception {

    HTTPApplicationService httpApplicationService = new HTTPApplicationService();
    httpApplicationService
        .constructAndSendRequest(endPointPath, new HashMap<String, String>(customData));
  }

}
