package strategy;

import model.*;
import java.util.*;

/**
 * Strategy for recommending successor products (S2).
 * Finds all direct and indirect successor products of the reference product.
 *
 * @author uuifx
 */
public class SuccessorProductStrategy implements RecommendationStrategy {

    @Override
    public Set<Product> getRecommendations(int referenceProductId, Graph graph) {
        Set<Product> recommendations = new HashSet<>();

        // Find the reference product
        Product referenceProduct = findProductById(referenceProductId, graph);
        if (referenceProduct == null) {
            return recommendations;
        }

        // Find all successor products using depth-first search
        Set<Node> visited = new HashSet<>();
        findSuccessors(referenceProduct, graph, recommendations, visited);

        return recommendations;
    }

    /**
     * Recursively finds all successor products using depth-first search.
     *
     * @param current The current product
     * @param graph The product graph
     * @param recommendations The set of recommendations to fill
     * @param visited The set of visited nodes
     */
    private void findSuccessors(Product current, Graph graph, Set<Product> recommendations, Set<Node> visited) {
        visited.add(current);

        // Get all outgoing predecessor-of edges (since we're looking for successors)
        Set<Edge> predecessorOfEdges = graph.getOutgoingEdgesByRelationship(current, RelationshipTypes.PREDECESSOR_OF);

        for (Edge edge : predecessorOfEdges) {
            Node target = edge.getTarget();
            if (target.isProduct() && !visited.contains(target)) {
                Product successor = (Product) target;
                recommendations.add(successor);
                // Recursively find successors of this successor
                findSuccessors(successor, graph, recommendations, visited);
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
