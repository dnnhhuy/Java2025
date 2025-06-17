package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class TextDumperTest {

  @Test
  void appointmentBookOwnerIsDumpedInTextFormat() {
    String owner = "Test Appointment Book";
    AppointmentBook book = new AppointmentBook(owner);

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(book);

    String text = sw.toString();
    assertThat(text, containsString(owner));
  }

  @Test
  void canParseTextWrittenByTextDumper(@TempDir File tempDir) throws IOException, ParserException {
    String owner = "Test Appointment Book";
    AppointmentBook book = new AppointmentBook(owner);

    File textFile = new File(tempDir, "apptbook.txt");
    TextDumper dumper = new TextDumper(new FileWriter(textFile));
    dumper.dump(book);

    TextParser parser = new TextParser(new FileReader(textFile));
    AppointmentBook read = parser.parse();
    assertThat(read.getOwnerName(), equalTo(owner));
  }

  @Test
  void createFileIfFileNotExist(@TempDir File tempDir) throws IOException {
    String owner = "Test Appointment Book";
    AppointmentBook book = new AppointmentBook(owner);

    File textFile = new File(tempDir, "apptbook.txt");
    assertThat(textFile.exists(), equalTo(false));

    TextDumper dumper = new TextDumper(new FileWriter(textFile));
    dumper.dump(book);
    assertThat(textFile.exists(), equalTo(true));
  }

  @Test
  void allAppointmentsShouldBeDumpToFile(@TempDir File tempDir) throws IOException, Appointment.InvalidAppointmentTimeException, ParserException, Appointment.InvalidDateTimeFormatException {
    String owner = "David";
    AppointmentBook book = new AppointmentBook(owner);

    book.addAppointment(new Appointment("I love this meeting", "10/12/2025 9:30", "10/12/2025 12:30"));
    book.addAppointment(new Appointment("I love this meeting", "10/12/2025 10:30", "10/12/2025 12:30"));
    book.addAppointment(new Appointment("I love this meeting", "10/12/2025 10:30", "10/12/2025 12:30"));
    book.addAppointment(new Appointment("I love this meeting", "10/12/2025 10:30", "10/12/2025 12:30"));

    File textFile = new File(tempDir, "apptbook.txt");
    TextDumper textDumper = new TextDumper(new FileWriter(textFile));
    textDumper.dump(book);

    TextParser parser = new TextParser(new FileReader(textFile));
    AppointmentBook readBook = parser.parse();
    assertThat(readBook.getAppointments().size(), equalTo(book.getAppointments().size()));
}}
