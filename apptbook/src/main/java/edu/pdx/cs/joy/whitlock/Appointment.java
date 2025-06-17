package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AbstractAppointment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The class represents a <code>Appoinment</code>
 */
public class Appointment extends AbstractAppointment {

  public Appointment(){};

  private String description;
  private LocalDateTime beginTime;
  private LocalDateTime endTime;
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:m");
  /**
   * Create a new <code>Appointment</code>
   * @param description
   *        Description of the appointment
   * @param beginTime
   *        Time the appointment begins
   * @param endTime
   *        Time the appointment ends
   * */
  public Appointment(String description, LocalDateTime beginTime, LocalDateTime  endTime) throws InvalidAppointmentTimeException {
    if (beginTime.isAfter(endTime)) {
      throw new InvalidAppointmentTimeException("Invalid Input. End date time must be after begin date time!");
    }
    this.description = description;
    this.beginTime = beginTime;
    this.endTime = endTime;
  }

  public Appointment(String description, String beginTime, String  endTime) throws InvalidAppointmentTimeException, InvalidDateTimeFormatException {
    this.description = description;
    try {
      this.beginTime = LocalDateTime.parse(beginTime, formatter);
      this.endTime = LocalDateTime.parse(endTime, formatter);
    } catch (DateTimeParseException exception) {
      throw new InvalidDateTimeFormatException("Invalid input time. Time should be formatted as M/d/yyyy H:m");
    }
    if (this.beginTime.isAfter(this.endTime)) {
      throw new InvalidAppointmentTimeException("Invalid Input. End date time must be after begin date time!");
    }
  }

  @Override
  public String getBeginTimeString() {
    return this.formatter.format(this.beginTime);
  }

  @Override
  public String getEndTimeString() {
    return this.formatter.format(this.endTime);
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  public static class InvalidDateTimeFormatException extends Throwable {
    public InvalidDateTimeFormatException(String s) {
      super(s);
    }
  }
  public static class InvalidAppointmentTimeException extends Throwable {
    public InvalidAppointmentTimeException(String s) {
      super(s);
    }
  }
}
