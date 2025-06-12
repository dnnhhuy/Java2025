package edu.pdx.cs.joy.whitlock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

}
