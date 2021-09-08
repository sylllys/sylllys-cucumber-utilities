package io.github.sylllys.cucumber.hooks;


import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.sylllys.cucumber.utilities.Configurations;
import io.github.sylllys.cucumber.utilities.CustomLogFilter;
import io.github.sylllys.cucumber.utilities.GlobalVariables;
import java.io.IOException;

/*
 * Contains all hooks that needs to be executed before or after a scenario
 */
public class UtilityHooks {

  private static Scenario scenario;

  public static CustomLogFilter getLogFilter() {
    return logFilter;
  }

  private static CustomLogFilter logFilter;

  @Before
  public void beforeScenario(Scenario scenario) throws IOException {

    this.scenario = scenario;
    System.out.println(this.scenario.getName());
    logFilter = new CustomLogFilter();
    GlobalVariables.resetGlobalVariables();
    Configurations.loadBackup();

  }

  public static void extractAPIDetailsIntoLogs(){

    scenario.log(
            "\n-----API Request details-----\n" +
                    logFilter.getRequestBuilder() +
                    "\n\n-----API Response details-----" +
                    logFilter.getResponseBuilder());
  }
}
