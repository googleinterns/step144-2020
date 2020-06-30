package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.servlet.ServletModule;

public class DatabaseServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    super.configureServlets();

    serve("/careerquiz").with(CareerQuizServlet.class);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // define above specific instance to be injected into CareerQuizServlet whenever a
    // CareerQuestionDatabase is requested
    bind(DatastoreService.class).toInstance(datastore);
  }
}
