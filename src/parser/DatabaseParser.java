package parser;

import model.Category;
import model.Edge;
import model.Graph;
import model.Node;
import model.Product;
import model.RelationshipTypes;
import util.Constants.Error;
import util.Constants.Regex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for database files according to the specified grammar.
 *
 * @author uuifx
 */
public class DatabaseParser {
    private static final int SUBJECT_GROUP = 1;
    private static final int PREDICATE_GROUP = 2;
    private static final int OBJECT_GROUP = 3;
    private static final int NAME_GROUP = 1;
    private static final int ID_GROUP = 2;
    private static final int LINE_START = 1;

    private final Graph graph;
    // Map to track product IDs and their corresponding names during parsing
    private final Map<Integer, String> productIdMap = new HashMap<>();
    /**
     * Creates a new database parser that operates on the given graph.
     *
     * @param graph The graph to build
     * @throws IllegalArgumentException if graph is null
     */
    public DatabaseParser(Graph graph) {

        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        this.graph = graph;
    }
    /**
     * Parses a database file and builds the graph.
     *
     * @param filePath The path to the database file
     * @throws IOException If an I/O error occurs
     * @throws IllegalArgumentException If the file content is invalid
     */
    public void parseFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            // First pass: validate the entire file for duplicate product IDs
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                // Validate the line syntax
                Pattern pattern = Pattern.compile(Regex.LINE_REGEX);
                Matcher matcher = pattern.matcher(line);

                if (!matcher.matches()) {
                    throw new IllegalArgumentException(Error.INVALID_LINE_FORMAT + line);
                }

                String subject = matcher.group(SUBJECT_GROUP).trim();
                String object = matcher.group(OBJECT_GROUP).trim();

                // Check subject for duplicate ID
                validateForDuplicateId(subject, lineNumber);

                // Check object for duplicate ID
                validateForDuplicateId(object, lineNumber);
            }

            // If validation passes, reset and actually parse the file
            productIdMap.clear();
            reader.close();

            // Now parse the file to build the graph
            try (BufferedReader parseReader = new BufferedReader(new FileReader(filePath))) {
                while ((line = parseReader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue; // Skip empty lines
                    }
                    parseLine(line);
                }
            }
        }
    }

    /**
     * Validates a node string for duplicate product IDs.
     *
     * @param nodeStr The node string to validate
     * @param lineNumber The line number for error reporting
     * @throws IllegalArgumentException If a duplicate product ID is found
     */
    private void validateForDuplicateId(String nodeStr, int lineNumber) {
        // Try to parse as a product
        Pattern productPattern = Pattern.compile(Regex.PRODUCT_REGEX);
        Matcher productMatcher = productPattern.matcher(nodeStr);

        if (productMatcher.matches()) {
            String name = productMatcher.group(NAME_GROUP);
            int id = Integer.parseInt(productMatcher.group(ID_GROUP));

            // Check if this ID already exists with a different name
            if (productIdMap.containsKey(id) && !productIdMap.get(id).equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("Duplicate product ID "
                        + id + " found with different names: " + productIdMap.get(id) + " and " + name
                        + " at line " + lineNumber);
            }

            // Add to the map
            productIdMap.put(id, name);
        }
    }

    /**
     * Parses a single line from the database file.
     *
     * @param line The line to parse
     * @throws IllegalArgumentException If the line is invalid
     */
    public void parseLine(String line) {
        Pattern pattern = Pattern.compile(Regex.LINE_REGEX);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(Error.INVALID_LINE_FORMAT + line);
        }

        String subject = matcher.group(SUBJECT_GROUP).trim();
        String predicate = matcher.group(PREDICATE_GROUP).trim();
        String object = matcher.group(OBJECT_GROUP).trim();

        RelationshipTypes relationship = RelationshipTypes.fromString(predicate);
        if (relationship == null) {
            throw new IllegalArgumentException(Error.INVALID_RELATIONSHIP_TYPE + predicate);
        }

        // First, parse the nodes to check for conflicts
        Node sourceNode = parseNode(subject);
        Node targetNode = parseNode(object);

        // Check for name conflicts with existing nodes
        if (sourceNode.isProduct()) {
            Product sourceProduct = (Product) sourceNode;
            Node existingNode = graph.getNodeByName(sourceNode.getName());
            if (existingNode != null && existingNode.isProduct()
                    &&
                    ((Product) existingNode).getId() != sourceProduct.getId()) {
                System.out.println(Error.PREFIX + "Product name " + sourceNode.getName()
                        + " already exists with a different ID");
                return;
            }
        }

        if (targetNode.isProduct()) {
            Product targetProduct = (Product) targetNode;
            Node existingNode = graph.getNodeByName(targetNode.getName());
            if (existingNode != null && existingNode.isProduct()
                    && ((Product) existingNode).getId() != targetProduct.getId()) {
                System.out.println(Error.PREFIX + "Product name " + targetNode.getName()
                        +
                        " already exists with a different ID");
                return;
            }
        }

        // Add nodes to the graph
        graph.addNode(sourceNode);
        graph.addNode(targetNode);

        // Create and add the edge
        Edge edge = new Edge(sourceNode, targetNode, relationship);
        if (!edge.isValidRelationship()) {
            throw new IllegalArgumentException("Invalid relationship between " + sourceNode + " and " + targetNode);
        }

        if (!graph.addEdge(edge)) {
            throw new IllegalArgumentException("Failed to add edge: " + edge);
        }
    }


    /**
     * Parses a node description (product or category).
     *
     * @param nodeStr The node description string
     * @return The parsed node
     * @throws IllegalArgumentException If the node description is invalid
     */
    private Node parseNode(String nodeStr) {
        // Try to parse as a product
        Pattern productPattern = Pattern.compile(Regex.PRODUCT_REGEX);
        Matcher productMatcher = productPattern.matcher(nodeStr);

        if (productMatcher.matches()) {
            String name = productMatcher.group(NAME_GROUP);
            int id = Integer.parseInt(productMatcher.group(ID_GROUP));
            return new Product(name, id);
        }

        // Try to parse as a category
        Pattern categoryPattern = Pattern.compile(Regex.CATEGORY_REGEX);
        Matcher categoryMatcher = categoryPattern.matcher(nodeStr);

        if (categoryMatcher.matches()) {
            String name = categoryMatcher.group(NAME_GROUP);
            return new Category(name);
        }

        throw new IllegalArgumentException(Error.INVALID_NODE_FORMAT + nodeStr);
    }
}