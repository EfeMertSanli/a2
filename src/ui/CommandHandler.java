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
import util.CommandValidationUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import parser.DatabaseParserConstants;
import static ui.CommandHandlerConstants.SUBJECT_INDEX;
import static ui.CommandHandlerConstants.PREDICATE_INDEX;
import static ui.CommandHandlerConstants.OBJECT_INDEX;



/**
 * Handles execution of commands for the product recommendation system.
 * @author uuifx
 */
public final class CommandHandler {
    // Array indices for add/remove command parts
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
     * @return true if the database was loaded successfully, false otherwise
     * @throws IOException if an IO error occurs
     */
    public static boolean handleLoadDatabase(String commandStr, Graph graph, CommandParser commandParser) throws IOException {
        String filePath = commandParser.parseLoadDatabase(commandStr);
        if (filePath == null) {
            return false;  // Invalid command format, error already printed
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            graph.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(Error.PREFIX + Error.FAILED_READ_DATABASE + e.getMessage());
            return false;
        }
        try {
            DatabaseParser parser = new DatabaseParser(graph);
            parser.parseFile(filePath);
            return true;
        } catch (IOException e) {
            System.out.println(Error.PREFIX + Error.FAILED_READ_DATABASE + e.getMessage());
            return false;
        }
    }
    /**
     * Checks if a database is loaded by checking if the graph has any nodes.
     * @param graph The graph to check
     * @return true if a database is loaded, false otherwise
     */
    private static boolean isDatabaseLoaded(Graph graph) {
        return !graph.getNodes().isEmpty();
    }


    /**
     * Handles the add command.
     * @param commandStr The command string
     * @param graph The graph to add to
     * @param commandParser The command parser to use
     */
    public static void handleAddCommand(String commandStr, Graph graph, CommandParser commandParser) {
        if (!isDatabaseLoaded(graph)) {
            System.out.println(Error.PREFIX + Constants.Error.CANNOT_ADD_RELATIONSHIP_NO_DATABASE);
            return;
        }
        String[] parts = commandParser.parseAddOrRemove(commandStr);

        String subject = parts[SUBJECT_INDEX];
        String predicate = parts[PREDICATE_INDEX];
        String object = parts[OBJECT_INDEX];

        // Extract node information
        boolean isSubjectProduct = subject.matches(Constants.Regex.PRODUCT_REGEX);
        boolean isObjectProduct = object.matches(Constants.Regex.PRODUCT_REGEX);

        // Validate product IDs and names
        if (isSubjectProduct) {
            Pattern pattern = Pattern.compile(Constants.Regex.PRODUCT_REGEX);
            Matcher matcher = pattern.matcher(subject);
            matcher.matches();
            String name = matcher.group(1);
            int id = Integer.parseInt(matcher.group(2));
            if (CommandValidationUtils.hasConflictingProductId(id, name, graph)) {
                System.out.println(Error.PREFIX + Constants.Error.PRODUCT_ID + id + Constants.Error.EXISTS_WITH_DIFFERENT_NAME);
                return;
            }
            if (CommandValidationUtils.hasConflictingProductName(name, id, graph)) {
                System.out.println(Error.PREFIX + Constants.Error.PRODUCT_NAME + name + Constants.Error.EXISTS_WITH_DIFFERENT_ID);
                return;
            }
        }

        if (isObjectProduct) {
            Pattern pattern = Pattern.compile(Constants.Regex.PRODUCT_REGEX);
            Matcher matcher = pattern.matcher(object);
            matcher.matches();
            String name = matcher.group(1);
            int id = Integer.parseInt(matcher.group(2));
            if (CommandValidationUtils.hasConflictingProductId(id, name, graph)) {
                System.out.println(Error.PREFIX + Constants.Error.PRODUCT_ID + id + Constants.Error.EXISTS_WITH_DIFFERENT_NAME);
                return;
            }
            if (CommandValidationUtils.hasConflictingProductName(name, id, graph)) {
                System.out.println(Error.PREFIX + Constants.Error.PRODUCT_NAME + name + Constants.Error.EXISTS_WITH_DIFFERENT_ID);
                return;
            }
        }

        RelationshipTypes relationship = RelationshipTypes.fromString(predicate);

        // Validate relationship constraints using the utility method
        if (!CommandValidationUtils.isValidRelationship(relationship, isSubjectProduct, isObjectProduct)) {
            System.out.println(Error.PREFIX + Constants.Error.INVALID_RELATIONSHIP_BETWEEN + subject + Constants.Error.AND + object);
            return;
        }

        // If we reach here, it's safe to add
        DatabaseParser parser = new DatabaseParser(graph);
        parser.parseLine(subject + CLI.SPACE + predicate + CLI.SPACE + object);
    }

    /**
     * Handles the remove command.
     * @param commandStr The command string
     * @param graph The graph to remove from
     * @param commandParser The command parser to use
     */
    public static void handleRemoveCommand(String commandStr, Graph graph, CommandParser commandParser) {
        if (!isDatabaseLoaded(graph)) {
            System.out.println(Error.PREFIX + Constants.Error.CANNOT_ADD_RELATIONSHIP_NO_DATABASE);
            return;
        }
        String[] parts = commandParser.parseAddOrRemove(commandStr);
        String subject = parts[SUBJECT_INDEX];
        String predicate = parts[PREDICATE_INDEX];
        String object = parts[OBJECT_INDEX];

        String sourceName = CommandValidationUtils.extractNodeName(subject);
        String targetName = CommandValidationUtils.extractNodeName(object);

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
        CommandValidationUtils.sortNodesByName(sortedNodes);
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
        CommandValidationUtils.sortEdges(sortedEdges);

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
        int productId = DatabaseParserConstants.LINE_START - 1; // or another appropriate constant
        if (term instanceof RecommendCommandParser.FinalTerm) {
            productId = ((RecommendCommandParser.FinalTerm) term).getProductId();
        } else {
            // For composite terms, find the first final term to get a product ID
            productId = CommandValidationUtils.findFirstProductId(term);
        }

        // Check if the product exists before proceeding
        Product referenceProduct = CommandValidationUtils.findProductById(productId, graph);
        if (referenceProduct == null) {
            System.out.println(Error.PREFIX + Constants.Error.PRODUCT_ID + productId + Constants.Error.NODE_NOT_FOUND);

            return;
        }

        // Get recommendations
        Set<Product> recommendations = strategy.getRecommendations(productId, graph);

        // Sort recommendations by name
        List<Product> sortedRecommendations = new ArrayList<>(recommendations);
        CommandValidationUtils.sortProductsByName(sortedRecommendations);

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
}