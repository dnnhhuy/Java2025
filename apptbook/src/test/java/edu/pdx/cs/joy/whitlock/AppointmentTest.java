package edu.pdx.cs.joy.whitlock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link Appointment} class.
 *
 * You'll need to update these unit tests as you build out your program.
 */
public class AppointmentTest {

  /**
   * This unit test will need to be modified (likely deleted) as you implement
   * your project.
   */
  @Disabled
  @Test
  void getBeginTimeStringNeedsToBeImplemented() {
    Appointment appointment = new Appointment();
    assertThrows(UnsupportedOperationException.class, appointment::getBeginTimeString);
  }

  /**
   * This unit test will need to be modified (likely deleted) as you implement
   * your project.
   */
  @Disabled
  @Test
  void initiallyAllAppointmentsHaveTheSameDescription() {
    Appointment appointment = new Appointment();
    assertThat(appointment.getDescription(), containsString("not implemented"));
  }

  @Test
  void forProject1ItIsOkayIfGetBeginTimeReturnsNull() {
    Appointment appointment = new Appointment();
    assertThat(appointment.getBeginTime(), is(nullValue()));
  }

  @Test
  void descriptionShouldBeEqualToGetDescription() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    LocalDateTime begin = LocalDateTime.of(2025, 5, 11, 12, 30);
    begin.format(formatter);

    LocalDateTime end = begin.plusDays(3);
    Appointment appointment = null;
    try {
      appointment = new Appointment("This is important meeting!", begin, end);
    } catch (Appointment.InvalidAppointmentTimeException ex) {
      return;
    }
    assertThat(appointment.getDescription(), equalTo("This is important meeting!"));
  }

  @Test
  void endTimeBeforeBeginTimeThrowInvalidException() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    LocalDateTime begin = LocalDateTime.of(2025, 5, 11, 12, 30);
    begin.format(formatter);

    LocalDateTime end = begin.minusDays(3);
    Appointment.InvalidAppointmentTimeException exception = assertThrows(Appointment.InvalidAppointmentTimeException.class, () -> new Appointment("This is important meeting!", begin, end));
    assertThat(exception.getMessage(), equalTo("Invalid Input. End date time must be after begin date time!"));
  }

  @Test
  void addingNullAppointmentShouldThrowException() {
    Appointment appt = null;
    AppointmentBook appointmentBook = new AppointmentBook("david");
    AppointmentBook.NullAppointmentException exception = assertThrows(AppointmentBook.NullAppointmentException.class, () -> appointmentBook.addAppointment(appt));
    assertThat(exception.getMessage(), equalTo("You are trying to adding null appointment!"));
  }

  @Test
  void addAppointmentShouldIncreaseBookSize() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    LocalDateTime begin = LocalDateTime.of(2025, 5, 11, 12, 30);
    begin.format(formatter);

    LocalDateTime end = begin.plusDays(3);
    Appointment appointment = null;
    AppointmentBook apptBook = new AppointmentBook("David");
    try {
      appointment = new Appointment("This is important meeting!", begin, end);
    } catch (Appointment.InvalidAppointmentTimeException ex) {
      return;
    }

    int initial_size = apptBook.getAppointments().size();
    apptBook.addAppointment(appointment);
    int added_size = apptBook.getAppointments().size();
    assertThat(added_size, equalTo(initial_size + 1));
  }

  @Test
  void appointmentShouldBeSortedBasedOnBeginTime() throws Appointment.InvalidAppointmentTimeException {
    String default_description = "";
    LocalDateTime begin1 = LocalDateTime.of(2025, 5, 11, 12, 30);
    LocalDateTime begin2 = LocalDateTime.of(2025, 5, 12, 12, 30);

    LocalDateTime end1 = begin1.plusDays(2);
    LocalDateTime end2 = begin2.plusDays(2);

    Appointment appointment1 = new Appointment(default_description, begin1, end1);
    Appointment appointment2 = new Appointment(default_description, begin2, end2);

    AppointmentBook appointmentBook = new AppointmentBook("owner");
    appointmentBook.addAppointment(appointment2);
    appointmentBook.addAppointment(appointment1);

    ArrayList<Appointment> list = (ArrayList<Appointment>) appointmentBook.getAppointments();
    assertThat(list.get(0), equalToObject(appointment1));
    assertThat(list.get(1), equalToObject(appointment2));
  }

  @Test
  void equalBeginTimeShouldSortByEndTime() throws Appointment.InvalidAppointmentTimeException {
    String default_description = "";
    LocalDateTime begin1 = LocalDateTime.of(2025, 5, 11, 12, 30);
    LocalDateTime begin2 = LocalDateTime.of(2025, 5, 11, 12, 30);

    LocalDateTime end1 = begin1.plusHours(2);
    LocalDateTime end2 = begin2.plusHours(3);

    Appointment appointment1 = new Appointment(default_description, begin1, end1);
    Appointment appointment2 = new Appointment(default_description, begin2, end2);

    AppointmentBook appointmentBook = new AppointmentBook("owner");
    appointmentBook.addAppointment(appointment2);
    appointmentBook.addAppointment(appointment1);

    ArrayList<Appointment> list = (ArrayList<Appointment>) appointmentBook.getAppointments();
    assertThat(list.get(0), equalToObject(appointment1));
    assertThat(list.get(1), equalToObject(appointment2));

  }

  @Test
  void sameBeginAndEndTimeShouldSortByDescription() throws Appointment.InvalidAppointmentTimeException {
      String description1 = "abc";
      String description2 = "bcd";
      LocalDateTime begin1 = LocalDateTime.of(2025, 5, 11, 12, 30);
      LocalDateTime begin2 = LocalDateTime.of(2025, 5, 11, 12, 30);

      LocalDateTime end1 = begin1.plusHours(2);
      LocalDateTime end2 = begin2.plusHours(2);

      Appointment appointment1 = new Appointment(description1, begin1, end1);
      Appointment appointment2 = new Appointment(description2, begin2, end2);

      AppointmentBook appointmentBook = new AppointmentBook("owner");
      appointmentBook.addAppointment(appointment2);
      appointmentBook.addAppointment(appointment1);

      ArrayList<Appointment> list = (ArrayList<Appointment>) appointmentBook.getAppointments();
      assertThat(list.get(0), equalToObject(appointment1));
      assertThat(list.get(1), equalToObject(appointment2));
    }

    @Test
    void appointmentPrintTimeStringShouldBeInCorrectFormat() throws Appointment.InvalidAppointmentTimeException {
      String description1 = "bcd";
      DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
      LocalDateTime begin1 = LocalDateTime.of(2025, 5, 11, 12, 30);
      LocalDateTime end1 = begin1.plusHours(2);

      Appointment appointment1 = new Appointment(description1, begin1, end1);
      assertThat(appointment1.getBeginTimeString(), equalTo(formatter.format(begin1)));
      assertThat(appointment1.getEndTimeString(), equalTo(formatter.format(end1)));
    }

    @Test
    void prettyPrinterShouldPrintExpectedFormat() throws Appointment.InvalidAppointmentTimeException {
      String description1 = "abc";
      String description2 = "bcd";
      LocalDateTime begin1 = LocalDateTime.of(2025, 5, 11, 12, 30);
      LocalDateTime begin2 = LocalDateTime.of(2025, 5, 12, 12, 30);

      LocalDateTime end1 = begin1.plusDays(2).minusHours(2);
      LocalDateTime end2 = begin2.plusHours(2);


      Appointment appointment1 = new Appointment(description1, begin1, end1);
      Appointment appointment2 = new Appointment(description2, begin2, end2);

      AppointmentBook appointmentBook = new AppointmentBook("owner");
      appointmentBook.addAppointment(appointment2);
      appointmentBook.addAppointment(appointment1);

      String expectedContent = String.format("""
              Owner: owner
              Description: abc
              Start at: 5/11/25, 12:30 PM
              End at: 5/13/25, 10:30 AM
              Duration: 1 days 22 hours 0 minutes 0 seconds
              
              Description: bcd
              Start at: 5/12/25, 12:30 PM
              End at: 5/12/25, 2:30 PM
              Duration: 0 days 2 hours 0 minutes 0 seconds
              
              """);

      StringWriter stringWriter = new StringWriter();
      PrettyPrinter prettyPrinter = new PrettyPrinter(stringWriter);
      prettyPrinter.dump(appointmentBook);

      assertThat(stringWriter.toString(), equalTo(expectedContent));
    }


}
