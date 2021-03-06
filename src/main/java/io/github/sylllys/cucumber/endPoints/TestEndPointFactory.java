package io.github.sylllys.cucumber.endPoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.apache.commons.io.FilenameUtils;

public class TestEndPointFactory {

  private static final Configuration configurationDefault =
      Configuration.builder()
          .jsonProvider(new JacksonJsonNodeJsonProvider())
          .mappingProvider(new JacksonMappingProvider())
          .build();

  private static final Configuration configurationForArrayAdditions =
      Configuration.defaultConfiguration()
          .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

  public static TestEndPointDetails loadTestEndPointDetails(String endPointPath)
      throws IOException {

    InputStream endPoint = TestEndPointFactory.class.getResourceAsStream(endPointPath);

    ObjectMapper ymlmapper = new ObjectMapper(new YAMLFactory());
    TestEndPointDetails testEndPointDetails = ymlmapper
        .readValue(endPoint, TestEndPointDetails.class);
    testEndPointDetails.setName(FilenameUtils.removeExtension(FilenameUtils.getName(endPointPath)));
    return testEndPointDetails;
  }

  public static String editBody(String body, String[] bodyEdit) {

    String tupleName = bodyEdit[0];
    String tupleValue = bodyEdit[1];

    try {
      JsonPath.using(configurationDefault).parse(body).read(tupleName).toString();

      body = setBody(body, tupleName, tupleValue);
    } catch (com.jayway.jsonpath.PathNotFoundException e) {

      body = addBody(body, tupleName, tupleValue);
    }

    return body;
  }

  public static String setBody(String body, String key, String value) {

    JsonNode updatedJson = JsonPath.using(configurationDefault).parse(body)
        .set(key, value).json();
    body = updatedJson.toString();

    return body;
  }

  public static String addBody(String body, String key, String value) {
    String newField = key;
    String parentField = "";
    if (newField.contains(".")) {
      newField = key.substring(key.lastIndexOf('.') + 1);
      parentField = key.substring(0, key.lastIndexOf('.'));
    }

    JsonNode updatedJson = JsonPath.using(configurationDefault).parse(body)
        .put("$" + (parentField.equals("") ? "" : ("." + parentField)), newField,
            value).json();
    body = updatedJson.toString();

    try {

      JsonPath.using(configurationDefault).parse(body).read(key).toString();
    } catch (com.jayway.jsonpath.PathNotFoundException e) {

      JsonPath pathToArray = JsonPath.compile("$." + parentField.replaceAll("\\[\\d+\\]", ""));

      DocumentContext document = JsonPath.using(configurationForArrayAdditions).parse(body);
      body = document.add(pathToArray, Collections
          .singletonMap(newField, value)).jsonString();
    }

    body = body.replaceAll("\"\\{\\s{1,}\\}\"", "{}").replaceAll("\"\\[\\s{1,}\\]\"", "[]");
    return body;
  }
}
