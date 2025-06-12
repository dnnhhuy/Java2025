package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AbstractAppointment;

import java.time.LocalDateTime;

/**
 * The class represents a <code>Appoinment</code>
 */
public class Appointment extends AbstractAppointment {

  public Appointment(){};

  private String description;
  private LocalDateTime beginTime;
  private LocalDateTime endTime;
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

  @Override
  public String getBeginTimeString() {
    return this.beginTime.toString();
  }

  @Override
  public String getEndTimeString() {
    return this.endTime.toString();
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  public static class InvalidAppointmentTimeException extends Throwable {
    public InvalidAppointmentTimeException(String s) {
      super(s);
    }
  }
}
