package parser;

/**
 * Constants used in the DatabaseParser class.
 * @author uuifx
 */
public final class DatabaseParserConstants {
    /**
     * Group index for the subject in a regex match.
     */
    public static final int SUBJECT_GROUP = 1;

    /**
     * Group index for the predicate in a regex match.
     */
    public static final int PREDICATE_GROUP = 2;

    /**
     * Group index for the object in a regex match.
     */
    public static final int OBJECT_GROUP = 3;

    /**
     * Group index for the name in a regex match.
     */
    public static final int NAME_GROUP = 1;

    /**
     * Group index for the ID in a regex match.
     */
    public static final int ID_GROUP = 2;

    /**
     * Starting line number.
     */
    public static final int LINE_START = 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseParserConstants() {
        // Utility class should not be instantiated
    }
}