package edu.pdx.cs.joy.whitlock;

import com.google.common.annotations.VisibleForTesting;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class PrettyPrinter {
  private final Writer writer;

  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  public void dump(AppointmentBook appointmentBook) {
    try (PrintWriter pw = new PrintWriter(this.writer)) {
      pw.printf("%s has %d appointments:\n", appointmentBook.getOwnerName(), appointmentBook.getAppointments().size());
      for (Appointment appt : appointmentBook.getAppointments()) {
        pw.println("Description: " + appt.getDescription());
        pw.println("Start at: " + appt.getBeginTimeString());
        pw.println("End at: " + appt.getEndTimeString());

        LocalDateTime startTime = appt.getBeginTime();
        LocalDateTime endTime = appt.getEndTime();
        long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
        long days = seconds / (24 * 60 * 60);
        long remain = seconds % (24 * 60 * 60);
        long hours = remain / (60 * 60);
        remain = remain % (60 * 60);
        long minutes = remain / 60;
        seconds = remain % 60;

        pw.printf("Duration: %d days %d hours %d minutes %d seconds\n", days, hours, minutes, seconds);

        pw.println();
      }
    }
  }
}
