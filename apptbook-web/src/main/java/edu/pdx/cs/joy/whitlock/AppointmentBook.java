package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AbstractAppointmentBook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof AppointmentBook)) {
      return false;
    }
    AppointmentBook otherAppontmentBook = (AppointmentBook) other;

    if (this.owner.equals(otherAppontmentBook.getOwnerName())) {
      ArrayList<Appointment> thisAppointments = (ArrayList<Appointment>) this.getAppointments();
      ArrayList<Appointment> otherAppointments = (ArrayList<Appointment>) otherAppontmentBook.getAppointments();
      if (this.getAppointments().size() != otherAppontmentBook.getAppointments().size()) return false;
      for (int i = 0; i < this.getAppointments().size(); i++) {
        if (!thisAppointments.get(i).getDescription().equals(otherAppointments.get(i).getDescription())) return false;
        if (!thisAppointments.get(i).getBeginTime().equals(otherAppointments.get(i).getBeginTime())) return false;
        if (!thisAppointments.get(i).getEndTime().equals(otherAppointments.get(i).getEndTime())) return false;
      }
      return true;
    } else {
      return false;
    }

  }
  public static class NullAppointmentException extends RuntimeException {
    public NullAppointmentException(String s) {
      super(s);
    }
  }
}
