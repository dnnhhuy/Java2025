package edu.pdx.cs.joy.whitlock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * A unit test for the {@link AppointmentBookServlet}.  It uses mockito to
 * provide mock http requests and responses.
 */
public class AppointmentBookServletTest {

  @Test
  void missingOwnerParameter() throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter pw = mock(PrintWriter.class);

    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);

    // Nothing is written to the response's PrintWriter
    verify(pw, never()).println(anyString());
    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter("owner"));
  }

  @Test
  void getNotExistOwnerThrowsError() throws IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn("David");
    servlet.doGet(request, response);

    verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Owner David does not exist.");

  }
  @Disabled
  @Test
  void addOneWordToDictionary() throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    String word = "TEST WORD";
    String definition = "TEST DEFINITION";

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(AppointmentBookServlet.WORD_PARAMETER)).thenReturn(word);
    when(request.getParameter(AppointmentBookServlet.DEFINITION_PARAMETER)).thenReturn(definition);

    HttpServletResponse response = mock(HttpServletResponse.class);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);

    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    assertThat(stringWriter.toString(), containsString(Messages.definedWordAs(word, definition)));

    // Use an ArgumentCaptor when you want to make multiple assertions against the value passed to the mock
    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).setStatus(statusCode.capture());

    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));

    assertThat(servlet.getDefinition(word), equalTo(definition));
  }

  @Test
  void addOneAppointmentToAppointmentBook() throws IOException, Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException {
    String owner = "David";
    String description = "This meeting is important";
    String beginTime = "12/10/2025 10:30 AM";
    String endTime =  "12/10/2025 12:30 PM";
    AppointmentBook appointmentBook = new AppointmentBook(owner);
    appointmentBook.addAppointment(new Appointment(description, beginTime, endTime));


    AppointmentBookServlet servlet = new AppointmentBookServlet();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(owner);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER)).thenReturn(description);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn(beginTime);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_END_PARAMETER)).thenReturn(endTime);

    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    assertThat(stringWriter.toString(), equalTo(Messages.addedAppointmentToAppointmentBook(owner, description, beginTime, endTime)));

    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).setStatus(statusCode.capture());

    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
    assertThat(servlet.getAppointmentBook(owner).equals(appointmentBook), equalTo(true));
  }

  @Test
  void missingPostParameterReturnErrorStatusCode() throws IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    servlet.doPost(request, response);
    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter("owner"));

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn("David");
    servlet.doPost(request, response);
    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter("description"));

    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER)).thenReturn("This is important meeting");
    servlet.doPost(request, response);
    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter("begin"));

    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn("12/10/2025 10:30 PM");
    servlet.doPost(request, response);
    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter("end"));
  }


  @Test
  void invalidTimeArgumentReturnErrorStatusCode() throws IOException {
    String owner = "David";
    String description = "This meeting is important";
    String beginTime = "12/10/2025 10:30 AM";
    String endTime =  "12/10/2025 9:30 AM";

    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(owner);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER)).thenReturn(description);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn("12/10/25 10:30 AM");
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_END_PARAMETER)).thenReturn(endTime);

    servlet.doPost(request, response);
    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).sendError(statusCode.capture(), eq("Invalid input time. Time should be formatted as M/d/yyyy h:mm a"));
    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_PRECONDITION_FAILED));


    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn(beginTime);
    servlet.doPost(request, response);
    verify(response).sendError(statusCode.capture(), eq("Invalid Input. End date time must be after begin date time!"));
    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_PRECONDITION_FAILED));
  }

  @Test
  void getExpectedAppointments() throws IOException, Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException {
    String beginTime = "10/10/2025 0:01 AM";
    String endTime = "12/12/2025 11:59 PM";

    AppointmentBook apptBook = new AppointmentBook("David");
    apptBook.addAppointment(new Appointment("This is important meeting", "10/10/2025 9:30 AM", "10/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "11/10/2025 9:30 AM", "11/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "9/10/2025 9:30 AM", "9/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "8/10/2025 9:30 AM", "8/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "12/10/2025 9:30 AM", "12/10/2025 12:30 PM"));


    AppointmentBookServlet servlet = spy(new AppointmentBookServlet());

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn("David");
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn(beginTime);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_END_PARAMETER)).thenReturn(endTime);

    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(pw);
    when(servlet.getAppointmentBook("David")).thenReturn(apptBook);

    servlet.doGet(request, response);


    String expectedString = "David:\n" +
            "This is important meeting; 10/10/25, 9:30 AM; 10/10/25, 12:30 PM\n" +
            "This is important meeting; 11/10/25, 9:30 AM; 11/10/25, 12:30 PM\n" +
            "This is important meeting; 12/10/25, 9:30 AM; 12/10/25, 12:30 PM";

    assertThat(stringWriter.toString().trim(), equalTo(expectedString));

    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  void getAppointmentGivenTimeRangeButEndTimeBeforeBeginTimeShouldThrowException() throws IOException, Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException {
    AppointmentBookServlet servlet = spy(new AppointmentBookServlet());
    String owner = "David";
    String beginTime = "10/10/2025 9:30 AM";
    String endTime = "9/12/2025 11:59 PM";

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(owner);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn(beginTime);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_END_PARAMETER)).thenReturn(endTime);

    AppointmentBook apptBook = new AppointmentBook("David");
    apptBook.addAppointment(new Appointment("This is important meeting", "10/10/2025 9:30 AM", "10/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "11/10/2025 9:30 AM", "11/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "9/10/2025 9:30 AM", "9/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "8/10/2025 9:30 AM", "8/10/2025 12:30 PM"));
    apptBook.addAppointment(new Appointment("This is important meeting", "12/10/2025 9:30 AM", "12/10/2025 12:30 PM"));

    when(servlet.getAppointmentBook(owner)).thenReturn(apptBook);
    servlet.doGet(request, response);
    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "End time must be after or equal begin time");

  }

  @Test
  void deleteGivenNoArgumentShouldReturnErrorStatusCode() throws IOException {
    AppointmentBookServlet servlet = spy(new AppointmentBookServlet());

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(null);
    when(request.getParameter("delete")).thenReturn("true");

    servlet.doPost(request, response);

    verify(response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter(AppointmentBookServlet.OWNER_PARAMETER));

  }

  @Test
  void deleteNotFoundOwnerShouldRespondErrorStatusCode() throws IOException {
    String owner = "David";
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(owner);
    when(request.getParameter("delete")).thenReturn("true");


    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter);

    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_OK);
    assertThat(stringWriter.toString().trim(), equalTo(String.format("%s's appointment book does not exist", owner)));
  }

  @Test
  void deleteFoundOwnerShouldDeleteOwnerFromTheServerAndReturn200StatusCode() throws IOException {
    // Setup
    String owner = "David";
    String description = "Important";
    String beginTime = "10/10/2025 9:30 AM";
    String endTime = "10/10/2025 12:30 PM";
    AppointmentBook apptBook = new AppointmentBook(owner);

    Map<String, AppointmentBook> storage = new HashMap<>();
    storage.put(owner, apptBook);


    AppointmentBookServlet servlet = new AppointmentBookServlet();


    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(owner);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_DESCRIPTION_PARAMETER)).thenReturn(description);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_BEGIN_PARAMETER)).thenReturn(beginTime);
    when(request.getParameter(AppointmentBookServlet.APPOINTMENT_END_PARAMETER)).thenReturn(endTime);

    PrintWriter pw = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_OK);

    // Test
    when(request.getParameter(AppointmentBookServlet.OWNER_PARAMETER)).thenReturn(owner);
    when(request.getParameter("delete")).thenReturn("true");

    StringWriter stringWriter = new StringWriter();
    pw = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    assertThat(stringWriter.toString().trim(), equalTo(Messages.allAppoinmentDeleted(owner)));
    verify(response, times(2)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  void deleteAllAppointmentBooksOnTheServer() throws IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(pw);

    servlet.doDelete(request, response);

    assertThat(stringWriter.toString().trim(), equalTo("All appointment books have been deleted"));

    verify(response).setStatus(HttpServletResponse.SC_OK);

  }

}
