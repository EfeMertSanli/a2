import model.*;
import parser.*;
import strategy.*;
import io.DotExport;
import util.Constants.CLI;
import util.Constants.Error;
import java.io.*;
import java.util.*;

/**
 * Main class for the product recommendation system.
 *
 * @author uuifx
 */
public class Main {
    private static final Graph graph = new Graph();
    private static final Scanner scanner = new Scanner(System.in);
    private static final CommandParser commandParser = new CommandParser();
    private static final RecommendCommandParser recommendParser = new RecommendCommandParser();

    // Array indices for add/remove command parts
    private static final int SUBJECT_INDEX = 0;
    private static final int PREDICATE_INDEX = 1;
    private static final int OBJECT_INDEX = 2;

    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.print(CLI.PROMPT);
            String userInput = scanner.nextLine().trim();

            try {
                CommandType commandType = commandParser.parseCommandType(userInput);

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
            } catch (Exception e) {
                System.out.println(Error.PREFIX + e.getMessage());
            }
        }
    }

    /**
     * Handles the load database command.
     *
     * @param commandStr The command string
     */
    private static void handleLoadDatabase(String commandStr) {
        try {
            String filePath = commandParser.parseLoadDatabase(commandStr);

            // Create a new graph for the database
            graph.clear();

            // Parse the database file line by line to print it verbatim
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Now actually parse the database and build the graph
            DatabaseParser parser = new DatabaseParser(graph);
            parser.parseFile(filePath);

        } catch (IOException e) {
            System.out.println(Error.PREFIX + Error.FAILED_READ_DATABASE + e.getMessage());
        }
    }

    /**
     * Handles the add command.
     *
     * @param commandStr The command string
     */
    private static void handleAddCommand(String commandStr) {
        try {
            String[] parts = commandParser.parseAddOrRemove(commandStr);
            String subject = parts[SUBJECT_INDEX];
            String predicate = parts[PREDICATE_INDEX];
            String object = parts[OBJECT_INDEX];

            // Create a temporary parser to parse the line
            DatabaseParser parser = new DatabaseParser(graph);
            parser.parseLine(subject + CLI.SPACE + predicate + CLI.SPACE + object);

        } catch (Exception e) {
            System.out.println(Error.PREFIX + Error.FAILED_ADD_RELATIONSHIP + e.getMessage());
        }
    }

    /**
     * Handles the remove command.
     *
     * @param commandStr The command string
     */
    private static void handleRemoveCommand(String commandStr) {
        try {
            String[] parts = commandParser.parseAddOrRemove(commandStr);
            String subject = parts[SUBJECT_INDEX];
            String predicate = parts[PREDICATE_INDEX];
            String object = parts[OBJECT_INDEX];

            // Parse the subject and object to get the nodes
            Node sourceNode = graph.getNodeByName(subject);
            Node targetNode = graph.getNodeByName(object);

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

        } catch (Exception e) {
            System.out.println(Error.PREFIX + Error.FAILED_REMOVE_RELATIONSHIP + e.getMessage());
        }
    }

    /**
     * Handles the nodes command.
     */
    private static void handleNodesCommand() {
        Set<Node> nodes = graph.getNodes();
        if (nodes.isEmpty()) {
            System.out.println(CLI.EMPTY);
            return;
        }

        // Sort nodes by name (case-insensitive)
        List<Node> sortedNodes = new ArrayList<>(nodes);
        sortedNodes.sort(Comparator.comparing(Node::getLowerCaseName));

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
     */
    private static void handleEdgesCommand() {
        Set<Edge> edges = graph.getEdges();
        if (edges.isEmpty()) {
            return;
        }

        // Sort edges by source name, then target name, then relationship
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort((e1, e2) -> {
            int sourceComp = e1.getSource().getLowerCaseName().compareTo(e2.getSource().getLowerCaseName());
            if (sourceComp != 0) {
                return sourceComp;
            }

            int targetComp = e1.getTarget().getLowerCaseName().compareTo(e2.getTarget().getLowerCaseName());
            if (targetComp != 0) {
                return targetComp;
            }

            return e1.getRelationship().toString().compareTo(e2.getRelationship().toString());
        });

        for (Edge edge : sortedEdges) {
            System.out.println(edge.toString());
        }
    }

    /**
     * Handles the recommend command.
     *
     * @param commandStr The command string
     */
    private static void handleRecommendCommand(String commandStr) {
        try {
            // Parse the recommend command
            RecommendCommandParser.RecommendTerm term = recommendParser.parse(commandStr);

            // Create a strategy from the term
            RecommendationStrategy strategy = RecommendationStrategyFactory.createStrategy(term);

            // If we have a final term, we need to extract the product ID
            int productId = 0;
            if (term instanceof RecommendCommandParser.FinalTerm) {
                productId = ((RecommendCommandParser.FinalTerm) term).getProductId();
            } else {
                // For composite terms, find the first final term to get a product ID
                productId = findFirstProductId(term);
            }

            // Get recommendations
            Set<Product> recommendations = strategy.getRecommendations(productId, graph);

            // Sort recommendations by name
            List<Product> sortedRecommendations = new ArrayList<>(recommendations);
            sortedRecommendations.sort(Comparator.comparing(Product::getLowerCaseName));

            // Print recommendations
            StringBuilder sb = new StringBuilder();
            for (Product product : sortedRecommendations) {
                if (sb.length() > 0) {
                    sb.append(CLI.SPACE);
                }
                sb.append(product.toString());
            }

            System.out.println(sb.toString());

        } catch (Exception e) {
            System.out.println(Error.PREFIX + Error.FAILED_PROCESS_RECOMMEND + e.getMessage());
        }
    }

    /**
     * Recursively finds the first product ID in a term.
     *
     * @param term The term
     * @return The first product ID
     * @throws IllegalArgumentException If no product ID is found
     */
    private static int findFirstProductId(RecommendCommandParser.RecommendTerm term) {
        if (term instanceof RecommendCommandParser.FinalTerm) {
            return ((RecommendCommandParser.FinalTerm) term).getProductId();
        } else if (term instanceof RecommendCommandParser.IntersectionTerm) {
            RecommendCommandParser.IntersectionTerm intersectionTerm = (RecommendCommandParser.IntersectionTerm) term;
            try {
                return findFirstProductId(intersectionTerm.getLeft());
            } catch (IllegalArgumentException e) {
                return findFirstProductId(intersectionTerm.getRight());
            }
        } else if (term instanceof RecommendCommandParser.UnionTerm) {
            RecommendCommandParser.UnionTerm unionTerm = (RecommendCommandParser.UnionTerm) term;
            try {
                return findFirstProductId(unionTerm.getLeft());
            } catch (IllegalArgumentException e) {
                return findFirstProductId(unionTerm.getRight());
            }
        }

        throw new IllegalArgumentException(Error.NO_PRODUCT_ID);
    }

    /**
     * Handles the export command.
     */
    private static void handleExportCommand() {
        DotExport exporter = new DotExport(graph);
        System.out.println(exporter.export());
    }
}