package strategy;

import model.*;
import java.util.*;

/**
 * Strategy that computes the union of two other strategies.
 *
 * @author u-student
 */
public class UnionStrategy implements RecommendationStrategy {
    private final RecommendationStrategy strategy1;
    private final RecommendationStrategy strategy2;

    /**
     * Creates a new union strategy with the given strategies.
     *
     * @param strategy1 The first strategy
     * @param strategy2 The second strategy
     */
    public UnionStrategy(RecommendationStrategy strategy1, RecommendationStrategy strategy2) {
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

        // Compute the union
        Set<Product> union = new HashSet<>(recommendations1);
        union.addAll(recommendations2);

        return union;
    }
}