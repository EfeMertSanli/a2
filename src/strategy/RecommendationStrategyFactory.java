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
public final class RecommendationStrategyFactory {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private RecommendationStrategyFactory() {
        // Utility class should not be instantiated
    }

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

        StrategyWithId strategyWithId = createStrategyWithId(term);
        return strategyWithId.getStrategy();
    }

    /**
     * Creates a StrategyWithId from a parsed recommendation term.
     * This method is used internally to pass product IDs to composite strategies.
     *
     * @param term The parsed recommendation term
     * @return A StrategyWithId containing the strategy and its product ID
     * @throws IllegalArgumentException If the term is invalid
     */
    private static StrategyWithId createStrategyWithId(RecommendTerm term) {
        if (term instanceof FinalTerm) {
            FinalTerm finalTerm = (FinalTerm) term;
            RecommendationStrategy strategy = createFinalStrategy(finalTerm.getStrategy());
            return new StrategyWithId(strategy, finalTerm.getProductId());
        } else if (term instanceof IntersectionTerm) {
            return createIntersectionStrategy((IntersectionTerm) term);
        } else if (term instanceof UnionTerm) {
            return createUnionStrategy((UnionTerm) term);
        } else {
            throw new IllegalArgumentException("Unknown term type: " + term.getClass().getName());
        }
    }

    /**
     * Creates a basic recommendation strategy from a strategy type.
     *
     * @param strategyType The strategy type (S1, S2, S3)
     * @return The recommendation strategy
     * @throws IllegalArgumentException If the strategy type is invalid
     */
    private static RecommendationStrategy createFinalStrategy(String strategyType) {
        switch (strategyType) {
            case "S1":
                return new SiblingProductStrategy();
            case "S2":
                return new SuccessorProductStrategy();
            case "S3":
                return new PredecessorProductStrategy();
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
        }
    }

    /**
     * Creates a StrategyWithId for an intersection strategy.
     *
     * @param term The intersection term
     * @return A StrategyWithId containing the intersection strategy and a product ID
     */
    private static StrategyWithId createIntersectionStrategy(IntersectionTerm term) {
        StrategyWithId leftStrategyWithId = createStrategyWithId(term.getLeft());
        StrategyWithId rightStrategyWithId = createStrategyWithId(term.getRight());

        IntersectionStrategy strategy = new IntersectionStrategy(
                leftStrategyWithId.getStrategy(), leftStrategyWithId.getProductId(),
                rightStrategyWithId.getStrategy(), rightStrategyWithId.getProductId());

        // Use the first strategy's product ID as the reference ID for the composite strategy
        return new StrategyWithId(strategy, leftStrategyWithId.getProductId());
    }

    /**
     * Creates a StrategyWithId for a union strategy.
     *
     * @param term The union term
     * @return A StrategyWithId containing the union strategy and a product ID
     */
    private static StrategyWithId createUnionStrategy(UnionTerm term) {
        StrategyWithId leftStrategyWithId = createStrategyWithId(term.getLeft());
        StrategyWithId rightStrategyWithId = createStrategyWithId(term.getRight());

        UnionStrategy strategy = new UnionStrategy(
                leftStrategyWithId.getStrategy(), leftStrategyWithId.getProductId(),
                rightStrategyWithId.getStrategy(), rightStrategyWithId.getProductId());

        // Use the first strategy's product ID as the reference ID for the composite strategy
        return new StrategyWithId(strategy, leftStrategyWithId.getProductId());
    }
}