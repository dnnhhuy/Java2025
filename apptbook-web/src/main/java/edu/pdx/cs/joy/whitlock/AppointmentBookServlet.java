package edu.pdx.cs.joy.whitlock;

import com.google.common.annotations.VisibleForTesting;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>AppointmentBook</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple dictionary of words
 * and their definitions.
 */
public class AppointmentBookServlet extends HttpServlet
{
    static final String WORD_PARAMETER = "word";
    static final String DEFINITION_PARAMETER = "definition";
    static final String OWNER_PARAMETER = "owner";
    static final String APPOINTMENT_DESCRIPTION_PARAMETER = "description";
    static final String APPOINTMENT_BEGIN_PARAMETER = "begin";
    static final String APPOINTMENT_END_PARAMETER = "end";

    private final Map<String, String> dictionary = new HashMap<>();
    private final Map<String, AppointmentBook> appointmentStorage = new HashMap<>();
    /**
     * Handles an HTTP GET request from a client by writing the definition of the
     * word specified in the "word" HTTP parameter to the HTTP response.  If the
     * "word" parameter is not specified, all of the entries in the dictionary
     * are written to the HTTP response.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        response.setContentType( "text/plain" );

        String owner = getParameter( OWNER_PARAMETER, request );
        if (owner != null && !owner.equals("")) {
            log("GET " + owner);
            getAppointmentBook(owner, response, request);

        } else {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, Messages.missingRequiredParameter(OWNER_PARAMETER));
        }
    }

    /**
     * Get appointment book given owner's name
     */
    @VisibleForTesting
    private void getAppointmentBook(String owner, HttpServletResponse response, HttpServletRequest request) throws IOException {
        AppointmentBook apptBook = getAppointmentBook(owner);

        String begin = getParameter(APPOINTMENT_BEGIN_PARAMETER, request);
        String end = getParameter(APPOINTMENT_END_PARAMETER, request);

        if (apptBook == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Owner %s does not exist.", owner));
        } else {
            PrintWriter pw = response.getWriter();

            TextDumper textDumper = new TextDumper(pw);
            if (begin != null || end != null) {
                try {
                    textDumper.dump(apptBook, begin, end);
                } catch (DateTimeParseException | TextDumper.IllegalTimeRangeException ex) {
                    response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, ex.getMessage());
                    return;
                }
            } else {
                textDumper.dump(apptBook);
            }

            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    /**
     * Handles an HTTP POST request by storing the dictionary entry for the
     * "word" and "definition" request parameters.  It writes the dictionary
     * entry to the HTTP response.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        response.setContentType( "text/plain" );
        String paramDelete = getParameter("delete", request);
        boolean delete = Boolean.valueOf(paramDelete);
        if (delete) {
            String owner = getParameter(OWNER_PARAMETER, request);
            if  (owner == null) {
                missingRequiredParameter(response, OWNER_PARAMETER);
            } else {
                if (getAppointmentBook(owner) != null) {
                    this.appointmentStorage.remove(owner);
                    PrintWriter pw = response.getWriter();
                    pw.println(Messages.allAppoinmentDeleted(owner));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    PrintWriter pw = response.getWriter();
                    pw.println(String.format("%s's appointment book does not exist", owner));
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        }
        else {
            String owner = getParameter(OWNER_PARAMETER, request);
            if (owner == null) {
                missingRequiredParameter(response, OWNER_PARAMETER);
                return;
            }

            String description = getParameter(APPOINTMENT_DESCRIPTION_PARAMETER, request);
            if (description == null) {
                missingRequiredParameter(response, APPOINTMENT_DESCRIPTION_PARAMETER);
                return;
            }

            String beginTime = getParameter(APPOINTMENT_BEGIN_PARAMETER, request);
            if (beginTime == null) {
                missingRequiredParameter(response, APPOINTMENT_BEGIN_PARAMETER);
                return;
            }

            String endTime = getParameter(APPOINTMENT_END_PARAMETER, request);
            if (endTime == null) {
                missingRequiredParameter(response, APPOINTMENT_END_PARAMETER);
                return;
            }

            log("POST " + owner + " -> " + String.format("%s, %s, %s", description, beginTime, endTime));

            AppointmentBook apptBook = this.appointmentStorage.get(owner);
            if (apptBook == null) {
                apptBook = new AppointmentBook(owner);
                this.appointmentStorage.put(owner, apptBook);
            }
            try {
                apptBook.addAppointment(new Appointment(description, beginTime, endTime));
            } catch (Appointment.InvalidAppointmentTimeException | Appointment.InvalidDateTimeFormatException e) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
                return;
            }


            PrintWriter pw = response.getWriter();
            pw.print(Messages.addedAppointmentToAppointmentBook(owner, description, beginTime, endTime));
            pw.flush();

            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    /**
     * Handles an HTTP DELETE request by removing all dictionary entries.  This
     * behavior is exposed for testing purposes only.  It's probably not
     * something that you'd want a real application to expose.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        log("DELETE all dictionary entries");

        this.appointmentStorage.clear();

        PrintWriter pw = response.getWriter();
        pw.println(Messages.allAppoinmentBooksDeleted());
        pw.flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        String message = Messages.missingRequiredParameter(parameterName);
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
    }


    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }


    @VisibleForTesting
    String getDefinition(String word) {
        return this.dictionary.get(word);
    }

    @VisibleForTesting
    AppointmentBook getAppointmentBook(String owner) {
        return this.appointmentStorage.get(owner);
    }

    @VisibleForTesting
    Map<String, AppointmentBook> getAppointmentStorage() {
        return this.appointmentStorage;
    }

    @Override
    public void log(String msg) {
      System.out.println(msg);
    }
}
