package com.google.sps.servlets;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/** Sets up servlets for the web application and injects necessary dependencies */
public class ServletConfig extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new DatabaseServletModule());
  }
}
