package edu.pdx.cs.joy.whitlock;

import com.google.common.annotations.VisibleForTesting;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.MissingResourceException;

/**
 * The main class for the Appointment Book Project
 */
public class Project1 {

  @VisibleForTesting
  static boolean isValidDateAndTime(String dateAndTime) {
    try {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
      LocalDateTime.parse(dateAndTime, dateTimeFormatter);
    } catch (DateTimeParseException ex) {
      return false;
    }
    return true;
  }

  public static String getDescriptionMessage() {
      return """
                usage: java -jar target/apptbook-1.0.0.jar [options] <args>
                  args are (in this order):
                    owner
                    description
                    begin
                    end
                  options are (options may appear in any order):
                    -print
                    -README
                  Date and time should be in the format: mm/dd/yyyy hh:mm
                """;
  }

  public static void main(String[] args) {

    if (args.length == 0) {
      System.err.println("Missing command line arguments");
      System.out.print(getDescriptionMessage() );
    } else if (args.length == 1){
      System.err.println("Missing description argument");
      System.out.print(getDescriptionMessage());
    } else if (args.length == 2) {
      System.err.println("Missing appointment's begin date argument");
      System.out.print(getDescriptionMessage());
    } else if (args.length == 3) {
      System.err.println("Missing appointment's begin time argument");
      System.out.print(getDescriptionMessage());
    } else if (args.length == 4) {
      System.err.println("Missing appointment's end date argument");
      System.out.print(getDescriptionMessage());
    } else if (args.length == 5) {
      System.err.println("Missing appointment's end time argument");
      System.out.print(getDescriptionMessage());
    }else {
      String owner = args[0];
      String description = args[1];
      LocalDateTime beginTime;
      LocalDateTime endTime;
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
      boolean print = false;
      boolean printReadme = false;

      for (int i = 6; i < args.length; i++) {
        if (args[i].equals("-print")) {
          print = true;
        } else if (args[i].equals("-README")) {
          printReadme = true;
        } else {
          System.err.println("Unexpected Argument.");
          return;
        }
      }

      String beginDateTime = args[2] + " " + args[3];
      String endDateTime = args[4] + " " + args[5];

      if (isValidDateAndTime(beginDateTime)) {
        beginTime = LocalDateTime.parse(beginDateTime, dateTimeFormatter);
      } else {
        System.err.println("Invalid input time. Time should be formatted as MM/dd/yyyy HH:mm");
        return;
      }

      if (isValidDateAndTime(endDateTime)) {
        endTime = LocalDateTime.parse(endDateTime, dateTimeFormatter);
      } else {
        System.err.println("Invalid input time. Time should be formatted as MM/dd/yyyy HH:mm");
        return;
      }


      AppointmentBook appointmentBook = new AppointmentBook(owner);
      Appointment appointment = null;
      try {
        appointment = new Appointment(description, beginTime, endTime);  // Refer to one of Dave's classes so that we can be sure it is on the classpath
      } catch (Appointment.InvalidAppointmentTimeException ex) {
        System.err.println(ex.getMessage());
        return;
      }

      try {
        appointmentBook.addAppointment(appointment);
      } catch (AppointmentBook.NullAppointmentException ex) {
        System.err.println(ex.getMessage());
        return;
      }
      if (print) {
        System.out.printf("Appointment:\n" + "Owner: %s\n" + "Description: %s\n" + "Time: %s - %s\n",
                owner, description, beginTime.format(dateTimeFormatter), endTime.format(dateTimeFormatter));
        System.out.println("Added appointment successfully!");
      }
      if (printReadme) {
        try (InputStream readme = Project1.class.getResourceAsStream("README.txt")) {
          assert readme != null;
          BufferedReader reader = new BufferedReader(new InputStreamReader(readme));
          String line = reader.readLine();
          System.out.println(line);
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      }
    }
   }
}