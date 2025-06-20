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
                    -pretty file        Pretty print the appointment book to
                                        a text file or standard out (file -)
                    -textFile File      Where to read/write the appointment book
                    -print              Prints a description of the new appointment
                    -README             Prints a README for this project and exits
                  Date and time should be in the format: M/d/yyyy h:mm a
                """;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Missing command line arguments");
            System.out.print(getDescriptionMessage());
            return;
        }
        boolean print = false;
        boolean printReadme = false;
        boolean textFile = false;
        boolean prettyPrint = false;
        String prettyPrintFilePath = "";
        String filePath = "";
        int i = 0;
        while (args[i].charAt(0) == '-') {
            if (args[i].equals("-print")) {
                print = true;
            } else if (args[i].equals("-README")) {
                printReadme = true;
            } else if (args[i].equals("-textFile")){
                textFile = true;
                filePath = args[++i];
            } else if (args[i].equals("-pretty")) {
                prettyPrint = true;
                prettyPrintFilePath = args[++i];
            } else {
                System.err.println("Unexpected Argument.");
                return;
            }
            i++;
        }

        if (args.length == i) {
            System.err.println("Missing command line arguments");
            System.out.print(getDescriptionMessage() );
        } else if (args.length == (i + 1)){
            System.err.println("Missing description argument");
            System.out.print(getDescriptionMessage());
        } else if (args.length == (i + 2)) {
            System.err.println("Missing appointment's begin date argument");
            System.out.print(getDescriptionMessage());
        } else if (args.length == (i + 3)) {
            System.err.println("Missing appointment's begin time argument");
            System.out.print(getDescriptionMessage());
        } else if (args.length == (i + 4)) {
            System.err.println("Missing appointment's begin time AM/PM argument");
            System.out.print(getDescriptionMessage());
        }else if (args.length == (i + 5)) {
            System.err.println("Missing appointment's end date argument");
            System.out.print(getDescriptionMessage());
        } else if (args.length == (i + 6)) {
            System.err.println("Missing appointment's end time argument");
            System.out.print(getDescriptionMessage());
        }else if (args.length == (i + 7)) {
            System.err.println("Missing appointment's end time AM/PM argument");
            System.out.print(getDescriptionMessage());
        }else {
            String owner = args[i];
            String description = args[i+1];


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
            String beginDateTime = args[i+2] + " " + args[i+3] + " " + args[i+4];
            String endDateTime = args[i+5] + " " + args[i+6] + " " +  args[i+7];

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
                    return;
                }
            }

            if (prettyPrint) {
                if (prettyPrintFilePath.equals("-")) {
                    StringWriter stringWriter = new StringWriter();
                    PrettyPrinter prettyPrinter = new PrettyPrinter(stringWriter);
                    prettyPrinter.dump(appointmentBook);
                    System.out.print(stringWriter);
                } else {
                    File file = new File(prettyPrintFilePath);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            System.err.println("Error when trying to create file");
                            return;
                        }
                    }
                    try (FileWriter fileWriter = new FileWriter(file)) {
                        PrettyPrinter prettyPrinter = new PrettyPrinter(fileWriter);
                        prettyPrinter.dump(appointmentBook);
                    } catch (IOException e) {
                        System.err.println("Error when creating fileWriter");
                    }
                }
            }
        }
    }
}