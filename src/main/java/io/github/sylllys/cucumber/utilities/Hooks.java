package io.github.sylllys.cucumber.utilities;


import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.io.IOException;

/*
 * Contains all hooks that needs to be executed before or after a scenario
 */
public class Hooks {

  @Before
  public void beforeScenario() throws IOException {

    GlobalVariables.resetGlobalVariables();
    Configurations.loadBackup();

  }
}
