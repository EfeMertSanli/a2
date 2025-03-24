package parser;

import util.Constants.CLI;
import util.Constants.Error;
import util.Constants.Regex;
import util.Constants.Strategy;

/**
 * Parser for the recommend command using a recursive descent parser.
 *
 * @author u-student
 */
public class RecommendedCommandParser {
    private static final int COMMAND_PARTS = 2;
    private static final int COMMAND_INDEX = 0;
    private static final int CONTENT_INDEX = 1;
    private static final int STRATEGY_LENGTH = 2;
    private static final int INTERSECTION_LENGTH = 12;
    private static final int UNION_LENGTH = 5;
    private static final char OPEN_PAREN = '(';
    private static final char CLOSE_PAREN = ')';
    private static final char COMMA = ',';

    // Input string and current position
    private String input;
    private int position;

    /**
     * Parses a recommend command.
     *
     * @param command The command string
     * @return The root node of the parse tree
     * @throws IllegalArgumentException If the command is invalid
     */
    public RecommendTerm parse(String command) {
        if (command == null || !command.trim().toLowerCase().startsWith(CLI.RECOMMEND)) {
            throw new IllegalArgumentException("Command must start with 'recommend'");
        }

        // Extract the term part (after "recommend")
        String[] parts = command.trim().split(Regex.COMMAND_SPLIT_REGEX, COMMAND_PARTS);
        if (parts.length < COMMAND_PARTS) {
            throw new IllegalArgumentException(Error.MISSING_TERM);
        }

        // Set up parser state
        input = parts[CONTENT_INDEX].trim();
        position = 0;

        // Parse the term
        RecommendTerm result = parseTerm();

        // Check if we've consumed the entire input
        skipWhitespace();
        if (position < input.length()) {
            throw new IllegalArgumentException(Error.UNEXPECTED_INPUT + input.substring(position));
        }

        return result;
    }

    /**
     * Parses a term in the grammar.
     * term ::= final | INTERSECTION(term, term) | UNION(term, term)
     */
    private RecommendTerm parseTerm() {
        skipWhitespace();

        // Try to parse INTERSECTION or UNION
        if (position + INTERSECTION_LENGTH <= input.length() &&
                input.substring(position, position + INTERSECTION_LENGTH).equalsIgnoreCase(Strategy.INTERSECTION)) {
            return parseIntersection();
        } else if (position + UNION_LENGTH <= input.length() &&
                input.substring(position, position + UNION_LENGTH).equalsIgnoreCase(Strategy.UNION)) {
            return parseUnion();
        } else {
            // Must be a final term
            return parseFinal();
        }
    }

    /**
     * Parses an INTERSECTION term.
     * INTERSECTION(term, term)
     */
    private RecommendTerm parseIntersection() {
        // Consume "INTERSECTION"
        position += INTERSECTION_LENGTH;
        skipWhitespace();

        // Expect a '('
        if (position >= input.length() || input.charAt(position) != OPEN_PAREN) {
            throw new IllegalArgumentException(Error.EXPECTED_OPEN_PAREN + Strategy.INTERSECTION);
        }
        position++;

        // Parse the first term
        skipWhitespace();
        RecommendTerm left = parseTerm();

        // Expect a ','
        skipWhitespace();
        if (position >= input.length() || input.charAt(position) != COMMA) {
            throw new IllegalArgumentException(Error.EXPECTED_COMMA + Strategy.INTERSECTION);
        }
        position++;

        // Parse the second term
        skipWhitespace();
        RecommendTerm right = parseTerm();

        // Expect a ')'
        skipWhitespace();
        if (position >= input.length() || input.charAt(position) != CLOSE_PAREN) {
            throw new IllegalArgumentException(Error.EXPECTED_CLOSE_PAREN + Strategy.INTERSECTION);
        }
        position++;

        return new IntersectionTerm(left, right);
    }

    /**
     * Parses a UNION term.
     * UNION(term, term)
     */
    private RecommendTerm parseUnion() {
        // Consume "UNION"
        position += UNION_LENGTH;
        skipWhitespace();

        // Expect a '('
        if (position >= input.length() || input.charAt(position) != OPEN_PAREN) {
            throw new IllegalArgumentException(Error.EXPECTED_OPEN_PAREN + Strategy.UNION);
        }
        position++;

        // Parse the first term
        skipWhitespace();
        RecommendTerm left = parseTerm();

        // Expect a ','
        skipWhitespace();
        if (position >= input.length() || input.charAt(position) != COMMA) {
            throw new IllegalArgumentException(Error.EXPECTED_COMMA + Strategy.UNION);
        }
        position++;

        // Parse the second term
        skipWhitespace();
        RecommendTerm right = parseTerm();

        // Expect a ')'
        skipWhitespace();
        if (position >= input.length() || input.charAt(position) != CLOSE_PAREN) {
            throw new IllegalArgumentException(Error.EXPECTED_CLOSE_PAREN + Strategy.UNION);
        }
        position++;

        return new UnionTerm(left, right);
    }

    /**
     * Parses a final term.
     * final ::= strategy productid
     */
    private RecommendTerm parseFinal() {
        skipWhitespace();

        // Parse the strategy
        if (position >= input.length()) {
            throw new IllegalArgumentException(Error.EXPECTED_STRATEGY);
        }

        // Check if the next token is a strategy
        if (position + STRATEGY_LENGTH <= input.length() &&
                (input.substring(position, position + STRATEGY_LENGTH).equals(Strategy.S1) ||
                        input.substring(position, position + STRATEGY_LENGTH).equals(Strategy.S2) ||
                        input.substring(position, position + STRATEGY_LENGTH).equals(Strategy.S3))) {

            String strategy = input.substring(position, position + STRATEGY_LENGTH);
            position += STRATEGY_LENGTH;

            // Parse the product ID
            skipWhitespace();

            // Extract the product ID (sequence of digits)
            int startPos = position;
            while (position < input.length() && Character.isDigit(input.charAt(position))) {
                position++;
            }

            if (startPos == position) {
                throw new IllegalArgumentException(Error.EXPECTED_PRODUCT_ID + strategy);
            }

            int productId = Integer.parseInt(input.substring(startPos, position));

            return new FinalTerm(strategy, productId);
        } else {
            throw new IllegalArgumentException(Error.EXPECTED_STRATEGY);
        }
    }

    /**
     * Skips whitespace in the input.
     */
    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    /**
     * Base class for terms in the recommend command syntax tree.
     */
    public static abstract class RecommendTerm {
        /**
         * Evaluates this term to produce a set of recommended product IDs.
         * This should be implemented by the recommendation system.
         */
        public abstract String toString();
    }

    /**
     * Final term representing a strategy applied to a product ID.
     */
    public static class FinalTerm extends RecommendTerm {
        private final String strategy;
        private final int productId;

        public FinalTerm(String strategy, int productId) {
            this.strategy = strategy;
            this.productId = productId;
        }

        public String getStrategy() {
            return strategy;
        }

        public int getProductId() {
            return productId;
        }

        @Override
        public String toString() {
            return strategy + CLI.SPACE + productId;
        }
    }

    /**
     * Intersection term representing the intersection of two terms.
     */
    public static class IntersectionTerm extends RecommendTerm {
        private final RecommendTerm left;
        private final RecommendTerm right;

        public IntersectionTerm(RecommendTerm left, RecommendTerm right) {
            this.left = left;
            this.right = right;
        }

        public RecommendTerm getLeft() {
            return left;
        }

        public RecommendTerm getRight() {
            return right;
        }

        @Override
        public String toString() {
            return Strategy.INTERSECTION + OPEN_PAREN + left + ", " + right + CLOSE_PAREN;
        }
    }

    /**
     * Union term representing the union of two terms.
     */
    public static class UnionTerm extends RecommendTerm {
        private final RecommendTerm left;
        private final RecommendTerm right;

        public UnionTerm(RecommendTerm left, RecommendTerm right) {
            this.left = left;
            this.right = right;
        }

        public RecommendTerm getLeft() {
            return left;
        }

        public RecommendTerm getRight() {
            return right;
        }

        @Override
        public String toString() {
            return Strategy.UNION + OPEN_PAREN + left + ", " + right + CLOSE_PAREN;
        }
    }
}