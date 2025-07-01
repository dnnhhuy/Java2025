package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;

import java.io.*;
import java.util.Map;

/**
 * The main class that parses the command line and communicates with the
 * Appointment Book server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";

    public static void main(String... args) {
        if (args.length == 0) {
            usage(MISSING_ARGS);
            return;
        }
        int i = 0;
        String hostName = null;
        String portString = null;
        Boolean search = false;
        Boolean print = false;
        Boolean printReadme = false;
        while (i < args.length && args[i].startsWith("-")) {
            if (args[i].equals("-host")) {
                hostName = args[++i];
            } else if (args[i].equals("-port")) {
                portString = args[++i];
            } else if (args[i].equals("-search")) {
                search = true;
            } else if (args[i].equals("-print")) {
                print = true;
            } else if (args[i].equals("-README")) {
                printReadme = true;
            } else {
                System.err.println("Invalid option");
                return;
            }
            i += 1;
        }

        String owner = null;
        String description = null;
        String begin = null;
        String end = null;

        while (i < args.length) {
            if (owner == null) {
                owner = args[i];
            } else if (description == null) {
                if (search) {
                    description = "";
                    continue;
                }
                description = args[i];
            } else if (begin == null) {
                try {
                    begin = args[i]+ " " + args[++i]+ " " + args[++i];
                } catch (RuntimeException ex) {
                    error("Missing begin argument");
                    return;
                }
            } else if (end == null) {
                try {
                    end = args[i] + " " + args[++i] + " " + args[++i];
                } catch (RuntimeException ex) {
                    error("Missing end argument");
                    return;
                }
           } else {
                usage("Extraneous command line argument: " + args[i]);
            }
            i++;
        }

        if (hostName == null) {
            usage( MISSING_ARGS );
            return;

        } else if ( portString == null) {
            usage( "Missing port" );
            return;
        }

        int port;
        try {
            port = Integer.parseInt( portString );

        } catch (NumberFormatException ex) {
            usage("Port \"" + portString + "\" must be an integer");
            return;
        }

        AppointmentBookRestClient client = new AppointmentBookRestClient(hostName, port);
        String message = "";
        if (search) {
            try {
                if  (owner == null) {
                    error("Missing owner argument");
                } else {
                    try {
                        if (begin == null) {
                            begin = "";
                        }
                        if (end == null) {
                            end = "";
                        }
                        AppointmentBook returnedApptBook = client.getAppointmentsInTimeRange(owner, begin, end);
                        StringWriter stringWriter = new StringWriter();
                        PrettyPrinter prettyPrinter = new PrettyPrinter(stringWriter);
                        prettyPrinter.dump(returnedApptBook);
                        message = stringWriter.toString();
                    } catch (HttpRequestHelper.RestException ex) {
                        error(ex.getMessage());
                        return;
                    }
                }
            } catch (ParserException | IOException e) {
                error("While contacting server: " + e.getMessage());
            }
        }
        else {
            try {
                if (owner == null) {
                    System.err.println("Missing owner argument");
                } else if (description == null) {
                    error("Missing description argument");
                } else if (begin == null) {
                    error("Missing begin argument");
                } else if (end == null) {
                    error("Missing end argument");
                } else {
                    try {
                        client.addAppointment(owner, description, begin, end);
                    } catch (HttpRequestHelper.RestException ex) {
                        error(ex.getMessage());
                        return;
                    }
                    message = Messages.addedAppointmentToAppointmentBook(owner, description, begin, end);
                    if (print) {
                        System.out.printf("Description: %s\n", description);
                    }
                }

            } catch (IOException ex) {
                error("While contacting server: " + ex.getMessage());
            }
        }
        System.out.println(message);

        if (printReadme) {
            try (InputStream input = Project4.class.getResourceAsStream("README.txt")) {
                assert input != null;
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                while (br.ready()) {
                    String line = br.readLine();
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        }

    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
    }

    /**
     * Prints usage information for this program and exits
     * @param message An error message to print
     */
    private static void usage( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java -jar target/apptbook-client.jar [options] args");
        err.println("  args are  (in this order):");
        err.println("    owner                  The person who owns the appt book");
        err.println("    description            A description of the appointmnet");
        err.println("    begin                  When the appt begins");
        err.println("    end                    When the appts ends");
        err.println("  options are (options may appear in any order):");
        err.println("   -host hostname          Host computer on which the server runs");
        err.println("   -port port              Port on which the server is listening");
        err.println("   -search                 Search for appointments");
        err.println("   -print                  Prints a description of the new appointment");
        err.println("   -README                 Prints a README fr this project and exits");
        err.println();
        err.println("This program post appointment to the server");
        err.println("to the server.");
        err.println("If no appointment information is specified, then all the appointment given owner's name");
        err.println("is printed.");
        err.println("When -search flag is specified, then print all the appointments of the given owner");
        err.println("If specify begin and end when using -search, all the appointments within that range is printed.");
        err.println();
    }
}