package edu.pdx.cs.joy.whitlock;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{
    public static String allAppoinmentBooksDeleted() {
        return "All appointment books have been deleted";
    };

    public static String missingRequiredParameter(String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    public static String definedWordAs(String word, String definition )
    {
        return String.format( "Defined %s as %s", word, definition );
    }

    public static String addedAppointmentToAppointmentBook(String owner, String description, String beginTime, String endTime) {
        return String.format("Added appointment: %s, %s, %s to %s's appointment book.", description, beginTime, endTime, owner);

    }
    public static String ownerNameCannotBeEmpty() {
        return String.format("Owner name cannot be empty");
    }

    public static String allDictionaryEntriesDeleted() {
        return "All dictionary entries have been deleted";
    }

    public static String allAppoinmentDeleted(String owner) {
        return String.format("The %s's appointment book has been deleted from the server", owner);
    }
}
