package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.format.DateTimeParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextParserTest {

  @Test
  void validTextFileCanBeParsed() throws ParserException {
    InputStream resource = getClass().getResourceAsStream("valid-apptbook.txt");
    assertThat(resource, notNullValue());

    TextParser parser = new TextParser(new InputStreamReader(resource));
    AppointmentBook book = parser.parse();
    assertThat(book.getOwnerName(), equalTo("Test Appointment Book"));
  }

  @Test
  void invalidTextFileThrowsParserException() {
    InputStream resource = getClass().getResourceAsStream("empty-apptbook.txt");
    assertThat(resource, notNullValue());
    TextParser parser = new TextParser(new InputStreamReader(resource));
    assertThrows(ParserException.class, parser::parse);
  }

  @Test
  void parseTextFileShouldGetAllAppointmentsInTheFile() throws ParserException {
    InputStream resource = getClass().getResourceAsStream("example-apptbook.txt");
    assertThat(resource, notNullValue());

    TextParser parser = new TextParser(new InputStreamReader(resource));
    AppointmentBook book = parser.parse();
    assertThat(book.getAppointments().size(), equalTo(5));
  }

  @Test
  void invalidEndTimeInTextFileShouldThrowException() {
    InputStream resource = getClass().getResourceAsStream("invalidDate-aptbook.txt");
    assertThat(resource, notNullValue());

    TextParser parser = new TextParser(new InputStreamReader(resource));
    assertThrows(ParserException.class, parser::parse);
  }

  @Test
  void invalidDateTimeFormatShouldThrowException() {
    InputStream resource = getClass().getResourceAsStream("invalidDateTimeFormat-apptbook.txt");
    assertThat(resource, notNullValue());

    TextParser parser = new TextParser(new InputStreamReader(resource));
    assertThrows(ParserException.class, parser::parse);
  }

}
