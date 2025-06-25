package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XmlParserTest {
    @Test
    void validXmlFileShouldNotThrowException() throws ParserException {
        InputStream resource = getClass().getResourceAsStream("valid-apptbook.xml");
        assertThat(resource, notNullValue());

        XmlParser xmlParser = new XmlParser(resource);
        AppointmentBook appointmentBook = xmlParser.parse();
        assertThat(appointmentBook.getOwnerName(), equalTo("Example Appointment Book"));

        for (Appointment appt : appointmentBook.getAppointments()) {
            assertThat(appt.getBeginTime().equals(LocalDateTime.of(2023, 10, 11, 13, 6)), equalTo(true));
            assertThat(appt.getEndTime().equals(LocalDateTime.of(2023, 10, 11, 14, 0)), equalTo(true));
            assertThat(appt.getDescription(), equalTo("Example Appointment"));
        }
    }

    @Test
    void invalidXmlFileShouldThrowException() {
        InputStream resource = getClass().getResourceAsStream("invalid-apptbook.xml");
        assertThat(resource, notNullValue());

        XmlParser xmlParser = new XmlParser(resource);
        assertThrows(ParserException.class, xmlParser::parse);
    }
}
