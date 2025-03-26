package ui;

import model.Node;
import model.Graph;
import model.Edge;
import model.Product;
import model.RelationshipTypes;
import parser.CommandParser;
import parser.CommandType;
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
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class for the product recommendation system.
 *
 * @author uuifx
 */
public final class Main {
    private static final Graph GRAPH = new Graph();
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final CommandParser COMMAND_PARSER = new CommandParser();
    private static final RecommendCommandParser RECOMMEND_PARSER = new RecommendCommandParser();

    // Array indices for add/remove command parts
    private static final int SUBJECT_INDEX = 0;
    private static final int PREDICATE_INDEX = 1;
    private static final int OBJECT_INDEX = 2;

    /**
     * Private constructor to prevent instantiation.
     */
    private Main() {
        // Utility class should not be instantiated
    }
    /**
     * Main entry point for the application.
     * @param args Command-line arguments (not used)
     * @throws IOException if an IO error occurs during file operations
     */
    public static void main(String[] args) throws IOException {
        boolean running = true;
        while (running) {
            String userInput = SCANNER.nextLine().trim();
            CommandType commandType = COMMAND_PARSER.parseCommandType(userInput);
            switch (commandType) {
                case LOAD_DATABASE:
                    handleLoadDatabase(userInput);
                    break;
                case QUIT:
                    running = false;
                    break;
                case ADD:
                    handleAddCommand(userInput);
                    break;
                case REMOVE:
                    handleRemoveCommand(userInput);
                    break;
                case NODES:
                    handleNodesCommand();
                    break;
                case EDGES:
                    handleEdgesCommand();
                    break;
                case RECOMMEND:
                    handleRecommendCommand(userInput);
                    break;
                case EXPORT:
                    handleExportCommand();
                    break;
                case UNKNOWN:
                default:
                    System.out.println(Error.PREFIX + Error.UNKNOWN_COMMAND + userInput);
                    break;
            }
        }
    }
    /**
     * Handles the load database command.
     * @param commandStr The command string
     */
    private static void handleLoadDatabase(String commandStr) throws IOException {
        String filePath = COMMAND_PARSER.parseLoadDatabase(commandStr);
        // Create a new Graph for the database
        GRAPH.clear();
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
        DatabaseParser parser = new DatabaseParser(GRAPH);
        parser.parseFile(filePath);
    }

    /**
     * Handles the add command.
     *
     * @param commandStr The command string
     */
    private static void handleAddCommand(String commandStr) {
        String[] parts = COMMAND_PARSER.parseAddOrRemove(commandStr);
        String subject = parts[SUBJECT_INDEX];
        String predicate = parts[PREDICATE_INDEX];
        String object = parts[OBJECT_INDEX];

        // Create a temporary parser to parse the line
        DatabaseParser parser = new DatabaseParser(GRAPH);
        parser.parseLine(subject + CLI.SPACE + predicate + CLI.SPACE + object);
    }

    /**
     * Handles the remove command.
     *
     * @param commandStr The command string
     */
    private static void handleRemoveCommand(String commandStr) {
        String[] parts = COMMAND_PARSER.parseAddOrRemove(commandStr);
        String subject = parts[SUBJECT_INDEX];
        String predicate = parts[PREDICATE_INDEX];
        String object = parts[OBJECT_INDEX];

        // Extract node names
        String sourceName = extractNodeName(subject);
        String targetName = extractNodeName(object);

        // Look up the nodes in the graph
        Node sourceNode = GRAPH.getNodeByName(sourceName);
        Node targetNode = GRAPH.getNodeByName(targetName);

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
        for (Edge edge : GRAPH.getOutgoingEdges(sourceNode)) {
            if (edge.getTarget().equals(targetNode) && edge.getRelationship() == relationship) {
                edgeToRemove = edge;
                break;
            }
        }

        if (edgeToRemove == null) {
            System.out.println(Error.PREFIX + Error.RELATIONSHIP_NOT_FOUND);
            return;
        }

        GRAPH.removeEdge(edgeToRemove);
    }
    /**
     * Extracts the node name from a node string.
     *
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
     * Handles the nodes command.
     */
    private static void handleNodesCommand() {
        Set<Node> nodes = GRAPH.getNodes();
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
     * Sorts a list of nodes by their lowercase name.
     *
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
     * Handles the edges command.
     */
    private static void handleEdgesCommand() {
        Set<Edge> edges = GRAPH.getEdges();
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
     * Sorts edges by source name, target name, and relationship.
     *
     * @param edges The list of edges to sort
     */
    private static void sortEdges(List<Edge> edges) {
        for (int i = 0; i < edges.size() - 1; i++) {
            for (int j = 0; j < edges.size() - i - 1; j++) {
                if (compareEdges(edges.get(j), edges.get(j + 1)) > 0) {
                    // Swap edges
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

        return e1.getRelationship().toString().compareTo(e2.getRelationship().toString());
    }

    /**
     * Handles the recommend command.
     *
     * @param commandStr The command string
     */
    private static void handleRecommendCommand(String commandStr) {
        // Parse the recommend command
        RecommendCommandParser.RecommendTerm term = RECOMMEND_PARSER.parse(commandStr);

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

        // Get recommendations
        Set<Product> recommendations = strategy.getRecommendations(productId, GRAPH);

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
    /**
     * Handles the export command.
     */
    private static void handleExportCommand() {
        DotExport exporter = new DotExport(GRAPH);
        System.out.println(exporter.export());
    }
}