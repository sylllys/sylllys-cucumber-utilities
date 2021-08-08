package io.github.sylllys.cucumber.endPoints;


import java.util.HashMap;

public class TestEndPointDetails {

  private String name;
  private String url;
  private String method;
  private HashMap<String, String> parameters;
  private HashMap<String, String> headers;
  private HashMap<String, String> cookies;
  private HashMap<String, String> initialize;
  private HashMap<Integer, String> save;
  private String body;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public HashMap<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(HashMap<String, String> parameters) {
    this.parameters = parameters;
  }

  public void setParameter(String key, String value) {

    if (this.parameters == null) {
      this.parameters = new HashMap<String, String>();
    }

    this.parameters.put(key, value);
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }

  public void setHeader(String key, String value) {

    if (this.headers == null) {
      this.headers = new HashMap<String, String>();
    }

    this.headers.put(key, value);
  }

  public HashMap<String, String> getCookies() {
    return cookies;
  }

  public void setCookies(HashMap<String, String> cookies) {
    this.cookies = cookies;
  }

  public HashMap<String, String> getInitialize() {
    return initialize;
  }

  public void setInitialize(HashMap<String, String> initialize) {
    this.initialize = initialize;
  }

  public HashMap<Integer, String> getSave() {
    return save;
  }

  public void setSave(HashMap<Integer, String> save) {
    this.save = save;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return "TestEndPoint{" +
        "url='" + url + '\'' +
        ", method='" + method + '\'' +
        ", parameters=" + parameters +
        ", headers=" + headers +
        ", body='" + body + '\'' +
        '}';
  }
}
