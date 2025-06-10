package edu.pdx.cs.joy.whitlock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Unit tests for the Student class.  In addition to the JUnit annotations,
 * they also make use of the <a href="http://hamcrest.org/JavaHamcrest/">hamcrest</a>
 * matchers for more readable assertion statements.
 */
public class StudentTest
{

  @Test
  void studentNamedPatIsNamedPat() {
    String name = "Pat";
    var pat = getStudentNamed(name);
    assertThat(pat.getName(), equalTo(name));
  }

  @Test
  void allStudentSayThisClassIsTooMuchWork() {
      var student = getStudentNamed("ANYTHING");
    assertThat(student.says(), equalTo("This class is too much work"));
  }

  private static Student getStudentNamed(String name) {
    return new Student(name, new ArrayList<>(), 0.0, "Doesn't matter");
  }
}
