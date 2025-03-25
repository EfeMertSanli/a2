package util;
/**
 * Global constants for the recommendation system.
 * This class provides centralized access to all string and numeric constants used throughout the application.
 * @author uuifx
 */
public final class Constants {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {
    }

    /**
     * Constants related to the command line interface.
     */
    public static final class CLI {
        /**
         * Command name for loading a database file.
         */
        public static final String LOAD = "load";
        /**
         * Parameter for the load command to specify a database file.
         */
        public static final String DATABASE = "database";
        /**
         * Command name for exiting the application.
         */
        public static final String QUIT = "quit";
        /**
         * Command name for adding a relationship to the graph.
         */
        public static final String ADD = "add";
        /**
         * Command name for removing a relationship from the graph.
         */
        public static final String REMOVE = "remove";
        /**
         * Command name for listing all nodes in the graph.
         */
        public static final String NODES = "nodes";
        /**
         * Command name for listing all relationships in the graph.
         */
        public static final String EDGES = "edges";
        /**
         * Command name for getting product recommendations.
         */
        public static final String RECOMMEND = "recommend";
        /**
         * Command name for exporting the graph in DOT notation.
         */
        public static final String EXPORT = "export";
        /**
         * The command line prompt displayed before each user input.
         */
        public static final String PROMPT = "> ";
        /**
         * A single space character used for formatting output.
         */
        public static final String SPACE = " ";
        /**
         * An empty string used for initialization and concatenation.
         */
        public static final String EMPTY = "";
        /**
         * A newline character for formatting multi-line output.
         */
        public static final String NEW_LINE = "\n";
        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private CLI() {
        }
    }
    /**
     * Constants related to errors and exceptions.
     * This class contains error messages and prefixes used throughout the application.
     */
    public static final class Error {
        /**
         * Prefix for all error messages displayed to the user.
         */
        public static final String PREFIX = "Error: ";
        /**
         * Error message prefix for line-specific errors in the database file.
         */
        public static final String ERROR_IN_LINE = "Error in line ";
        /**
         * Error message for when a database file cannot be read.
         */
        public static final String FAILED_READ_DATABASE = "Failed to read database file: ";
        /**
         * Error message for when adding a relationship fails.
         */
        public static final String FAILED_ADD_RELATIONSHIP = "Failed to add relationship: ";
        /**
         * Error message for when removing a relationship fails.
         */
        public static final String FAILED_REMOVE_RELATIONSHIP = "Failed to remove relationship: ";
        /**
         * Error message for when parsing a recommend command fails.
         */
        public static final String FAILED_PARSE_RECOMMEND = "Failed to parse recommend command: ";
        /**
         * Error message for when processing a recommend command fails.
         */
        public static final String FAILED_PROCESS_RECOMMEND = "Failed to process recommend command: ";
        /**
         * Error message for when an unknown command is entered.
         */
        public static final String UNKNOWN_COMMAND = "Unknown command: ";
        /**
         * Error message for when a referenced node cannot be found.
         */
        public static final String NODE_NOT_FOUND = "Node not found";
        /**
         * Error message for when an invalid relationship type is specified.
         */
        public static final String INVALID_RELATIONSHIP_TYPE = "Invalid relationship type: ";
        /**
         * Error message for when a relationship cannot be found.
         */
        public static final String RELATIONSHIP_NOT_FOUND = "Relationship not found";
        /**
         * Error message for when a line has an invalid format.
         */
        public static final String INVALID_LINE_FORMAT = "Invalid line format: ";
        /**
         * Error message for when a node specification has an invalid format.
         */
        public static final String INVALID_NODE_FORMAT = "Invalid node format: ";
        /**
         * Error message prefix for command format errors.
         */
        public static final String INVALID_COMMAND_FORMAT = "Invalid command format, could not ";
        /**
         * Error message suffix for when a subject-predicate-object cannot be split.
         */
        public static final String SPLIT_SUBJECT_OBJECT = "split into subject and object";
        /**
         * Error message suffix for when predicate and object cannot be extracted.
         */
        public static final String EXTRACT_PREDICATE_OBJECT = "extract predicate and object";
        /**
         * Error message for when a recommend command is missing its term.
         */
        public static final String MISSING_TERM = "Invalid recommend command: missing term";
        /**
         * Error message for when an opening parenthesis is expected after a keyword.
         */
        public static final String EXPECTED_OPEN_PAREN = "Expected '(' after ";
        /**
         * Error message for when a comma is expected after the first term.
         */
        public static final String EXPECTED_COMMA = "Expected ',' after first term in ";
        /**
         * Error message for when a closing parenthesis is expected after the second term.
         */
        public static final String EXPECTED_CLOSE_PAREN = "Expected ')' after second term in ";
        /**
         * Error message for when a strategy specification is expected.
         */
        public static final String EXPECTED_STRATEGY = "Expected strategy (S1, S2, or S3)";
        /**
         * Error message for when a product ID is expected after a strategy.
         */
        public static final String EXPECTED_PRODUCT_ID = "Expected product ID after strategy ";
        /**
         * Error message for when there is unexpected input after a term.
         */
        public static final String UNEXPECTED_INPUT = "Unexpected input after term: ";
        /**
         * Error message for when no product ID can be found in a term.
         */
        public static final String NO_PRODUCT_ID = "No product ID found in term";
        /**
         * Error message for when an unknown term type is encountered.
         */
        public static final String UNKNOWN_TERM_TYPE = "Unknown term type: ";
        /**
         * Error message for when an unknown strategy type is specified.
         */
        public static final String UNKNOWN_STRATEGY_TYPE = "Unknown strategy type: ";
        /**
         * Error message for when a command string is empty.
         */
        public static final String COMMAND_EMPTY = "Command string must not be empty";
        /**
         * Error message for when a load database command has an invalid format.
         */
        public static final String INVALID_LOAD_DATABASE = "Invalid load database command format: ";
        /**
         * Error message for when a command must start with 'add' or 'remove'.
         */
        public static final String COMMAND_START_ADD_REMOVE = "Command must start with 'add' or 'remove'";
        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private Error() {
        }
    }
    /**
     * Constants related to the regular expressions used for parsing.
     * This class contains patterns for validating and extracting components from user input.
     */
    public static final class Regex {
        /**
         * Regular expression pattern for valid node names.
         * Node names must consist of one or more alphanumeric characters.
         */
        public static final String NODE_NAME_PATTERN = "[a-zA-Z0-9]+";
        /**
         * Regular expression pattern for valid product IDs.
         * Product IDs must consist of one or more numeric digits.
         */
        public static final String PRODUCT_ID_PATTERN = "[0-9]+";
        /**
         * Regular expression for matching product specifications in the format: name(id=number).
         * This pattern captures the product name and ID as separate groups.
         */
        public static final String PRODUCT_REGEX = "(" + NODE_NAME_PATTERN + ")\\s*\\(\\s*id\\s*=\\s*(" + PRODUCT_ID_PATTERN + ")\\s*\\)";
        /**
         * Regular expression for matching category names.
         * This pattern captures the category name.
         */
        public static final String CATEGORY_REGEX = "(" + NODE_NAME_PATTERN + ")";
        /**
         * Regular expression for matching valid relationship predicates.
         * Captures one of the six relationship types as a group.
         */
        public static final String PREDICATE_REGEX = "(contains|contained-in|part-of|has-part|successor-of|predecessor-of)";
        /**
         * Regular expression for matching a complete line in the database file format.
         * Captures subject, predicate, and object as groups.
         */
        public static final String LINE_REGEX = "\\s*(.+?)\\s+" + PREDICATE_REGEX + "\\s+(.+?)\\s*";
        /**
         * Regular expression for splitting command strings on whitespace.
         */
        public static final String COMMAND_SPLIT_REGEX = "\\s+";
        /**
         * Regular expression for splitting a command based on its predicate.
         * Used to separate subject from predicate and object.
         */
        public static final String PREDICATE_SPLIT_REGEX = "\\s+(contains|contained-in|part-of|has-part|successor-of|predecessor-of)\\s+";
        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private Regex() {
        }
    }
    /**
     * Constants related to the DOT graph notation.
     * This class contains strings used to format the DOT output for the export command.
     */
    public static final class Dot {
        /**
         * Opening line for a directed graph in DOT notation.
         */
        public static final String DIGRAPH_START = "digraph {";
        /**
         * Closing line for a directed graph in DOT notation.
         */
        public static final String DIGRAPH_END = "}";
        /**
         * Arrow symbol used to represent directed edges in DOT notation.
         */
        public static final String ARROW = " -> ";
        /**
         * Opening bracket for edge label attributes in DOT notation.
         */
        public static final String LABEL_START = " [label=";
        /**
         * Closing bracket for edge label attributes in DOT notation.
         */
        public static final String LABEL_END = "]";
        /**
         * Attribute string for specifying box-shaped nodes in DOT notation.
         * Used for category nodes.
         */
        public static final String SHAPE_BOX = " [shape=box]";
        /**
         * Indentation string used for formatting DOT output.
         */
        public static final String INDENT = "  ";
        /**
         * Hyphen character that should be removed from relationship names in DOT edge labels.
         */
        public static final String HYPHEN = "-";
        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private Dot() {
        }
    }

    /**
     * Constants related to graph relationships.
     * This class contains the string names of the six relationship types supported in the system.
     */
    public static final class Relationship {
        /**
         * Relationship name for "contains" relationships.
         * Used when a category contains a product or subcategory.
         */
        public static final String CONTAINS = "contains";

        /**
         * Relationship name for "contained-in" relationships.
         * Used when a product or subcategory is contained in a category.
         */
        public static final String CONTAINED_IN = "contained-in";

        /**
         * Relationship name for "part-of" relationships.
         * Used when a product is part of another product.
         */
        public static final String PART_OF = "part-of";

        /**
         * Relationship name for "has-part" relationships.
         * Used when a product has another product as a part.
         */
        public static final String HAS_PART = "has-part";

        /**
         * Relationship name for "successor-of" relationships.
         * Used when a product is a successor of another product.
         */
        public static final String SUCCESSOR_OF = "successor-of";

        /**
         * Relationship name for "predecessor-of" relationships.
         * Used when a product is a predecessor of another product.
         */
        public static final String PREDECESSOR_OF = "predecessor-of";

        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private Relationship() {
        }
    }

    /**
     * Constants related to recommendation strategies.
     * This class contains the names of basic and composite strategies used for product recommendations.
     */
    public static final class Strategy {
        /**
         * Strategy code for the sibling products strategy (S1).
         * Recommends products that share a common parent category with the reference product.
         */
        public static final String S1 = "S1";

        /**
         * Strategy code for the successor products strategy (S2).
         * Recommends products that are direct or indirect successors of the reference product.
         */
        public static final String S2 = "S2";

        /**
         * Strategy code for the predecessor products strategy (S3).
         * Recommends products that are direct or indirect predecessors of the reference product.
         */
        public static final String S3 = "S3";

        /**
         * Keyword for the intersection operation in composite recommendation strategies.
         * Returns only products recommended by both constituent strategies.
         */
        public static final String INTERSECTION = "INTERSECTION";

        /**
         * Keyword for the union operation in composite recommendation strategies.
         * Returns all products recommended by either constituent strategy.
         */
        public static final String UNION = "UNION";

        /**
         * Private constructor to prevent instantiation of this utility class.
         */
        private Strategy() {
        }
    }
}