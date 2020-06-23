package com.google.sps.servlets;

import com.google.sps.servlets.CareerQuizServlet;
import com.google.sps.data.CareerQuestionDatabase;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/** Sets up servlets for the web application and injects necessary dependencies*/
public class ServletConfig extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new ServletModule() {
      @Override
      protected void configureServlets() {
        super.configureServlets();

        serve("/careerquiz").with(CareerQuizServlet.class);
                
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        CareerQuestionDatabase careerQuestionDatabase = new CareerQuestionDatabase(datastore);

        // define above specific instance to be injected into CareerQuizServlet whenever a 
        // CareerQuestionDatabase is requested
        bind(CareerQuestionDatabase.class).toInstance(careerQuestionDatabase); 
      }
    });
  }
}
