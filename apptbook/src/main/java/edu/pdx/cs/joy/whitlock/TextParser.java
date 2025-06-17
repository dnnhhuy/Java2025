package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.AppointmentBookParser;
import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A skeletal implementation of the <code>TextParser</code> class for Project 2.
 */
public class TextParser implements AppointmentBookParser<AppointmentBook> {
  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public AppointmentBook parse() throws ParserException {
    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {

      String owner = br.readLine();
      if (owner == null) {
        throw new ParserException("Missing owner");
      }

      String[] checkOwner = owner.split(",");
      if (checkOwner.length > 1 || owner.equals("")) {
        throw new ParserException("Missing owner");
      }
      AppointmentBook book = new AppointmentBook(owner);
      while (br.ready()) {
        String line = br.readLine();
        String[] data = line.split(",");
        if (data.length != 3) {
          throw new ParserException("Invalid text file format.");
        }
        String description = data[0].trim();
        String beginTime = data[1].trim();
        String endTime = data[2].trim();
        book.addAppointment(new Appointment(description, beginTime, endTime));

      }
      return book;

    } catch (IOException | Appointment.InvalidAppointmentTimeException | Appointment.InvalidDateTimeFormatException e) {
      throw new ParserException("While parsing appointment book text", e);
    }
  }
}
