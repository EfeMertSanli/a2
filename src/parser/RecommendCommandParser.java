package parser;

import util.Constants;
import util.Constants.CLI;
import util.Constants.Error;
import util.Constants.Regex;
import util.Constants.Strategy;

import static parser.CommandParserConstants.CONTENT_INDEX;
import static parser.RecommendCommandParserConstants.OPEN_PAREN;
import static parser.RecommendCommandParserConstants.COMMA;
import static parser.RecommendCommandParserConstants.CLOSE_PAREN;
import static parser.RecommendCommandParserConstants.COMMAND_PARTS;
import static parser.RecommendCommandParserConstants.INTERSECTION_LENGTH;
import static parser.RecommendCommandParserConstants.STRATEGY_LENGTH;
import static parser.RecommendCommandParserConstants.UNION_LENGTH;
/**
 * Parser for the recommend command using a recursive descent parser.
 *
 * @author uuifx
 */
public class RecommendCommandParser {
    // Input string and current position
    private String input;
    private int position;
    /**
     * Parses a recommend command.
     * @param command The command string
     * @return The root node of the parse tree
     * @throws IllegalArgumentException If the command is invalid
     */
    public RecommendTerm parse(String command) {
        if (command == null || !command.trim().toLowerCase().startsWith(CLI.RECOMMEND)) {
            throw new IllegalArgumentException(Constants.Error.MUST_START_WITH_RECOMMEND);
        }
        // Extract the term part (after "recommend")
        String[] parts = command.trim().split(Regex.COMMAND_SPLIT_REGEX, COMMAND_PARTS);
        if (parts.length < COMMAND_PARTS) {
            throw new IllegalArgumentException(Error.MISSING_TERM);
        }

        // Set up parser state
        input = parts[CONTENT_INDEX].trim();
        position = DatabaseParserConstants.LINE_START - 1;
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
        if (position + INTERSECTION_LENGTH <= input.length()
                && input.substring(position, position + INTERSECTION_LENGTH).equalsIgnoreCase(Strategy.INTERSECTION)) {
            return parseIntersection();
        } else if (position + UNION_LENGTH <= input.length()
                && input.substring(position, position + UNION_LENGTH).equalsIgnoreCase(Strategy.UNION)) {
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
        if (position + STRATEGY_LENGTH <= input.length()
                && (input.substring(position, position + STRATEGY_LENGTH).equals(Strategy.S1)
                || input.substring(position, position + STRATEGY_LENGTH).equals(Strategy.S2)
                || input.substring(position, position + STRATEGY_LENGTH).equals(Strategy.S3))) {

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
    public abstract static class RecommendTerm {
        /**
         * Evaluates this term to produce a set of recommended product IDs.
         * This should be implemented by the recommendation system.
         *
         * @return String representation of this term
         */
        public abstract String toString();
    }

    /**
     * Final term representing a strategy applied to a product ID.
     */
    public static class FinalTerm extends RecommendTerm {
        private final String strategy;
        private final int productId;

        /**
         * Creates a new final term with the specified strategy and product ID.
         *
         * @param strategy the recommendation strategy
         * @param productId the ID of the product
         */
        public FinalTerm(String strategy, int productId) {
            this.strategy = strategy;
            this.productId = productId;
        }

        /**
         * Gets the strategy for this term.
         *
         * @return the strategy code
         */
        public String getStrategy() {
            return strategy;
        }

        /**
         * Gets the product ID for this term.
         *
         * @return the product ID
         */
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

        /**
         * Creates a new intersection term with the specified left and right terms.
         * @param left the left term
         * @param right the right term
         */
        public IntersectionTerm(RecommendTerm left, RecommendTerm right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Gets the left term of this intersection.
         *
         * @return the left term
         */
        public RecommendTerm getLeft() {
            return left;
        }

        /**
         * Gets the right term of this intersection.
         *
         * @return the right term
         */
        public RecommendTerm getRight() {
            return right;
        }

        @Override
        public String toString() {
            return Strategy.INTERSECTION + OPEN_PAREN + left + RecommendCommandParserConstants.TERM_SEPARATOR + right + CLOSE_PAREN;
        }
    }

    /**
     * Union term representing the union of two terms.
     */
    public static class UnionTerm extends RecommendTerm {
        private final RecommendTerm left;
        private final RecommendTerm right;

        /**
         * Creates a new union term with the specified left and right terms.
         *
         * @param left the left term
         * @param right the right term
         */
        public UnionTerm(RecommendTerm left, RecommendTerm right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Gets the left term of this union.
         *
         * @return the left term
         */
        public RecommendTerm getLeft() {
            return left;
        }

        /**
         * Gets the right term of this union.
         *
         * @return the right term
         */
        public RecommendTerm getRight() {
            return right;
        }

        @Override
        public String toString() {
            return Strategy.UNION + parser.RecommendCommandParserConstants.OPEN_PAREN + left
                    + RecommendCommandParserConstants.TERM_SEPARATOR + right + RecommendCommandParserConstants.CLOSE_PAREN;
        }
    }
}