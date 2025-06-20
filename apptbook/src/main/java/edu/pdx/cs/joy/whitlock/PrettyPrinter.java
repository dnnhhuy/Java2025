package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AppointmentBookDumper;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PrettyPrinter implements AppointmentBookDumper<AppointmentBook> {
    private final Writer writer;

    public PrettyPrinter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void dump(AppointmentBook appointmentBook) {
        try (PrintWriter pw = new PrintWriter(this.writer)) {
            pw.write(String.format("Owner: %s\n", appointmentBook.getOwnerName()));
            for (Appointment appointment : appointmentBook.getAppointments()) {
                pw.write(String.format("Description: %s\n", appointment.getDescription()));
                pw.write(String.format("Start at: %s\n", appointment.getBeginTimeString()));
                pw.write(String.format("End at: %s\n", appointment.getEndTimeString()));

                LocalDateTime startTime = appointment.getBeginTime();
                LocalDateTime endTime = appointment.getEndTime();
                long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
                long days = seconds / (24 * 60 * 60);
                long remain = seconds % (24 * 60 * 60);
                long hours = remain / (60 * 60);
                remain = remain % (60 * 60);
                long minutes = remain / 60;
                seconds = remain % 60;

                pw.write(String.format("Duration: %d days %d hours %d minutes %d seconds\n", days, hours, minutes, seconds));
                pw.write("\n");
            }
            pw.flush();
        }
    }
}
