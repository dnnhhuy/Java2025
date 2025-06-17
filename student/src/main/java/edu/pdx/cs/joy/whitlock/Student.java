package edu.pdx.cs.joy.whitlock;

import edu.pdx.cs.joy.lang.Human;

import java.util.ArrayList;
                                                                                    
/**                                                                                 
 * This class represents a <code>Student</code>.
 */                                                                                 
public class Student extends Human {

  private ArrayList<String> classes;
  private double gpa;
  private String gender;



  /**                                                                               
   * Creates a new <code>Student</code>                                             
   *                                                                                
   * @param name                                                                    
   *        The student's name                                                      
   * @param classes                                                                 
   *        The names of the classes the student is taking.  A student              
   *        may take zero or more classes.                                          
   * @param gpa                                                                     
   *        The student's grade point average                                       
   * @param gender                                                                  
   *        The student's gender ("male", "female", or "other", case insensitive)
   */
  public Student(String name, ArrayList<String> classes, Double gpa, String gender) {
    super(name);
    this.name = parseName(name);
    this.classes = classes;
    this.gpa = parseGPA(gpa);
    this.gender = parseGender(gender);
  }

  private String parseGender(String gender) {
    if (gender.toLowerCase().equals("male") || gender.toLowerCase().equals("female") || gender.toLowerCase().equals("other")) {
      return gender;
    } else {
      System.out.println(gender);
      throw new IllegalArgumentException("Gender must be male/female/other");
    }
  }


  private String parseName(String name) {
    try {
      Integer.parseInt(name);
      throw new IllegalArgumentException("Name cannot be a number");
    } catch (NumberFormatException ex) {
    }
    return name;
  }
private Double parseGPA(Double gpa) {
    if (gpa >= 0.0 && gpa <= 4.0) {
      return gpa;
    } else {
      throw new InvalidGpaException();
    }
}
  /**                                                                               
   * All students say "This class is too much work"
   */
  @Override
  public String says() {
    return "This class is too much work";
  }
                                                                                    
  /**                                                                               
   * Returns a <code>String</code> that describes this                              
   * <code>Student</code>.                                                          
   */                                                                               
  public String toString() {
    StringBuilder returnString = new StringBuilder();
    returnString.append(this.name);
    returnString.append(" has a GPA of ").append(this.gpa);
    returnString.append(" and is taking ").append(this.classes.size()).append(" classes: ");
    if (this.classes.size() > 0) {
      returnString.append(this.classes.get(0));
      for (int i = 1; i < this.classes.size(); i++) {
        returnString.append(", ");
        returnString.append(this.classes.get(i));
      }
    }
    returnString.append(". He says \"").append(says()).append("\".");

    return returnString.toString();
  }

  /**
   * Main program that parses the command line, creates a
   * <code>Student</code>, and prints a description of the student to
   * standard out by invoking its <code>toString</code> method.
   */

  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println("Missing command line arguments");
    } else {
      String name = args[0];
      String gender = args[1];
      double gpa;
      try {
         gpa = Double.parseDouble(args[2]);
      } catch (NumberFormatException ex) {
        System.err.print("GPA is not a number");
        return;
      }
      ArrayList<String> classes = new ArrayList<>();
      for (int i = 3; i < args.length; i++) {
        classes.add(args[i]);
      }
      try {
        Student student = new Student(name, classes, gpa, gender);
        System.out.println(student.toString());
        return;
      } catch (InvalidGpaException ex) {
        System.err.print("GPA must be between 0.0 - 4.0");
        return;
      }
    }
  }

  public static class InvalidGpaException extends RuntimeException{
  }
}