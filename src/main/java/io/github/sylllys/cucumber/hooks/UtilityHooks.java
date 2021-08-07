package io.github.sylllys.cucumber.hooks;


import io.cucumber.java.Before;

import io.github.sylllys.cucumber.utilities.Configurations;
import io.github.sylllys.cucumber.utilities.GlobalVariables;
import java.io.IOException;

/*
 * Contains all hooks that needs to be executed before or after a scenario
 */
public class UtilityHooks {

  @Before
  public void beforeScenario() throws IOException {

    GlobalVariables.resetGlobalVariables();
    Configurations.loadBackup();

  }
}
