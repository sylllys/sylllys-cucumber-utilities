package io.github.sylllys.cucumber.services;

import io.github.sylllys.cucumber.endPoints.ApplicationTestEndpoint;
import io.github.sylllys.cucumber.endPoints.TestEndPointDetails;
import io.github.sylllys.cucumber.endPoints.TestEndPointFactory;
import io.github.sylllys.cucumber.utilities.DataMiner;
import io.github.sylllys.cucumber.utilities.HardCodes;
import java.util.HashMap;

public class HTTPApplicationService {

  TestEndPointDetails testEndPointDetails;

  public HTTPApplicationService() {
    this.testEndPointDetails = new TestEndPointDetails();
  }


  public void constructAndSendRequest(String endPointPath) throws Exception {

    testEndPointDetails = TestEndPointFactory
        .loadTestEndPointDetails(
            HardCodes.configurationDirectoryName + endPointPath);

    sendRequest();
  }

  public void constructAndSendRequest(String endPointPath, HashMap<String, String> customData)
      throws Exception {

    testEndPointDetails = TestEndPointFactory
        .loadTestEndPointDetails(
            HardCodes.configurationDirectoryName + endPointPath);

    if (customData.containsKey("HEADER(S)")) {
      testEndPointDetails.setHeaders(TestEndPointFactory.get("HEADER(S)", customData));
    }
    if (customData.containsKey("PARAM(S)")) {
      testEndPointDetails.setParameters(TestEndPointFactory.get("PARAM(S)", customData));
    }
    if (customData.containsKey("BODY")) {
      testEndPointDetails.setBody(customData.get("BODY"));
    }
    if (customData.containsKey("BODY.EDIT(S)")) {
      testEndPointDetails.setBody(
          TestEndPointFactory
              .editBody(DataMiner.refactor(testEndPointDetails.getBody()), customData));
    }

    sendRequest();
  }

  public void sendRequest() throws Exception {

    ApplicationTestEndpoint applicationTestEndpoint = new ApplicationTestEndpoint();
    applicationTestEndpoint.setEndPointDetails(testEndPointDetails);
    applicationTestEndpoint.constructRequest();
    applicationTestEndpoint.sendRequest();

  }
}
