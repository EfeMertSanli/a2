package strategy;

import model.Graph;
import model.Product;
import java.util.HashSet;
import java.util.Set;

/**
 * Strategy that computes the union of two other strategies.
 * Each strategy is applied with its own reference product ID.
 *
 * @author uuifx
 */
public class UnionStrategy implements RecommendationStrategy {
    private final RecommendationStrategy strategy1;
    private final int productId1;
    private final RecommendationStrategy strategy2;
    private final int productId2;

    /**
     * Creates a new union strategy with the given strategies and their product IDs.
     *
     * @param strategy1 The first strategy
     * @param productId1 The product ID for the first strategy
     * @param strategy2 The second strategy
     * @param productId2 The product ID for the second strategy
     * @throws IllegalArgumentException if either strategy is null
     */
    public UnionStrategy(RecommendationStrategy strategy1, int productId1,
                         RecommendationStrategy strategy2, int productId2) {
        if (strategy1 == null || strategy2 == null) {
            throw new IllegalArgumentException("Strategies must not be null");
        }
        this.strategy1 = strategy1;
        this.productId1 = productId1;
        this.strategy2 = strategy2;
        this.productId2 = productId2;
    }

    @Override
    public Set<Product> getRecommendations(int unusedProductId, Graph graph) {
        // Use the stored product IDs, not the parameter
        Set<Product> recommendations1 = strategy1.getRecommendations(productId1, graph);
        Set<Product> recommendations2 = strategy2.getRecommendations(productId2, graph);

        // Compute the union
        Set<Product> union = new HashSet<>(recommendations1);
        union.addAll(recommendations2);

        return union;
    }
}