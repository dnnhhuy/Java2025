package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AbstractAppointmentBook;

import java.util.*;

/**
 * This class represents <code>AppointmentBook</code>
 * */
public class AppointmentBook extends AbstractAppointmentBook<Appointment> {
  private final String owner;
  ArrayList<Appointment> book;
  /**
   * Create a new <code>AppointmentBook</code>
   *
   * */
  public AppointmentBook(String owner) {
    this.owner = owner;
    this.book = new ArrayList<>();
  }

  @Override
  public String getOwnerName() {
    return this.owner;
  }

  @Override
  public Collection<Appointment> getAppointments() {
    return this.book;
  }

  @Override
  public void addAppointment(Appointment appt) {
    if (appt == null) {
      throw new NullAppointmentException("You are trying to adding null appointment!");
    }
    this.book.add(appt);
    Collections.sort(this.book);
  }

  public static class NullAppointmentException extends RuntimeException {
    public NullAppointmentException(String s) {
      super(s);
    }
  }
}
