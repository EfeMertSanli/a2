package strategy;

import model.*;
import java.util.*;

/**
 * Interface for product recommendation strategies.
 *
 * @author uuifx
 */
public interface RecommendationStrategy {

    /**
     * Gets product recommendations for a reference product.
     *
     * @param referenceProductId The ID of the reference product
     * @param graph The product graph
     * @return A set of recommended products
     */
    Set<Product> getRecommendations(int referenceProductId, Graph graph);
}