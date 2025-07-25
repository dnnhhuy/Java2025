package edu.pdx.cs.joy.whitlock;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper.Response;
import edu.pdx.cs.joy.web.HttpRequestHelper.RestException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send dictionary entries.
 */
public class AppointmentBookRestClient {
  private static final String WEB_APP = "apptbook";
  private static final String SERVLET = "appointments";

  private final HttpRequestHelper http;


  /**
   * Creates a client to the appointment book REST service running on the given host and port
   *
   * @param hostName The name of the host
   * @param port     The port
   */
  public AppointmentBookRestClient(String hostName, int port) {
    this(new HttpRequestHelper(String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET)));
  }

  @VisibleForTesting
  AppointmentBookRestClient(HttpRequestHelper http) {
    this.http = http;
  }


  /**
   * Returns all dictionary entries from the server
   */
//  public Map<String, String> getAllDictionaryEntries() throws IOException, ParserException {
//    Response response = http.get(Map.of());
//    throwExceptionIfNotOkayHttpStatus(response);
//
//    TextParser parser = new TextParser(new StringReader(response.getContent()));
//    return parser.parse();
//  }

  /**
   * Returns the definition for the given word
   */
//  public String getDefinition(String word) throws IOException, ParserException {
//    Response response = http.get(Map.of(AppointmentBookServlet.WORD_PARAMETER, word));
//    throwExceptionIfNotOkayHttpStatus(response);
//    String content = response.getContent();
//
//    TextParser parser = new TextParser(new StringReader(content));
//    return parser.parse().get(word);
//  }

//  public void addDictionaryEntry(String word, String definition) throws IOException {
//    Response response = postToMyURL(Map.of(AppointmentBookServlet.WORD_PARAMETER, word, AppointmentBookServlet.DEFINITION_PARAMETER, definition));
//    throwExceptionIfNotOkayHttpStatus(response);
//  }

  /**
   * Get all appointments given owner name
   */
  public AppointmentBook getAppointments(String owner) throws IOException, ParserException {
    Response response = http.get(Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner));
    throwExceptionIfNotOkayHttpStatus(response);
    String content = response.getContent();

    TextParser parser = new TextParser(new StringReader(content));
    return parser.parse();
  }

  /**
   * Get appointments given owner and time range
   */
  public AppointmentBook getAppointmentsInTimeRange(String owner, String begin, String end) throws ParserException, IOException {
    Response response = http.get(Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner, AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER, begin, AppointmentBookServlet.APPOINTMENT_END_PARAMETER, end));
    throwExceptionIfNotOkayHttpStatus(response);
    String content = response.getContent();

    TextParser parser = new TextParser(new StringReader(content));
    return parser.parse();
  }

  public void addAppointment(String owner, String description, String begin, String end) throws IOException {
    Response response = http.post(Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner, AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER, description, AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER, begin, AppointmentBookServlet.APPOINTMENT_END_PARAMETER, end));
    throwExceptionIfNotOkayHttpStatus(response);
  }

//  public void removeAppointmentBook(String owner) throws IOException {
//    Response response = http.post(Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner));
//    throwExceptionIfNotOkayHttpStatus(response);
//  }

  public void removeAllAppointmentBooks() throws IOException {
    Response response = http.delete(Map.of());
    throwExceptionIfNotOkayHttpStatus(response);
  }

  @VisibleForTesting
  Response postToMyURL(Map<String, String> dictionaryEntries) throws IOException {
    return http.post(dictionaryEntries);
  }

  public void removeAllDictionaryEntries() throws IOException {
    Response response = http.delete(Map.of());
    throwExceptionIfNotOkayHttpStatus(response);
  }

  private void throwExceptionIfNotOkayHttpStatus(Response response) {
    int code = response.getHttpStatusCode();
    if (code != HTTP_OK) {
      String message = response.getContent();
      throw new RestException(code, message);
    }
  }

}
