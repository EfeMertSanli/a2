package parser;

/**
 * Constants used in the CommandParser class.
 * @author uuifx
 */
public final class CommandParserConstants {
    /**
     * Limit for command parts when splitting.
     */
    public static final int COMMAND_PARTS_LIMIT = 2;

    /**
     * Number of parts in a load database command.
     */
    public static final int LOAD_DATABASE_PARTS = 3;

    /**
     * Number of parts in a predicate-object pair.
     */
    public static final int PREDICATE_OBJECT_PARTS = 2;

    /**
     * Index of the command in the split array.
     */
    public static final int COMMAND_INDEX = 0;

    /**
     * Index of the content in the split array.
     */
    public static final int CONTENT_INDEX = 1;

    /**
     * Index of the path in the split array.
     */
    public static final int PATH_INDEX = 2;

    /**
     * Index of the subject in a relationship.
     */
    public static final int SUBJECT_INDEX = 0;

    /**
     * Index of the predicate in a relationship component.
     */
    public static final int PREDICATE_INDEX = 0;

    /**
     * Index of the object in a relationship component.
     */
    public static final int OBJECT_INDEX = 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private CommandParserConstants() {
        // Utility class should not be instantiated
    }
}