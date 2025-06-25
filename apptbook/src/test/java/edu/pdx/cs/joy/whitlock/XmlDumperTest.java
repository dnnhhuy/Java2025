package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class XmlDumperTest {

    @Test
    void testXmlDumperClass(@TempDir File tempDir) throws Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException, IOException {
        String owner = "David";
        AppointmentBook book = new AppointmentBook(owner);
        book.addAppointment(new Appointment("I love this meeting", "10/12/2025 9:30 AM", "10/12/2025 12:30 PM"));

        File textFile = new File(tempDir, "apptbook.xml");
        XmlDumper xmlDumper = new XmlDumper(new FileWriter(textFile));
        xmlDumper.dump(book);

        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            while (br.ready()) {
                String line =  br.readLine();
                System.out.println(line);
            }
        }
    }

    @Test
    void dumpedXmlFileShouldBeSuccessfullyParsedByParser(@TempDir File tempDir) throws Appointment.InvalidAppointmentTimeException, Appointment.InvalidDateTimeFormatException, IOException, ParserException {
        String owner = "David";
        AppointmentBook book = new AppointmentBook(owner);
        book.addAppointment(new Appointment("I love this meeting", "10/12/2025 9:30 AM", "10/12/2025 12:30 PM"));
        ArrayList<Appointment> originalAppointment = (ArrayList<Appointment>) book.getAppointments();

        File textFile = new File(tempDir, "apptbook.xml");
        XmlDumper dumper = new XmlDumper(new FileWriter(textFile));
        dumper.dump(book);

        XmlParser parser = new XmlParser(new FileInputStream(textFile));
        AppointmentBook newBook = parser.parse();

        assertThat(newBook.getOwnerName(), equalTo(book.getOwnerName()));

        ArrayList<Appointment> parsedAppoitment = (ArrayList<Appointment>) newBook.getAppointments();
        for (int i = 0; i < parsedAppoitment.size(); i++) {
            assertThat(parsedAppoitment.get(i).getBeginTimeString(), equalTo(originalAppointment.get(i).getBeginTimeString()));
            assertThat(parsedAppoitment.get(i).getEndTimeString(), equalTo(originalAppointment.get(i).getEndTimeString()));
            assertThat(parsedAppoitment.get(i).getDescription(), equalTo(originalAppointment.get(i).getDescription()));
        }
    }

}
