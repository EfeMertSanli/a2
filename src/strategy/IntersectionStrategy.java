package strategy;

import model.Graph;
import model.Product;
import java.util.HashSet;
import java.util.Set;

/**
 * Strategy that computes the intersection of two other strategies.
 *
 * @author uuifx
 */
public class IntersectionStrategy implements RecommendationStrategy {
    private final RecommendationStrategy strategy1;
    private final RecommendationStrategy strategy2;

    /**
     * Creates a new intersection strategy with the given strategies.
     *
     * @param strategy1 The first strategy
     * @param strategy2 The second strategy
     * @throws IllegalArgumentException if either strategy is null
     */
    public IntersectionStrategy(RecommendationStrategy strategy1, RecommendationStrategy strategy2) {
        if (strategy1 == null || strategy2 == null) {
            throw new IllegalArgumentException("Strategies must not be null");
        }
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public Set<Product> getRecommendations(int referenceProductId, Graph graph) {
        Set<Product> recommendations1 = strategy1.getRecommendations(referenceProductId, graph);
        Set<Product> recommendations2 = strategy2.getRecommendations(referenceProductId, graph);

        // Compute the intersection
        Set<Product> intersection = new HashSet<>(recommendations1);
        intersection.retainAll(recommendations2);

        return intersection;
    }
}