package strategy;

import model.*;
import java.util.*;

/**
 * Strategy for recommending predecessor products (S3).
 * Finds all direct and indirect predecessor products of the reference product.
 *
 * @author uuifx
 */
public class PredecessorProductStrategy implements RecommendationStrategy {

    @Override
    public Set<Product> getRecommendations(int referenceProductId, Graph graph) {
        Set<Product> recommendations = new HashSet<>();

        // Find the reference product
        Product referenceProduct = findProductById(referenceProductId, graph);
        if (referenceProduct == null) {
            return recommendations;
        }

        // Find all predecessor products using depth-first search
        Set<Node> visited = new HashSet<>();
        findPredecessors(referenceProduct, graph, recommendations, visited);

        return recommendations;
    }

    /**
     * Recursively finds all predecessor products using depth-first search.
     *
     * @param current The current product
     * @param graph The product graph
     * @param recommendations The set of recommendations to fill
     * @param visited The set of visited nodes
     */
    private void findPredecessors(Product current, Graph graph, Set<Product> recommendations, Set<Node> visited) {
        visited.add(current);

        // Get all outgoing successor-of edges (since we're looking for predecessors)
        Set<Edge> successorOfEdges = graph.getOutgoingEdgesByRelationship(current, RelationshipTypes.SUCCESSOR_OF);

        for (Edge edge : successorOfEdges) {
            Node target = edge.getTarget();
            if (target.isProduct() && !visited.contains(target)) {
                Product predecessor = (Product) target;
                recommendations.add(predecessor);
                // Recursively find predecessors of this predecessor
                findPredecessors(predecessor, graph, recommendations, visited);
            }
        }
    }

    /**
     * Finds a product by its ID.
     *
     * @param productId The product ID
     * @param graph The product graph
     * @return The product, or null if not found
     */
    private Product findProductById(int productId, Graph graph) {
        for (Product product : graph.getProducts()) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
}
