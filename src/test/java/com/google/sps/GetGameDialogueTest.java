package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.sps.servlets.GetGameDialogue;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public final class GetGameDialogueTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private GetGameDialogue getGameDialogue = new GetGameDialogue();
  private static final Gson gson = new Gson();
  private static final String TEST_STRING = "hello this is a test";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests that the doGet method returns JSON containing database queried career question and
   * choices
   */
  @Test
  public void testGetGameDialogue_Outputs() throws IOException {
    // mocks the HttpServletResponse, which uses a writer to output JSON response
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(this.response.getWriter()).thenReturn(printWriter);

    // mocks the result of querying the GetGameDialogue
    this.getGameDialogue.doGet(this.request, this.response);
    // checks that the string writer used in servlet mock response contains the database object JSON
    // that matches with the hardcoded CareerQAndChoice given be the mock database
    Gson gson = new Gson();
    JsonElement expected = JsonParser.parseString(gson.toJson(TEST_STRING));
    JsonElement result = JsonParser.parseString(stringWriter.toString());
    Assert.assertEquals(expected, result);
  }
}
