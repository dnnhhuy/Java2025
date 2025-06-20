package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AbstractAppointment;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

/**
 * The class represents a <code>Appoinment</code>
 */
public class Appointment extends AbstractAppointment implements Comparable<Appointment> {

  public Appointment(){};

  private String description;
  private LocalDateTime beginTime;
  private LocalDateTime endTime;
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm a");
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
      this.beginTime = LocalDateTime.parse(beginTime, this.formatter);
      this.endTime = LocalDateTime.parse(endTime, this.formatter);
    } catch (DateTimeParseException exception) {
      throw new InvalidDateTimeFormatException("Invalid input time. Time should be formatted as M/d/yyyy H:mm a");
    }
    if (this.beginTime.isAfter(this.endTime)) {
      throw new InvalidAppointmentTimeException("Invalid Input. End date time must be after begin date time!");
    }
  }

  @Override
  public LocalDateTime getBeginTime() {
    return this.beginTime;
  }

  @Override
  public LocalDateTime getEndTime() {
    return this.endTime;
  }
  @Override
  public String getBeginTimeString() {
    DateTimeFormatter formatter1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    return formatter1.format(this.beginTime);
  }

  @Override
  public String getEndTimeString() {
    DateTimeFormatter formatter1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    return formatter1.format(this.endTime);

  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public int compareTo(Appointment o) {
    if (this.beginTime.equals(o.getBeginTime())) {
      if (this.endTime.equals(o.getEndTime())) {
        return this.description.compareTo(o.getDescription());
      } else {
        return this.endTime.compareTo(o.getEndTime());
      }
    } else {
      return this.beginTime.compareTo(o.getBeginTime());
    }
  }

  public boolean equals(Object obj) {
    if (obj instanceof Appointment) {
      return this.getBeginTime().equals(((Appointment) obj).getBeginTime()) && this.endTime.equals(((Appointment) obj).getEndTime()) && this.description.equals(((Appointment) obj).getDescription());
    }
    return false;
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
