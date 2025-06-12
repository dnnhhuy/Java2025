package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for the <code>Student</code> class's main method.
 * These tests extend <code>InvokeMainTestCase</code> which allows them
 * to easily invoke the <code>main</code> method of <code>Student</code>.
 */
class StudentIT extends InvokeMainTestCase {

  @Test
  void invokingMainWithNoArgumentsPrintsMissingArgumentsToStandardError() {
    InvokeMainTestCase.MainMethodResult result = invokeMain(Student.class);
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
  }

  @Test
   void invokingMainPrintStringAsExpected() {
    InvokeMainTestCase.MainMethodResult result = invokeMain(Student.class, new String[]{"Dave", "male", "3.5", "Algorithms", "Operating Systems"});
    String toString = "Dave has a GPA of 3.5 and is taking 2 classes: Algorithms, Operating Systems. He says \"This class is too much work\".\n";
    assertThat(result.getTextWrittenToStandardOut(), equalTo(toString));
  }

  @Test
    void negativeGpaPrintErrorMessageToStandardError() {
      String[] args = {"Dave", "male", "-3.5", "Algorithms", "Operating Systems"};
      InvokeMainTestCase.MainMethodResult result = invokeMain(Student.class, args);

      assertThat(result.getTextWrittenToStandardError(), equalTo("GPA must be between 0.0 - 4.0"));
      assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
  }

}
