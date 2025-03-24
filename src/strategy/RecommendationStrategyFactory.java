package strategy;

import parser.RecommendCommandParser.RecommendTerm;
import parser.RecommendCommandParser.FinalTerm;
import parser.RecommendCommandParser.IntersectionTerm;
import parser.RecommendCommandParser.UnionTerm;

/**
 * Factory for creating recommendation strategies from parsed recommendation terms.
 *
 * @author uuifx
 */
public class RecommendationStrategyFactory {

    /**
     * Creates a recommendation strategy from a parsed recommendation term.
     *
     * @param term The parsed recommendation term
     * @return The recommendation strategy
     * @throws IllegalArgumentException If the term is invalid
     */
    public static RecommendationStrategy createStrategy(RecommendTerm term) {
        if (term == null) {
            throw new IllegalArgumentException("Term must not be null");
        }

        if (term instanceof FinalTerm) {
            return createFinalStrategy((FinalTerm) term);
        } else if (term instanceof IntersectionTerm) {
            return createIntersectionStrategy((IntersectionTerm) term);
        } else if (term instanceof UnionTerm) {
            return createUnionStrategy((UnionTerm) term);
        } else {
            throw new IllegalArgumentException("Unknown term type: " + term.getClass().getName());
        }
    }

    /**
     * Creates a recommendation strategy from a final term.
     *
     * @param term The final term
     * @return The recommendation strategy
     * @throws IllegalArgumentException If the strategy type is invalid
     */
    private static RecommendationStrategy createFinalStrategy(FinalTerm term) {
        String strategy = term.getStrategy();

        switch (strategy) {
            case "S1":
                return new SiblingProductStrategy();
            case "S2":
                return new SuccessorProductStrategy();
            case "S3":
                return new PredecessorProductStrategy();
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + strategy);
        }
    }

    /**
     * Creates an intersection strategy from an intersection term.
     *
     * @param term The intersection term
     * @return The intersection strategy
     */
    private static RecommendationStrategy createIntersectionStrategy(IntersectionTerm term) {
        RecommendationStrategy leftStrategy = createStrategy(term.getLeft());
        RecommendationStrategy rightStrategy = createStrategy(term.getRight());

        return new IntersectionStrategy(leftStrategy, rightStrategy);
    }

    /**
     * Creates a union strategy from a union term.
     *
     * @param term The union term
     * @return The union strategy
     */
    private static RecommendationStrategy createUnionStrategy(UnionTerm term) {
        RecommendationStrategy leftStrategy = createStrategy(term.getLeft());
        RecommendationStrategy rightStrategy = createStrategy(term.getRight());

        return new UnionStrategy(leftStrategy, rightStrategy);
    }
}