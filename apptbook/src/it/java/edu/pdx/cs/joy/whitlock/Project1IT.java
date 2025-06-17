package edu.pdx.cs.joy.whitlock;

import com.sun.tools.javac.Main;
import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileReader;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for the {@link Project1} main class.
 */
class Project1IT extends InvokeMainTestCase {

  /**
   * Invokes the main method of {@link Project1} with the given arguments.
   */
  private MainMethodResult invokeMain(String... args) {
    return invokeMain( Project1.class, args );
  }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
  }

  @Test
  void noArgumentsProvidedPrintHelpfulMessage() {
    String[] args = {};
    MainMethodResult result = invokeMain(Project1.class, args);
    String desiredMessage = """
                usage: java -jar target/apptbook-1.0.0.jar [options] <args>
                  args are (in this order):
                    owner               The person whose owns the appt book
                    description         A description of the appointment
                    begin               When the appt begins (24-hour time)
                    end                 When the appt ends (24-hour time)
                  options are (options may appear in any order):
                    -textFile File      Where to read/write the appointment book
                    -print              Prints a description of the new appointment
                    -README             Prints a README for this project and exits
                  Date and time should be in the format: mm/dd/yyyy hh:mm
                """;
    assertThat(result.getTextWrittenToStandardOut(), equalTo(desiredMessage));
  }

  @Test
  void missingDescriptionShouldThrowExceptionAndPrintAppropriateMessage() {
    String[] args = {"David"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Missing description argument\n"));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(Project1.getDescriptionMessage()));

  }

  @Test
  void missingBeginDateShouldThrowExceptionAndPrintAppropriateMessage() {
    String[] args = {"David", "This is important meeting"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Missing appointment's begin date argument\n"));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(Project1.getDescriptionMessage()));

  }

  @Test
  void missingBeginTimeShouldThrowExceptionAndPrintAppropriateMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Missing appointment's begin time argument\n"));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(Project1.getDescriptionMessage()));

  }

  @Test
  void missingEndDateShouldThrowExceptionAndPrintAppropriateMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Missing appointment's end date argument\n"));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(Project1.getDescriptionMessage()));
  }

  @Test
  void missingEndTimeShouldThrowExceptionAndPrintAppropriateMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/12/2025"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Missing appointment's end time argument\n"));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(Project1.getDescriptionMessage()));
  }

  @Test
  void invalidBeginTimeShouldPrintErrorMessage() {
    String[] args = {"David", "This is important meeting", "12/10/5", "12:30", "12/10/2025", "12:30"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Invalid input time. Time should be formatted as M/d/yyyy H:m\n"));
  }

  @Test
  void invalidEndTimeShouldPrintErrorMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/10/5", "12:30"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Invalid input time. Time should be formatted as M/d/yyyy H:m\n"));
  }

  @Test
  void endTimeBeforebeginTimeShouldPrintErrorMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/08/2025", "9:30"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Invalid Input. End date time must be after begin date time!\n"));

  }

  @Test
  void successAddingAppointmentShouldPrintSuccessMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/12/2025", "12:30", "-print"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardOut(), containsString("This is important meeting"));
  }

  @Test
  void inValidArgumentThrowsException() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/12/2025", "12:30", "-test"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Unexpected Argument.\n"));
  }

  @Test
  void successPrintReadme() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/12/2025", "12:30", "-README"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardOut(), containsString("This is a README file!"));
  }

  @Test
  void fileAndArgumentHaveDifferentOwnerNameWillThrowError() {
    String[] args = {"Anna", "This is important meeting", "12/10/2025", "9:30", "12/10/2025", "12:30", "-textFile", "/Users/dnnhhuy/Project/JavaProject/Java2025/apptbook/src/test/resources/edu/pdx/cs/joy/whitlock/example-apptbook.txt"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), containsString("Input name in the argument is not the same as in the given text file.\n"));
  }


  @Test
  void fileNotFoundShouldNotThrowExceptionButCreateFile(@TempDir File tempDir) {
    String[] args = {"Anna", "This is important meeting", "12/10/2025", "9:30", "12/10/2025", "12:30", "-textFile", tempDir.getAbsolutePath() + "example-apptbook.txt"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), containsString(""));
  }

  @Test
  void invalidTextFileFormatShouldThrowException() {
    String[] args = {"Anna", "This is important meeting", "12/10/2025", "9:30", "12/10/2025", "12:30", "-textFile", "src/test/resources/edu/pdx/cs/joy/whitlock/malformatted-apptbook.txt"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), containsString("Invalid text file format."));
  }

  @Test
  void missingOwnerInTextFileShouldThrowException() {
    String[] args = {"Anna", "This is important meeting", "12/10/2025", "9:30", "12/10/2025", "12:30", "-textFile", "src/test/resources/edu/pdx/cs/joy/whitlock/missingOwner-apptbook.txt"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing owner"));

  }

  @Test
  void emptyOwnerInTextFileShouldThrowException() {
    String[] args = {"Anna", "This is important meeting", "12/10/2025", "9:30", "12/10/2025", "12:30", "-textFile", "src/test/resources/edu/pdx/cs/joy/whitlock/emptyOwner-apptbook.txt"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing owner"));

  }

}