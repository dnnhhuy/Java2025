package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextDumperParserTest {

//  @Test
//  void emptyMapCanBeDumpedAndParsed() throws ParserException {
//    Map<String, String> map = Collections.emptyMap();
//    Map<String, String> read = dumpAndParse(map);
//    assertThat(read, equalTo(map));
//  }
//
  private AppointmentBook dumpAndParse(AppointmentBook map) throws ParserException {
    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(map);

    String text = sw.toString();

    TextParser parser = new TextParser(new StringReader(text));
    return parser.parse();
  }
//
//  @Test
//  void dumpedTextCanBeParsed() throws ParserException {
//    Map<String, String> map = Map.of("one", "1", "two", "2");
//    Map<String, String> read = dumpAndParse(map);
//    assertThat(read, equalTo(map));
//  }
    @Test
    void emptyAppointmentCanBeParsed() throws ParserException {
      AppointmentBook apptBook = new AppointmentBook("David");
      AppointmentBook read = dumpAndParse(apptBook);
      assertThat(read, equalTo(apptBook));
    }

    @Test
    void dumpedAppointmentBookCanBeParsed() throws ParserException, Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException {
        AppointmentBook apptBook = new AppointmentBook("David");
        apptBook.addAppointment(new Appointment("This is important meeting", "10/10/2025 9:30 AM", "10/10/2025 12:30 PM"));
        apptBook.addAppointment(new Appointment("This is important meeting", "11/10/2025 9:30 AM", "11/10/2025 12:30 PM"));
        apptBook.addAppointment(new Appointment("This is important meeting", "9/10/2025 9:30 AM", "9/10/2025 12:30 PM"));
        AppointmentBook read = dumpAndParse(apptBook);
        assertThat(read, equalTo(apptBook));
    }

    @Test
    void invalidTimeShouldThrowException() throws Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException, ParserException {
        AppointmentBook apptBook = new AppointmentBook("David");
        apptBook.addAppointment(new Appointment("This is important meeting", "10/10/2025 9:30 AM", "10/10/2025 12:30 PM"));
        apptBook.addAppointment(new Appointment("This is important meeting", "11/10/2025 9:30 AM", "11/10/2025 12:30 PM"));
        apptBook.addAppointment(new Appointment("This is important meeting", "9/10/2025 9:30 AM", "9/10/2025 12:30 PM"));

        String begin = "11/12/23 12:30 PM";
        String end = "11/12/2023 1:30 PM";
        StringWriter sw = new StringWriter();
        TextDumper dumper = new TextDumper(sw);
        DateTimeParseException ex = assertThrows(DateTimeParseException.class, () -> dumper.dump(apptBook, begin, end));
        assertThat(ex.getMessage(), containsString("could not be parsed"));

        String begin1 = "11/12/2023 00:01 AM";
        String end1 = "11/12/23 1:30";
        ex = assertThrows(DateTimeParseException.class, () -> dumper.dump(apptBook, begin1, end1));
        assertThat(ex.getMessage(), containsString("could not be parsed"));




    }
}
