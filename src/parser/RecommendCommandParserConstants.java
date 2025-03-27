package parser;

/**
 * Constants used in the RecommendCommandParser class.
 * @author uuifx
 */
public final class RecommendCommandParserConstants {
    /**
     * Number of parts in a command.
     */
    public static final int COMMAND_PARTS = 2;

    /**
     * Index of the command in the parts array.
     */
    public static final int COMMAND_INDEX = 0;

    /**
     * Index of the content in the parts array.
     */
    public static final int CONTENT_INDEX = 1;

    /**
     * Length of a strategy code (e.g., "S1").
     */
    public static final int STRATEGY_LENGTH = 2;

    /**
     * Length of the "INTERSECTION" keyword.
     */
    public static final int INTERSECTION_LENGTH = 12;

    /**
     * Length of the "UNION" keyword.
     */
    public static final int UNION_LENGTH = 5;

    /**
     * Open parenthesis character.
     */
    public static final char OPEN_PAREN = '(';

    /**
     * Close parenthesis character.
     */
    public static final char CLOSE_PAREN = ')';

    /**
     * Comma character for separating terms.
     */
    public static final char COMMA = ',';

    /**
     * The string separator for terms in toString().
     */
    public static final String TERM_SEPARATOR = ", ";

    /**
     * Private constructor to prevent instantiation.
     */
    private RecommendCommandParserConstants() {
        // Utility class should not be instantiated
    }
}