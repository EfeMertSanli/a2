package strategy;

import model.Category;
import model.Edge;
import model.Graph;
import model.Node;
import model.Product;
import model.RelationshipTypes;
import java.util.HashSet;
import java.util.Set;

/**
 * Strategy for recommending sibling products (S1).
 * Finds products that share a common parent category with the reference product.
 *
 * @author uuifx
 */
public class SiblingProductStrategy implements RecommendationStrategy {

    @Override
    public Set<Product> getRecommendations(int referenceProductId, Graph graph) {
        Set<Product> recommendations = new HashSet<>();

        // Find the reference product
        Product referenceProduct = findProductById(referenceProductId, graph);
        if (referenceProduct == null) {
            return recommendations;
        }

        // Find all parent categories of the reference product
        Set<Category> parentCategories = findParentCategories(referenceProduct, graph);

        // For each parent category, find all its child products
        for (Category category : parentCategories) {
            Set<Product> siblings = findChildProducts(category, graph);
            siblings.remove(referenceProduct); // Exclude the reference product
            recommendations.addAll(siblings);
        }

        return recommendations;
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

    /**
     * Finds all parent categories of a product.
     *
     * @param product The product
     * @param graph The product graph
     * @return A set of parent categories
     */
    private Set<Category> findParentCategories(Product product, Graph graph) {
        Set<Category> parents = new HashSet<>();

        // Get all outgoing contained-in edges
        Set<Edge> containedInEdges = graph.getOutgoingEdgesByRelationship(product, RelationshipTypes.CONTAINED_IN);

        for (Edge edge : containedInEdges) {
            Node target = edge.getTarget();
            if (target.isCategory()) {
                parents.add((Category) target);
            }
        }

        return parents;
    }

    /**
     * Finds all child products of a category.
     *
     * @param category The category
     * @param graph The product graph
     * @return A set of child products
     */
    private Set<Product> findChildProducts(Category category, Graph graph) {
        Set<Product> children = new HashSet<>();

        // Get all outgoing contains edges
        Set<Edge> containsEdges = graph.getOutgoingEdgesByRelationship(category, RelationshipTypes.CONTAINS);

        for (Edge edge : containsEdges) {
            Node target = edge.getTarget();
            if (target.isProduct()) {
                children.add((Product) target);
            }
        }

        return children;
    }
}