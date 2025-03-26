package strategy;

/**
 * Associates a recommendation strategy with its corresponding product ID.
 * This class enables composite strategies to apply their component strategies
 * with the correct product IDs.
 *
 * @author uuifx
 */
public class StrategyWithId {
    private final RecommendationStrategy strategy;
    private final int productId;

    /**
     * Creates a new StrategyWithId with the given strategy and product ID.
     *
     * @param strategy The recommendation strategy
     * @param productId The product ID to use with this strategy
     */
    public StrategyWithId(RecommendationStrategy strategy, int productId) {
        this.strategy = strategy;
        this.productId = productId;
    }

    /**
     * Gets the recommendation strategy.
     *
     * @return The recommendation strategy
     */
    public RecommendationStrategy getStrategy() {
        return strategy;
    }

    /**
     * Gets the product ID.
     *
     * @return The product ID
     */
    public int getProductId() {
        return productId;
    }
}