package edu.pdx.cs.joy.whitlock;

import com.sun.tools.javac.Main;
import edu.pdx.cs.joy.InvokeMainTestCase;
import edu.pdx.cs.joy.UncaughtExceptionInMain;
import edu.pdx.cs.joy.web.HttpRequestHelper.RestException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.Buffer;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * An integration test for {@link Project4} that invokes its main method with
 * various arguments
 */
@TestMethodOrder(MethodName.class)
class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");

//    @Test
//    void test0RemoveAllMappings() throws IOException {
//      AppointmentBookRestClient client = new AppointmentBookRestClient(HOSTNAME, Integer.parseInt(PORT));
//      client.removeAllDictionaryEntries();
//    }
//

//
//    @Test
//    void test2EmptyServer() {
//        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT );
//
//        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
//
//        String out = result.getTextWrittenToStandardOut();
//        assertThat(out, out, containsString(PrettyPrinter.formatWordCount(0)));
//    }
//
//    @Test
//    void test3NoDefinitionsThrowsAppointmentBookRestException() {
//        String word = "WORD";
//        try {
//            invokeMain(Project4.class, HOSTNAME, PORT, word);
//            fail("Expected a RestException to be thrown");
//
//        } catch (UncaughtExceptionInMain ex) {
//            RestException cause = (RestException) ex.getCause();
//            assertThat(cause.getHttpStatusCode(), equalTo(HttpURLConnection.HTTP_NOT_FOUND));
//        }
//    }
//
//    @Test
//    void test4AddDefinition() {
//        String word = "WORD";
//        String definition = "DEFINITION";
//
//        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, word, definition );
//
//        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
//
//        String out = result.getTextWrittenToStandardOut();
//        assertThat(out, out, containsString(Messages.definedWordAs(word, definition)));
//
//        result = invokeMain( Project4.class, HOSTNAME, PORT, word );
//
//        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
//
//        out = result.getTextWrittenToStandardOut();
//        assertThat(out, out, containsString(PrettyPrinter.formatDictionaryEntry(word, definition)));
//
//        result = invokeMain( Project4.class, HOSTNAME, PORT );
//
//        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
//
//        out = result.getTextWrittenToStandardOut();
//        assertThat(out, out, containsString(PrettyPrinter.formatDictionaryEntry(word, definition)));
//    }

    @Test
    void test0RemoveAllAppointmentBooks() throws IOException {
        AppointmentBookRestClient client = new AppointmentBookRestClient(HOSTNAME, Integer.parseInt(PORT));
        client.removeAllAppointmentBooks();
    }

    @Test
    void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getTextWrittenToStandardError(), containsString(Project4.MISSING_ARGS));
    }

    @Test
    void test2PostMissingArguments() {
        String[] args = {"-host", "localhost", "-port", "8080", "David"};
        MainMethodResult result =  invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing description argument"));

        args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing begin argument"));

        args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "12/10/2025"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing begin argument"));

        args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "12/10/2025", "9:30"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing begin argument"));

        args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "12/10/2025", "9:30", "AM"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing end argument"));

        args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "12/10/2025", "9:30", "AM", "12/10/2025"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing end argument"));


        args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "12/10/2025", "9:30", "AM", "12/10/2025", "12:30"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing end argument"));
    }

    @Test
    void test3PostInvalidDateTimeShouldPrintError() {
        String[] args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "12/10/20", "9:30", "AM", "12/10/2025", "12:30", "PM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Invalid input time"));
    }

    @Test
    void test4PostValidDateTimeShouldPrintSuccessMessage() {
        String[] args = new String[]{"-host", "localhost", "-port", "8080", "David", "This is important", "10/19/2025", "6:00", "PM", "10/19/2025", "9:30", "PM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardOut(), containsString("Added appointment"));
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    }

    @Test
    void test5GetEmptyOwnerNameShouldPrintError() {
        String[] args = new String[]{"-host", "localhost", "-port", "8080", "-search", ""};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("The required parameter \"owner\" is missing"));
    }

    @Test
    void test6GetNotFoundOwnerShouldPrintError() {
        String[] args = new String[]{"-host", "localhost", "-port", "8080", "-search", "Anna"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Owner Anna does not exist"));
    }

    @Test
    void test7GetFoundOwnerShouldPrintAllAppointments() {
        String[] args = {"-host", "localhost", "-port", "8080", "-search", "David"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardOut(), containsString("David"));
    }

    @Test
    void test8GetFoundOwnerInRangeShouldPrintAppointmentsInRange() {
        String[] args = {"-host", "localhost", "-port", "8080", "-search", "David", "1/1/2025", "0:01", "AM", "12/12/2025", "11:59", "PM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardOut(), containsString("David has 1 appointments"));

        args = new String[] {"-host", "localhost", "-port", "8080", "-search", "David", "1/1/2024", "0:01", "AM", "12/12/2024", "11:59", "PM"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardOut(), containsString("David has 0 appointments"));
    }

    @Test
    void test9UnexpectedArgumentShouldThrowException() {
        String[] args = {"-host", "localhost", "-port", "8080", "-testing", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Invalid option"));

        args = new String[] {"-host", "localhost", "-port", "8080", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM", "testing"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Extraneous command line argument"));

        args = new String[] {"-host", "localhost", "-port", "8080", "-print", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM"};
        result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardOut(), containsString("Description:"));
    }

    @Test
    void test10MissingHostNameThrowException() {
        String[] args = {"-port", "8080", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
    }

    @Test
    void test11MissingPortThrowException() {
        String[] args = {"-host", "localhost", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing port"));
    }

    @Test
    void test12InvalidPortShouldThrowException() {
        String[] args = {"-host", "localhost", "-port", "abc", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM"};
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardError(), containsString("must be an integer"));
    }

    @Test
    void test13printReadmeShouldPrintContentFromReadme() throws IOException {
        String[] args = {"-host", "localhost", "-port", "8080", "-README", "David", "This is important meeting", "12/24/2025", "9:30", "AM", "12/24/2025", "11:30", "AM"};
        InputStream readme = Project4.class.getResourceAsStream("README.txt");
        assertThat(readme, not(nullValue()));

        BufferedReader br =  new BufferedReader(new InputStreamReader(readme));
        StringBuilder output = new StringBuilder();
        while (br.ready()) {
            String line = br.readLine();
            output.append(line + "\n");
        }
        MainMethodResult result = invokeMain(Project4.class, args);
        assertThat(result.getTextWrittenToStandardOut(), containsString(output.toString()));

    }

}