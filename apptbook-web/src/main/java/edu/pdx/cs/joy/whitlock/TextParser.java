package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;

import javax.swing.text.html.parser.Parser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.Buffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

//  public Map<String, String> parse() throws ParserException {
//    Pattern pattern = Pattern.compile("(.*) : (.*)");
//
//    Map<String, String> map = new HashMap<>();
//
//    try (
//      BufferedReader br = new BufferedReader(this.reader)
//    ) {
//
//      for (String line = br.readLine(); line != null; line = br.readLine()) {
//        Matcher matcher = pattern.matcher(line);
//        if (!matcher.find()) {
//          throw new ParserException("Unexpected text: " + line);
//        }
//
//        String word = matcher.group(1);
//        String definition = matcher.group(2);
//
//        map.put(word, definition);
//      }
//
//    } catch (IOException e) {
//      throw new ParserException("While parsing dictionary", e);
//    }
//
//    return map;
//  }

  public AppointmentBook parse() throws ParserException {
    Pattern ownerPattern = Pattern.compile(".+:");
    Pattern appointmentPattern = Pattern.compile("(.+);(.+);(.+)");
    AppointmentBook apptBook;
    try (BufferedReader br = new BufferedReader(this.reader)) {
      String owner = br.readLine();
      Matcher matcher = ownerPattern.matcher(owner);
      if (!matcher.find()) {
        throw new ParserException("Cannot parse owner's name");
      }
      apptBook = new AppointmentBook(owner.substring(0, owner.length() - 1));
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (line.isBlank()) continue;
        matcher = appointmentPattern.matcher(line);
        if (!matcher.find()) {
          throw new ParserException("Cannot parse appointment");
        }
        String description = matcher.group(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        LocalDateTime begin = LocalDateTime.parse(matcher.group(2).trim().replace("?", "\u202F"), formatter);
        LocalDateTime end = LocalDateTime.parse(matcher.group(3).trim().replace("?", "\u202F"), formatter);
          try {
              apptBook.addAppointment(new Appointment(description, begin, end));
          } catch (Appointment.InvalidAppointmentTimeException e) {
              throw new ParserException("While parsing appointments", e);
          }
      }
    } catch (IOException e) {
        throw new ParserException("While parsing appointments", e);
    }

    return apptBook;
  }

}
