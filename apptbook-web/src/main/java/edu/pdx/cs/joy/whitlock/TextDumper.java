package edu.pdx.cs.joy.whitlock;

import javax.xml.crypto.Data;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TextDumper {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }


  public void dump(AppointmentBook appointmentBook) {
    try (
      PrintWriter pw = new PrintWriter(this.writer);
    ) {
      pw.println(appointmentBook.getOwnerName() + ":");
      for (Appointment appt : appointmentBook.getAppointments()) {
        pw.println(appt.getDescription() + "; " + appt.getBeginTimeString() + "; " + appt.getEndTimeString());
      }
      pw.println();
      pw.flush();
    }
  }

  public void dump(AppointmentBook appointmentBook, String begin, String end) throws DateTimeParseException, IllegalTimeRangeException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
    LocalDateTime beginTime;
    LocalDateTime endTime;
    if (!begin.equals("")) {
      try {
        beginTime = LocalDateTime.parse(begin, formatter);
      } catch (DateTimeParseException ex) {
        String error = ex.getMessage();
        throw ex;
      }
    } else {
      beginTime = LocalDateTime.MIN;
    }

    if (!end.equals("")) {
      try {
        endTime = LocalDateTime.parse(end, formatter);
      } catch (DateTimeParseException ex) {
        throw ex;
      }
    } else {
      endTime = LocalDateTime.MAX;
    }

    if(endTime.isBefore(beginTime)) {
      throw new IllegalTimeRangeException("End time must be after or equal begin time");
    }
      Stream<Appointment> filteredAppointmentBook = appointmentBook.getAppointments()
            .stream()
            .filter(appointment -> (appointment.getBeginTime().isEqual(beginTime) || appointment.getBeginTime().isAfter(beginTime)
                    && (appointment.getEndTime().isEqual(endTime) || appointment.getEndTime().isBefore(endTime))
            ));

    try (PrintWriter pw = new PrintWriter(this.writer)) {
      pw.println(appointmentBook.getOwnerName() + ":");
      for (Appointment appt : filteredAppointmentBook.toList()) {
        pw.println(appt.getDescription() +  "; " + appt.getBeginTimeString() + "; " + appt.getEndTimeString());
      }
      pw.println();
      pw.flush();
    }
  }


  public static class IllegalTimeRangeException extends Throwable {
    public IllegalTimeRangeException(String s) {
      super(s);
    }
  }
}
