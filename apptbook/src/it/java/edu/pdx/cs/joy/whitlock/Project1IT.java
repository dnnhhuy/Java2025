package edu.pdx.cs.joy.whitlock;

import com.sun.tools.javac.Main;
import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
                  owner
                  description
                  begin
                  end
                options are (options may appear in any order):
                  -print
                  -README
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
    assertThat(result.getTextWrittenToStandardError(), equalTo("Invalid input time. Time should be formatted as MM/dd/yyyy HH:mm\n"));
  }

  @Test
  void invalidEndTimeShouldPrintErrorMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/10/5", "12:30"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Invalid input time. Time should be formatted as MM/dd/yyyy HH:mm\n"));
  }

  @Test
  void endTimeBeforebeginTimeShouldPrintErrorMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/08/2025", "12:30"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardError(), equalTo("Invalid Input. End date time must be after begin date time!\n"));

  }

  @Test
  void successAddingAppointmentShouldPrintSuccessMessage() {
    String[] args = {"David", "This is important meeting", "12/10/2025", "12:30", "12/12/2025", "12:30", "-print"};
    MainMethodResult result = invokeMain(Project1.class, args);
    assertThat(result.getTextWrittenToStandardOut(), containsString("Added appointment successfully!"));
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

}