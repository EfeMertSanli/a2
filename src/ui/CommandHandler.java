package ui;
import model.Node;
import model.Graph;
import model.Edge;
import model.Product;
import model.RelationshipTypes;
import parser.CommandParser;
import parser.DatabaseParser;
import parser.RecommendCommandParser;
import strategy.RecommendationStrategy;
import strategy.RecommendationStrategyFactory;
import io.DotExport;
import util.Constants;
import util.Constants.CLI;
import util.Constants.Error;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Handles execution of commands for the product recommendation system.
 * @author uuifx
 */
public final class CommandHandler {
    // Array indices for add/remove command parts
    private static final int SUBJECT_INDEX = 0;
    private static final int PREDICATE_INDEX = 1;
    private static final int OBJECT_INDEX = 2;
    /**
     * Private constructor to prevent instantiation.
     */
    private CommandHandler() {
        // Utility class should not be instantiated
    }
    /**
     * Handles the load database command.
     * @param commandStr The command string
     * @param graph The graph to load into
     * @param commandParser The command parser to use
     * @throws IOException
     */
    public static void handleLoadDatabase(String commandStr, Graph graph, CommandParser commandParser) throws IOException {
        String filePath = commandParser.parseLoadDatabase(commandStr);
        // Create a new Graph for the database
        graph.clear();
        // Parse the database file line by line to print it verbatim
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(Error.PREFIX + Error.FAILED_READ_DATABASE + e.getMessage());
            return;
        }
        // Now actually parse the database and build the Graph
        DatabaseParser parser = new DatabaseParser(graph);
        parser.parseFile(filePath);
    }
    /**
     * Handles the add command.
     * @param commandStr The command string
     * @param graph The graph to add to
     * @param commandParser The command parser to use
     */
    public static void handleAddCommand(String commandStr, Graph graph, CommandParser commandParser) {
        String[] parts = commandParser.parseAddOrRemove(commandStr);
        String subject = parts[SUBJECT_INDEX];
        String predicate = parts[PREDICATE_INDEX];
        String object = parts[OBJECT_INDEX];
        // Check if subject is a product
        Pattern productPattern = Pattern.compile(Constants.Regex.PRODUCT_REGEX);
        Matcher subjectMatcher = productPattern.matcher(subject);
        if (subjectMatcher.matches()) {
            String productName = subjectMatcher.group(1);
            int productId = Integer.parseInt(subjectMatcher.group(2));
            // Check if this ID exists with a different name
            if (hasConflictingProductId(productId, productName, graph)) {
                System.out.println("Error, Product ID " + productId
                        + " already exists with a different name");
                return;
            }
        }
        // Check if object is a product
        Matcher objectMatcher = productPattern.matcher(object);
        if (objectMatcher.matches()) {
            String productName = objectMatcher.group(1);
            int productId = Integer.parseInt(objectMatcher.group(2));
            // Check if this ID exists with a different name
            if (hasConflictingProductId(productId, productName, graph)) {
                System.out.println("Error, Product ID " + productId
                        + " already exists with a different name");
                return;
            }
        }
        // If we reach here, it's safe to add
        DatabaseParser parser = new DatabaseParser(graph);
        parser.parseLine(subject + CLI.SPACE + predicate + CLI.SPACE + object);
    }
    /**
     * Checks if a product ID already exists with a different name.
     * @param productId The product ID to check
     * @param productName The product name to check
     * @param graph The graph to check in
     * @return true if a conflict exists, false otherwise
     */
    private static boolean hasConflictingProductId(int productId, String productName, Graph graph) {
        for (Product existingProduct : graph.getProducts()) {
            if (existingProduct.getId() == productId
                    && !existingProduct.getLowerCaseName().equals(productName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    /**
     * Handles the remove command.
     * @param commandStr The command string
     * @param graph The graph to remove from
     * @param commandParser The command parser to use
     */
    public static void handleRemoveCommand(String commandStr, Graph graph, CommandParser commandParser) {
        String[] parts = commandParser.parseAddOrRemove(commandStr);
        String subject = parts[SUBJECT_INDEX];
        String predicate = parts[PREDICATE_INDEX];
        String object = parts[OBJECT_INDEX];
        // Extract node names
        String sourceName = extractNodeName(subject);
        String targetName = extractNodeName(object);
        // Look up the nodes in the graph
        Node sourceNode = graph.getNodeByName(sourceName);
        Node targetNode = graph.getNodeByName(targetName);
        if (sourceNode == null || targetNode == null) {
            System.out.println(Error.PREFIX + Error.NODE_NOT_FOUND);
            return;
        }
        // Find the relationship type
        RelationshipTypes relationship = RelationshipTypes.fromString(predicate);
        if (relationship == null) {
            System.out.println(Error.PREFIX + Error.INVALID_RELATIONSHIP_TYPE + predicate);
            return;
        }
        // Find and remove the edge
        Edge edgeToRemove = null;
        for (Edge edge : graph.getOutgoingEdges(sourceNode)) {
            if (edge.getTarget().equals(targetNode) && edge.getRelationship() == relationship) {
                edgeToRemove = edge;
                break;
            }
        }
        if (edgeToRemove == null) {
            System.out.println(Error.PREFIX + Error.RELATIONSHIP_NOT_FOUND);
            return;
        }
        graph.removeEdge(edgeToRemove);
    }
    /**
     * Handles the nodes command.
     * @param graph The graph to get nodes from
     */
    public static void handleNodesCommand(Graph graph) {
        Set<Node> nodes = graph.getNodes();
        if (nodes.isEmpty()) {
            System.out.println(CLI.EMPTY);
            return;
        }
        // Sort nodes by name (case-insensitive)
        List<Node> sortedNodes = new ArrayList<>(nodes);
        sortNodesByName(sortedNodes);
        StringBuilder sb = new StringBuilder();
        for (Node node : sortedNodes) {
            if (sb.length() > 0) {
                sb.append(CLI.SPACE);
            }
            sb.append(node.toString());
        }
        System.out.println(sb.toString());
    }
    /**
     * Handles the edges command.
     * @param graph The graph to get edges from
     */
    public static void handleEdgesCommand(Graph graph) {
        Set<Edge> edges = graph.getEdges();
        if (edges.isEmpty()) {
            return;
        }
        // Sort edges by source name, then target name, then relationship
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortEdges(sortedEdges);
        for (Edge edge : sortedEdges) {
            System.out.println(edge.toString());
        }
    }
    /**
     * Handles the recommend command.
     * @param commandStr The command string
     * @param graph The graph to get recommendations from
     * @param recommendParser The recommend command parser to use
     */
    public static void handleRecommendCommand(String commandStr, Graph graph, RecommendCommandParser recommendParser) {
        // Parse the recommend command
        RecommendCommandParser.RecommendTerm term = recommendParser.parse(commandStr);
        // Create a strategy from the term
        RecommendationStrategy strategy = RecommendationStrategyFactory.createStrategy(term);
        // Find the product ID to use for the strategy
        int productId = 0;
        if (term instanceof RecommendCommandParser.FinalTerm) {
            productId = ((RecommendCommandParser.FinalTerm) term).getProductId();
        } else {
            // For composite terms, find the first final term to get a product ID
            productId = findFirstProductId(term);
        }
        // Check if the product exists before proceeding
        Product referenceProduct = findProductById(productId, graph);
        if (referenceProduct == null) {
            System.out.println(Error.PREFIX + "Product with ID " + productId + " not found");
            return;
        }
        // Get recommendations
        Set<Product> recommendations = strategy.getRecommendations(productId, graph);
        // Sort recommendations by name
        List<Product> sortedRecommendations = new ArrayList<>(recommendations);
        sortProductsByName(sortedRecommendations);
        // Print recommendations
        StringBuilder sb = new StringBuilder();
        for (Product product : sortedRecommendations) {
            if (sb.length() > 0) {
                sb.append(CLI.SPACE);
            }
            sb.append(product.toString());
        }
        System.out.println(sb.toString());
    }
    /**
     * Handles the export command.
     * @param graph The graph to export
     */
    public static void handleExportCommand(Graph graph) {
        DotExport exporter = new DotExport(graph);
        System.out.println(exporter.export());
    }
    /**
     * Extracts the node name from a node string.
     * @param nodeStr The node string
     * @return The extracted node name
     */
    private static String extractNodeName(String nodeStr) {
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
    private static void sortNodesByName(List<Node> nodes) {
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
     * Sorts edges by source name, target name, and relationship.
     * @param edges The list of edges to sort
     */
    private static void sortEdges(List<Edge> edges) {
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
     *
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
    private static Product findProductById(int productId, Graph graph) {
        for (Product product : graph.getProducts()) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
    /**
     * Sorts a list of products by their lowercase name.
     *
     * @param products The list of products to sort
     */
    private static void sortProductsByName(List<Product> products) {
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
     * Recursively finds the first product ID in a term.
     *
     * @param term The term
     * @return The first product ID
     */
    private static int findFirstProductId(RecommendCommandParser.RecommendTerm term) {
        if (term instanceof RecommendCommandParser.FinalTerm) {
            return ((RecommendCommandParser.FinalTerm) term).getProductId();
        } else if (term instanceof RecommendCommandParser.IntersectionTerm) {
            RecommendCommandParser.IntersectionTerm intersectionTerm =
                    (RecommendCommandParser.IntersectionTerm) term;
            int leftId = findFirstProductId(intersectionTerm.getLeft());
            if (leftId != 0) {
                return leftId;
            }
            return findFirstProductId(intersectionTerm.getRight());
        } else if (term instanceof RecommendCommandParser.UnionTerm) {
            RecommendCommandParser.UnionTerm unionTerm =
                    (RecommendCommandParser.UnionTerm) term;
            int leftId = findFirstProductId(unionTerm.getLeft());
            if (leftId != 0) {
                return leftId;
            }
            return findFirstProductId(unionTerm.getRight());
        }
        return 0;
    }
}