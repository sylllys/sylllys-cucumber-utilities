package io.github.sylllys.cucumber.services;

import io.github.sylllys.cucumber.endPoints.ApplicationTestEndpoint;
import io.github.sylllys.cucumber.endPoints.TestEndPointDetails;
import io.github.sylllys.cucumber.endPoints.TestEndPointFactory;
import io.github.sylllys.cucumber.utilities.DataMiner;
import io.github.sylllys.cucumber.utilities.HardCodes;

import java.util.List;

public class HTTPApplicationService {

  TestEndPointDetails testEndPointDetails;

  public HTTPApplicationService() {
    this.testEndPointDetails = new TestEndPointDetails();
  }

  public void constructAndSendRequest(List<String> requestDetails)
      throws Exception {

    if (!requestDetails.get(0).toUpperCase()
        .startsWith(HardCodes.YAML_PATH_KEYWORD.toUpperCase())) {
      throw new Exception(
          "Must provide " + HardCodes.YAML_PATH_KEYWORD + "as first item in the list");
    }

    testEndPointDetails = TestEndPointFactory
        .loadTestEndPointDetails(
            HardCodes.configurationDirectoryName + requestDetails.get(0)
                .substring(HardCodes.YAML_PATH_KEYWORD.length()));

    ApplicationTestEndpoint applicationTestEndpoint = new ApplicationTestEndpoint();
    applicationTestEndpoint.setEndPointDetails(testEndPointDetails);
    applicationTestEndpoint.initializePayload();

    for (String requestDetail : requestDetails) {

      if (requestDetail.toUpperCase().startsWith(HardCodes.REQUEST_PATH_VARIABLE_KEYWORD.toUpperCase())) {

        String[] pathVar = requestDetail.substring(HardCodes.REQUEST_PATH_VARIABLE_KEYWORD.length())
                .split(":", 2);

        testEndPointDetails.setPathVariable(pathVar[0], pathVar[1]);
      }

      if (requestDetail.toUpperCase().startsWith(HardCodes.REQUEST_HEADER_KEYWORD.toUpperCase())) {

        String[] header = requestDetail.substring(HardCodes.REQUEST_HEADER_KEYWORD.length())
            .split(":", 2);

        testEndPointDetails.setHeader(header[0], header[1]);
      }

      if (requestDetail.toUpperCase()
          .startsWith(HardCodes.REQUEST_PARAMETER_KEYWORD.toUpperCase())) {

        String[] header = requestDetail.substring(HardCodes.REQUEST_PARAMETER_KEYWORD.length())
            .split(":", 2);

        testEndPointDetails.setParameter(header[0], header[1]);
      }

      if (requestDetail.toUpperCase().startsWith(HardCodes.REQUEST_BODY_KEYWORD.toUpperCase())) {

        testEndPointDetails
            .setBody(requestDetail.substring(HardCodes.REQUEST_BODY_KEYWORD.length()));
      }

      if (requestDetail.toUpperCase()
          .startsWith(HardCodes.REQUEST_BODY_EDIT_KEYWORD.toUpperCase())) {
        testEndPointDetails.setBody(
            TestEndPointFactory
                .editBody(DataMiner.refactorKeywordExpressions(testEndPointDetails.getBody()),
                    requestDetail.substring(HardCodes.REQUEST_BODY_EDIT_KEYWORD.length())
                        .split(":", 2)));
      }
    }

    applicationTestEndpoint.constructRequest();
    applicationTestEndpoint.sendRequest();

  }
}
