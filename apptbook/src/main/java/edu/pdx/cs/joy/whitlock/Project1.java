package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * The main class for the Appointment Book Project
 */
public class Project1 {

    public static String getDescriptionMessage() {
        return """
                usage: java -jar target/apptbook-1.0.0.jar [options] <args>
                  args are (in this order):
                    owner               The person whose owns the appt book
                    description         A description of the appointment
                    begin               When the appt begins (24-hour time)
                    end                 When the appt ends (24-hour time)
                  options are (options may appear in any order):
                    -textFile File      Where to read/write the appointment book
                    -print              Prints a description of the new appointment
                    -README             Prints a README for this project and exits
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
            boolean print = false;
            boolean printReadme = false;
            boolean textFile = false;
            String filePath = "";
            for (int i = 6; i < args.length; i++) {
                if (args[i].equals("-print")) {
                    print = true;
                } else if (args[i].equals("-README")) {
                    printReadme = true;
                } else if (args[i].equals("-textFile")){
                    textFile = true;
                    filePath = args[i + 1];
                    i++;
                } else {
                    System.err.println("Unexpected Argument.");
                    return;
                }
            }

            AppointmentBook appointmentBook = null;
            if (textFile) {
                File inputFile = new File(filePath);
                try {
                    boolean createdFile = inputFile.createNewFile();
                    if (!createdFile) {
                        TextParser textParser = null;
                        try {
                            textParser = new TextParser(new FileReader(inputFile));
                            appointmentBook = textParser.parse();
                        } catch (FileNotFoundException ex) {
                            System.err.println("Something wrong");
                            return;
                        } catch (ParserException e) {
                            System.err.println(e.getMessage());
                            return;
                        }

                        if (!appointmentBook.getOwnerName().equals(owner)) {
                            System.err.println("Input name in the argument is not the same as in the given text file.");
                            return;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Cannot create file");
                    return;
                }
            }
            String beginDateTime = args[2] + " " + args[3];
            String endDateTime = args[4] + " " + args[5];

            Appointment appointment = null;
            try {
                appointment = new Appointment(description, beginDateTime, endDateTime);  // Refer to one of Dave's classes so that we can be sure it is on the classpath
            } catch (Appointment.InvalidDateTimeFormatException | Appointment.InvalidAppointmentTimeException exception){
                System.err.println(exception.getMessage());
                return;
            }

            try {
                if (appointmentBook == null) {
                    appointmentBook = new AppointmentBook(owner);
                }
                appointmentBook.addAppointment(appointment);
            } catch (AppointmentBook.NullAppointmentException ex) {
                System.err.println(ex.getMessage());
                return;
            }
            if (print) {
                System.out.printf("Appointment:\n" + "Owner: %s\n" + "Description: %s\n" + "Time: %s - %s\n",
                        owner, description, beginDateTime, beginDateTime);
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
            if (textFile) {
                try {
                    TextDumper textDumper = new TextDumper(new FileWriter(filePath));
                    textDumper.dump(appointmentBook);
                } catch (IOException e) {
                    System.err.println("Something went wrong");
                }

            }
        }
    }
}