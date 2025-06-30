package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.module.ResolutionException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AppointmentBookRestClientTest {

  @Test
  void returnErrorStatusCodeWhenPassingEmptyOwner() throws IOException, ParserException {
    Map<String, String> parameters = Map.of(AppointmentBookServlet.OWNER_PARAMETER, "");

    HttpRequestHelper http = mock(HttpRequestHelper.class);

    HttpRequestHelper.Response response = mock(HttpRequestHelper.Response.class);
    when(response.getHttpStatusCode()).thenReturn(412);
    when(response.getContent()).thenReturn("Owner name cannot be empty");

    when(http.get(parameters)).thenReturn(response);

    AppointmentBookRestClient client = new AppointmentBookRestClient(http);
    HttpRequestHelper.RestException exception = assertThrows(HttpRequestHelper.RestException.class, () -> client.getAppointments(""));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code " + exception.getHttpStatusCode() + ": " + "Owner name cannot be empty"));
  }

  @Test
  void getNotFoundOwnerAppointmentsShouldThrowException() throws IOException {
    String owner = "David";

    HttpRequestHelper http = mock(HttpRequestHelper.class);
    HttpRequestHelper.Response response = mock(HttpRequestHelper.Response.class);
    when(response.getHttpStatusCode()).thenReturn(404);
    when(response.getContent()).thenReturn(String.format("Owner %s does not exist.", owner));
    when(http.get(Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner))).thenReturn(response);

    AppointmentBookRestClient client = new AppointmentBookRestClient(http);
    HttpRequestHelper.RestException exception = assertThrows(HttpRequestHelper.RestException.class, () -> client.getAppointments(owner));

    assertThat(exception.getMessage(), equalTo("HTTP Status Code " + exception.getHttpStatusCode() + ": " + String.format("Owner %s does not exist.", owner)));
  }

  @Test
  void getAllAppointmentsGivenOwnerName() throws Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException, IOException, ParserException {
    String owner = "David";
    AppointmentBook apptBook = new AppointmentBook(owner);
    apptBook.addAppointment(new Appointment("This is important meeting", "10/10/2025 9:30 AM", "10/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "11/10/2025 9:30 AM", "11/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "9/10/2025 9:30 AM", "9/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "8/10/2025 9:30 AM", "8/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "12/10/2025 9:30 AM", "12/10/2025 12:30 PM"));

    HttpRequestHelper http = mock(HttpRequestHelper.class);
    when(http.get(Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner))).thenReturn(appointmentAsText(apptBook));

    AppointmentBookRestClient client = new AppointmentBookRestClient(http);
    AppointmentBook returnBook = client.getAppointments(owner);
    assertThat(returnBook.equals(apptBook), equalTo(true));
  }

  @Test
  void getAppointmentInTimeRangeShouldReturnAsExpected() throws Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException, IOException, ParserException {
    String owner = "David";
    AppointmentBook apptBook = new AppointmentBook(owner);
    apptBook.addAppointment(new Appointment("This is important meeting", "10/10/2025 9:30 AM", "10/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "11/10/2025 9:30 AM", "11/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "9/10/2025 9:30 AM", "9/10/2025 12:30 PM"));

    String beginTime = "10/10/2025 0:01 AM";
    String endTime = "12/12/2025 23:59 PM";

    Map<String, String> paramters = Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner, AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER, beginTime, AppointmentBookServlet.APPOINTMENT_END_PARAMETER, endTime);
    HttpRequestHelper http = mock(HttpRequestHelper.class);
    when(http.get(paramters)).thenReturn(appointmentAsText(apptBook));
    
    AppointmentBookRestClient client = new AppointmentBookRestClient(http);
    AppointmentBook responseApptBook = client.getAppointmentsInTimeRange(owner, beginTime, endTime);

    assertThat(responseApptBook, equalTo(apptBook));
  }

  @Test
  void endTimeBeforeBeginTimeShouldThrowException() throws IOException {
    String owner = "David";
    String beginTime = "10/10/2025 0:01 AM";
    String endTime = "9/12/2025 23:59 PM";

    Map<String, String> paramters = Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner, AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER, beginTime, AppointmentBookServlet.APPOINTMENT_END_PARAMETER, endTime);
    HttpRequestHelper http = mock(HttpRequestHelper.class);
    AppointmentBookRestClient client = new AppointmentBookRestClient(http);

    HttpRequestHelper.Response response = mock(HttpRequestHelper.Response.class);
    when(response.getHttpStatusCode()).thenReturn(HttpServletResponse.SC_PRECONDITION_FAILED);
    when(response.getContent()).thenReturn("End time must be before or equal begin time");

    when(http.get(paramters)).thenReturn(response);

    HttpRequestHelper.RestException exception = assertThrows(HttpRequestHelper.RestException.class, () -> client.getAppointmentsInTimeRange(owner, beginTime, endTime));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code " + exception.getHttpStatusCode() + ": " + "End time must be before or equal begin time"));
    assertThat(exception.getHttpStatusCode(), equalTo(HttpServletResponse.SC_PRECONDITION_FAILED));
  }

  @Test
  void addValidAppointmentShouldReturnOkStatusCode() throws IOException {
    String owner = "David";
    String description = "This is important meeting";
    String beginTime = "10/10/2025 10:30 AM";
    String endTime = "10/10/2025 12:30 PM";

    Map<String, String> parameters = Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner, AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER, description, AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER, beginTime, AppointmentBookServlet.APPOINTMENT_END_PARAMETER, endTime);
    HttpRequestHelper http = mock(HttpRequestHelper.class);
    HttpRequestHelper.Response response = mock(HttpRequestHelper.Response.class);
    when(response.getContent()).thenReturn(String.format("Added appointment: %s, %s, %s to %s's appointment book.", description, beginTime, endTime, owner));
    when(response.getHttpStatusCode()).thenReturn(HttpServletResponse.SC_OK);

    when(http.post(parameters)).thenReturn(response);

    AppointmentBookRestClient client = new AppointmentBookRestClient(http);
    client.addAppointment(owner, description, beginTime, endTime);

    verify(http).post(parameters);
    verify(response).getHttpStatusCode();
  }

  @Test
  void errorStatusCodeShouldThrowException() throws IOException {
    String owner = "David";
    String description = "This is important meeting";
    String beginTime = "10/10/2025 10:30 AM";
    String endTime = "10/10/2025 12:30 PM";

    HttpRequestHelper http = mock(HttpRequestHelper.class);
    Map<String, String> parameters = Map.of(AppointmentBookServlet.OWNER_PARAMETER, owner, AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER, description, AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER, beginTime, AppointmentBookServlet.APPOINTMENT_END_PARAMETER, endTime);
    HttpRequestHelper.Response response = mock(HttpRequestHelper.Response.class);
    when(response.getHttpStatusCode()).thenReturn(HttpServletResponse.SC_PRECONDITION_FAILED);
    when(response.getContent()).thenReturn("Response failed!");
    when(http.post(parameters)).thenReturn(response);

    AppointmentBookRestClient client = new AppointmentBookRestClient(http);

    HttpRequestHelper.RestException exception = assertThrows(HttpRequestHelper.RestException.class, () -> client.addAppointment(owner, description, beginTime, endTime));
    assertThat(exception.getHttpStatusCode(), equalTo(HttpServletResponse.SC_PRECONDITION_FAILED));
    assertThat(exception.getMessage(), equalTo("HTTP Status Code " + response.getHttpStatusCode() + ": " + response.getContent()));
    }

  private HttpRequestHelper.Response appointmentAsText(AppointmentBook apptBook) {
    StringWriter writer = new StringWriter();
    new TextDumper(writer).dump(apptBook);

    return new HttpRequestHelper.Response(writer.toString());
  }
}