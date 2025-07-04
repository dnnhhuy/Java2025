package edu.pdx.cs.joy.whitlock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Date;

import edu.pdx.cs.joy.AbstractAppointment;

public class Appointment extends AbstractAppointment {
    private final String description;
    private final LocalDateTime begin;
    private final LocalDateTime end;

    public Appointment(String description, String beginString, String endString) throws IllegalTimeInputException, IllegalDateTimeFormat {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        LocalDateTime begin;
        LocalDateTime end;
        try {
            begin = LocalDateTime.parse(beginString, formatter);
        } catch (DateTimeParseException exception) {
            throw new IllegalDateTimeFormat();
        }

        try {
            end = LocalDateTime.parse(endString, formatter);
        } catch (DateTimeParseException exception) {
            throw new IllegalDateTimeFormat();
        }

        if (end.isBefore(begin)) {
            throw new IllegalTimeInputException("Invalid input time. End time cannot be before begin time.");
        }
        this.description = description;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public LocalDateTime getBeginTime() {
        return this.begin;
    }

    public LocalDateTime getEndTime() {
        return this.end;
    }
    @Override
    public String getBeginTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        return formatter.format(this.begin);
    }

    @Override
    public String getEndTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        return formatter.format(this.end);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public static class IllegalTimeInputException extends Throwable {
        public IllegalTimeInputException(String s) {
            super(s);
        }
    }

    public static class IllegalDateTimeFormat extends Throwable {
        public IllegalDateTimeFormat() {
            super("Invalid Date Time Input. Please try again");
        }
    }
}
