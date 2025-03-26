package util;

import model.Edge;
import model.Graph;
import model.Node;
import model.Product;
import model.RelationshipTypes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for command validation and helper methods.
 * @author uuifx
 */
public final class CommandValidationUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private CommandValidationUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Checks if a relationship is valid between two node types.
     *
     * @param relationship The relationship type
     * @param isSubjectProduct Whether the subject is a product
     * @param isObjectProduct Whether the object is a product
     * @return true if the relationship is valid, false otherwise
     */
    public static boolean isValidRelationship(RelationshipTypes relationship,
                                              boolean isSubjectProduct,
                                              boolean isObjectProduct) {
        // Validate contained-in: target must be a category
        if (relationship == RelationshipTypes.CONTAINED_IN && isObjectProduct) {
            return false;
        }

        // Validate contains: source must be a category
        if (relationship == RelationshipTypes.CONTAINS && isSubjectProduct) {
            return false;
        }

        // For product-product relationships: both must be products
        if (isProductToProductRelationship(relationship) && (!isSubjectProduct || !isObjectProduct)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a relationship is one that must connect two products.
     * @param relationship The relationship type
     * @return true if both nodes must be products
     */
    private static boolean isProductToProductRelationship(RelationshipTypes relationship) {
        return relationship == RelationshipTypes.PART_OF
                ||
                relationship == RelationshipTypes.HAS_PART
                ||
                relationship == RelationshipTypes.SUCCESSOR_OF
                ||
                relationship == RelationshipTypes.PREDECESSOR_OF;
    }

    /**
     * Checks if a product ID already exists with a different name.
     * @param productId The product ID to check
     * @param productName The product name to check
     * @param graph The graph to check in
     * @return true if a conflict exists, false otherwise
     */
    public static boolean hasConflictingProductId(int productId, String productName, Graph graph) {
        for (Product existingProduct : graph.getProducts()) {
            if (existingProduct.getId() == productId
                    && !existingProduct.getLowerCaseName().equals(productName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if a product name already exists with a different ID.
     * @param name The product name
     * @param id The product ID
     * @param graph The graph to check
     * @return true if a conflict exists
     */
    public static boolean hasConflictingProductName(String name, int id, Graph graph) {
        for (Product product : graph.getProducts()) {
            if (product.getLowerCaseName().equals(name.toLowerCase()) && product.getId() != id) {
                return true;
            }
        }
        return false;
    }
    /**
     * Extracts the node name from a node string.
     * @param nodeStr The node string
     * @return The extracted node name
     */
    public static String extractNodeName(String nodeStr) {
        // Try to match as a product
        Pattern productPattern = Pattern.compile(Constants.Regex.PRODUCT_REGEX);
        Matcher productMatcher = productPattern.matcher(nodeStr);
        if (productMatcher.matches()) {
            return productMatcher.group(1);
        }
        // Try to match as a category
        Pattern categoryPattern = Pattern.compile(Constants.Regex.CATEGORY_REGEX);
        Matcher categoryMatcher = categoryPattern.matcher(nodeStr);
        if (categoryMatcher.matches()) {
            return categoryMatcher.group(1);
        }
        // If all else fails, return the original string
        return nodeStr;
    }

    /**
     * Sorts a list of nodes by their lowercase name.
     * @param nodes The list of nodes to sort
     */
    public static void sortNodesByName(List<Node> nodes) {
        for (int i = 0; i < nodes.size() - 1; i++) {
            for (int j = 0; j < nodes.size() - i - 1; j++) {
                if (nodes.get(j).getLowerCaseName().compareTo(
                        nodes.get(j + 1).getLowerCaseName()) > 0) {
                    // Swap nodes
                    Node temp = nodes.get(j);
                    nodes.set(j, nodes.get(j + 1));
                    nodes.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Sorts a list of products by their lowercase name.
     * @param products The list of products to sort
     */
    public static void sortProductsByName(List<Product> products) {
        for (int i = 0; i < products.size() - 1; i++) {
            for (int j = 0; j < products.size() - i - 1; j++) {
                if (products.get(j).getLowerCaseName().compareTo(
                        products.get(j + 1).getLowerCaseName()) > 0) {
                    // Swap products
                    Product temp = products.get(j);
                    products.set(j, products.get(j + 1));
                    products.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Sorts edges by source name, target name, and relationship.
     * @param edges The list of edges to sort
     */
    public static void sortEdges(List<Edge> edges) {
        for (int i = 0; i < edges.size() - 1; i++) {
            for (int j = 0; j < edges.size() - i - 1; j++) {
                if (compareEdges(edges.get(j), edges.get(j + 1)) > 0) {
                    Edge temp = edges.get(j);
                    edges.set(j, edges.get(j + 1));
                    edges.set(j + 1, temp);
                }
            }
        }
    }


    /**
     * Compares two edges for sorting.
     * @param e1 The first edge
     * @param e2 The second edge
     * @return Comparison result
     */
    private static int compareEdges(Edge e1, Edge e2) {
        int sourceComp = e1.getSource().getLowerCaseName().compareTo(e2.getSource().getLowerCaseName());
        if (sourceComp != 0) {
            return sourceComp;
        }
        int targetComp = e1.getTarget().getLowerCaseName().compareTo(e2.getTarget().getLowerCaseName());
        if (targetComp != 0) {
            return targetComp;
        }
        // Sort by relationship according to A.2.2 order
        return getRelationshipOrder(e1.getRelationship()) - getRelationshipOrder(e2.getRelationship());
    }

    /**
     * Gets the order index for a relationship type based on the specification.
     *
     * @param relationship The relationship type
     * @return The order index
     */
    private static int getRelationshipOrder(RelationshipTypes relationship) {
        switch (relationship) {
            case CONTAINS: return 0;
            case CONTAINED_IN: return 1;
            case PART_OF: return 2;
            case HAS_PART: return 3;
            case SUCCESSOR_OF: return 4;
            case PREDECESSOR_OF: return 5;
            default: return 6;
        }
    }

    /**
     * Finds a product by its ID.
     * @param productId The product ID
     * @param graph The product graph
     * @return The product, or null if not found
     */
    public static Product findProductById(int productId, Graph graph) {
        for (Product product : graph.getProducts()) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    /**
     * Recursively finds the first product ID in a term.
     * @param term The term
     * @return The first product ID
     */
    public static int findFirstProductId(parser.RecommendCommandParser.RecommendTerm term) {
        if (term instanceof parser.RecommendCommandParser.FinalTerm) {
            return ((parser.RecommendCommandParser.FinalTerm) term).getProductId();
        } else if (term instanceof parser.RecommendCommandParser.IntersectionTerm) {
            parser.RecommendCommandParser.IntersectionTerm intersectionTerm =
                    (parser.RecommendCommandParser.IntersectionTerm) term;
            int leftId = findFirstProductId(intersectionTerm.getLeft());
            if (leftId != 0) {
                return leftId;
            }
            return findFirstProductId(intersectionTerm.getRight());
        } else if (term instanceof parser.RecommendCommandParser.UnionTerm) {
            parser.RecommendCommandParser.UnionTerm unionTerm =
                    (parser.RecommendCommandParser.UnionTerm) term;
            int leftId = findFirstProductId(unionTerm.getLeft());
            if (leftId != 0) {
                return leftId;
            }
            return findFirstProductId(unionTerm.getRight());
        }
        return 0;
    }
}