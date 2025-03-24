package util;

/**
 * Global constants for the recommendation system.
 *
 * @author u-student
 */
public final class Constants {
    // Prevent instantiation
    private Constants() {
    }

    /**
     * Constants related to the command line interface.
     */
    public static final class CLI {
        // Command names
        public static final String LOAD = "load";
        public static final String DATABASE = "database";
        public static final String QUIT = "quit";
        public static final String ADD = "add";
        public static final String REMOVE = "remove";
        public static final String NODES = "nodes";
        public static final String EDGES = "edges";
        public static final String RECOMMEND = "recommend";
        public static final String EXPORT = "export";

        // Command line prompt
        public static final String PROMPT = "> ";

        // Formatters
        public static final String SPACE = " ";
        public static final String EMPTY = "";
        public static final String NEW_LINE = "\n";

        private CLI() {
        }
    }

    /**
     * Constants related to errors and exceptions.
     */
    public static final class Error {
        public static final String PREFIX = "Error: ";
        public static final String ERROR_IN_LINE = "Error in line ";
        public static final String FAILED_READ_DATABASE = "Failed to read database file: ";
        public static final String FAILED_ADD_RELATIONSHIP = "Failed to add relationship: ";
        public static final String FAILED_REMOVE_RELATIONSHIP = "Failed to remove relationship: ";
        public static final String FAILED_PARSE_RECOMMEND = "Failed to parse recommend command: ";
        public static final String FAILED_PROCESS_RECOMMEND = "Failed to process recommend command: ";
        public static final String UNKNOWN_COMMAND = "Unknown command: ";
        public static final String NODE_NOT_FOUND = "Node not found";
        public static final String INVALID_RELATIONSHIP_TYPE = "Invalid relationship type: ";
        public static final String RELATIONSHIP_NOT_FOUND = "Relationship not found";
        public static final String INVALID_LINE_FORMAT = "Invalid line format: ";
        public static final String INVALID_NODE_FORMAT = "Invalid node format: ";
        public static final String INVALID_COMMAND_FORMAT = "Invalid command format, could not ";
        public static final String SPLIT_SUBJECT_OBJECT = "split into subject and object";
        public static final String EXTRACT_PREDICATE_OBJECT = "extract predicate and object";
        public static final String MISSING_TERM = "Invalid recommend command: missing term";
        public static final String EXPECTED_OPEN_PAREN = "Expected '(' after ";
        public static final String EXPECTED_COMMA = "Expected ',' after first term in ";
        public static final String EXPECTED_CLOSE_PAREN = "Expected ')' after second term in ";
        public static final String EXPECTED_STRATEGY = "Expected strategy (S1, S2, or S3)";
        public static final String EXPECTED_PRODUCT_ID = "Expected product ID after strategy ";
        public static final String UNEXPECTED_INPUT = "Unexpected input after term: ";
        public static final String NO_PRODUCT_ID = "No product ID found in term";
        public static final String UNKNOWN_TERM_TYPE = "Unknown term type: ";
        public static final String UNKNOWN_STRATEGY_TYPE = "Unknown strategy type: ";
        public static final String COMMAND_EMPTY = "Command string must not be empty";
        public static final String INVALID_LOAD_DATABASE = "Invalid load database command format: ";
        public static final String COMMAND_START_ADD_REMOVE = "Command must start with 'add' or 'remove'";

        private Error() {
        }
    }

    /**
     * Constants related to the regular expressions used for parsing.
     */
    public static final class Regex {
        // Node name pattern
        public static final String NODE_NAME_PATTERN = "[a-zA-Z0-9]+";

        // Product ID pattern
        public static final String PRODUCT_ID_PATTERN = "[0-9]+";

        // Regular expressions for parsing
        public static final String PRODUCT_REGEX = "(" + NODE_NAME_PATTERN + ")\\s*\\(\\s*id\\s*=\\s*(" + PRODUCT_ID_PATTERN + ")\\s*\\)";
        public static final String CATEGORY_REGEX = "(" + NODE_NAME_PATTERN + ")";
        public static final String PREDICATE_REGEX = "(contains|contained-in|part-of|has-part|successor-of|predecessor-of)";
        public static final String LINE_REGEX = "\\s*(.+?)\\s+" + PREDICATE_REGEX + "\\s+(.+?)\\s*";
        public static final String COMMAND_SPLIT_REGEX = "\\s+";
        public static final String PREDICATE_SPLIT_REGEX = "\\s+(contains|contained-in|part-of|has-part|successor-of|predecessor-of)\\s+";

        private Regex() {
        }
    }

    /**
     * Constants related to the DOT graph notation.
     */
    public static final class Dot {
        public static final String DIGRAPH_START = "digraph {";
        public static final String DIGRAPH_END = "}";
        public static final String ARROW = " -> ";
        public static final String LABEL_START = " [label=";
        public static final String LABEL_END = "]";
        public static final String SHAPE_BOX = " [shape=box]";
        public static final String INDENT = "  ";
        public static final String HYPHEN = "-";

        private Dot() {
        }
    }

    /**
     * Constants related to graph relationships.
     */
    public static final class Relationship {
        // Relationship names
        public static final String CONTAINS = "contains";
        public static final String CONTAINED_IN = "contained-in";
        public static final String PART_OF = "part-of";
        public static final String HAS_PART = "has-part";
        public static final String SUCCESSOR_OF = "successor-of";
        public static final String PREDECESSOR_OF = "predecessor-of";

        private Relationship() {
        }
    }

    /**
     * Constants related to recommendation strategies.
     */
    public static final class Strategy {
        public static final String S1 = "S1";
        public static final String S2 = "S2";
        public static final String S3 = "S3";
        public static final String INTERSECTION = "INTERSECTION";
        public static final String UNION = "UNION";

        private Strategy() {
        }
    }
}