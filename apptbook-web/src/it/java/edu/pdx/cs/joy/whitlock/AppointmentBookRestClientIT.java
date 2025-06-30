package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper.RestException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test that tests the REST calls made by {@link AppointmentBookRestClient}
 */

@TestMethodOrder(MethodName.class)
class AppointmentBookRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private AppointmentBookRestClient newAppointmentBookRestClient() {
    int port = Integer.parseInt(PORT);
    return new AppointmentBookRestClient(HOSTNAME, port);
  }

//  @Test
//  void test0RemoveAllDictionaryEntries() throws IOException {
//    AppointmentBookRestClient client = newAppointmentBookRestClient();
//    client.removeAllDictionaryEntries();
//  }
//
//  @Test
//  void test1EmptyServerContainsNoDictionaryEntries() throws IOException, ParserException {
//    AppointmentBookRestClient client = newAppointmentBookRestClient();
//    Map<String, String> dictionary = client.getAllDictionaryEntries();
//    assertThat(dictionary.size(), equalTo(0));
//  }
//
//  @Test
//  void test2DefineOneWord() throws IOException, ParserException {
//    AppointmentBookRestClient client = newAppointmentBookRestClient();
//    String testWord = "TEST WORD";
//    String testDefinition = "TEST DEFINITION";
//    client.addDictionaryEntry(testWord, testDefinition);
//
//    String definition = client.getDefinition(testWord);
//    assertThat(definition, equalTo(testDefinition));
//  }
//
//  @Test
//  void test4EmptyWordThrowsException() {
//    AppointmentBookRestClient client = newAppointmentBookRestClient();
//    String emptyString = "";
//
//    RestException ex = assertThrows(RestException.class, () -> client.addDictionaryEntry(emptyString, emptyString));
//    assertThat(ex.getHttpStatusCode(), equalTo(HttpURLConnection.HTTP_PRECON_FAILED));
//    assertThat(ex.getMessage(), containsString(Messages.missingRequiredParameter(AppointmentBookServlet.WORD_PARAMETER)));  }
  @Test
  void test0RemoveAppointmentBookFromServer() throws IOException {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    client.removeAllAppointmentBooks();
  }

  @Test
  void test1AddAppointmentToServer() throws IOException, ParserException, Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException {
    String owner = "David";
    String description = "This is important meeting";
    String begin = "10/10/2025 10:30 AM";
    String end = "10/10/2025 12:30 PM";
    AppointmentBook appointmentBook = new AppointmentBook(owner);
    appointmentBook.addAppointment(new Appointment(description, begin, end));

    AppointmentBookRestClient client = newAppointmentBookRestClient();
    client.addAppointment(owner, description, begin, end);

    AppointmentBook returnedApptBook = client.getAppointments(owner);
    assertThat(returnedApptBook, equalTo(appointmentBook));
  }

  @Test
  void test2EmptyOwnerNameThrowException() {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    RestException exception = assertThrows(RestException.class, () -> client.getAppointments(""));
    assertThat(exception.getHttpStatusCode(), equalTo(412));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code 412: The required parameter \"owner\" is missing"));
  }

  @Test
  void test3GetNotFoundOwnerShouldThrowException() {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    RestException exception = assertThrows(RestException.class, () -> client.getAppointments("Annie"));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code 404: " + String.format("Owner %s does not exist.", "Annie")));
  }

  @Test
  void test4CreateEmptyOwnerShouldThrowException() {
    String owner = "David";
    String description = "This is important meeting";
    String begin = "10/10/2025 10:30 AM";
    String end = "10/10/2025 12:30 PM";

    AppointmentBookRestClient client = newAppointmentBookRestClient();
    RestException exception = assertThrows(RestException.class, () -> client.addAppointment("", description, begin, end));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code 412: " + Messages.missingRequiredParameter("owner")));

    RestException exception1 = assertThrows(RestException.class, () -> client.addAppointment(owner, "", begin, end));
    assertThat(exception1.getMessage(), equalTo("HTTP Status Code 412: " + Messages.missingRequiredParameter("description")));

    RestException exception2 = assertThrows(RestException.class, () -> client.addAppointment(owner, description, "", end));
    assertThat(exception2.getMessage(), equalTo("HTTP Status Code 412: " + Messages.missingRequiredParameter("begin")));

    RestException exception3 = assertThrows(RestException.class, () -> client.addAppointment(owner, description, begin, ""));
    assertThat(exception3.getMessage(), equalTo("HTTP Status Code 412: " + Messages.missingRequiredParameter("end")));
  }

  @Test
  void test5InvalidTimeShouldThrowException() {
    String owner = "David";
    String description = "This is important meeting";
    String begin = "10/10/20 10:30 AM";
    String end = "10/10/2025 12:30 PM";

    AppointmentBookRestClient client = newAppointmentBookRestClient();
    RestException exception = assertThrows(RestException.class, () -> client.addAppointment(owner, description, begin, end));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code 412: " + "Invalid input time. Time should be formatted as M/d/yyyy h:mm a"));

    RestException exception1 = assertThrows(RestException.class, () -> client.addAppointment(owner, description, "10/10/2025 10:30 AM", "10/10/2025 9:30 AM"));
    assertThat(exception1.getMessage(), equalTo("HTTP Status Code 412: " + "Invalid Input. End date time must be after begin date time!"));

  }

  @Test
  void test6GetTimeRangeShouldReturnExpectedAppointments() throws Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException, ParserException, IOException {
    String owner = "David";
    String begin = "10/10/2025 10:30 AM";
    String end = "10/10/2025 12:30 PM";

    AppointmentBook apptBook = new AppointmentBook(owner);
    apptBook.addAppointment(new Appointment("This is important meeting", begin, end));

    AppointmentBookRestClient client = newAppointmentBookRestClient();
    String beginTime = "10/10/2025 0:01 AM";
    String endTime = "12/12/2025 11:59 PM";
    AppointmentBook returnedApptBook = client.getAppointmentsInTimeRange(owner, beginTime, endTime);

    assertThat(returnedApptBook, equalTo(apptBook));
  }


}
