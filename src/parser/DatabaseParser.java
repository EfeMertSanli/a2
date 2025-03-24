package parser;

import model.*;
import util.Constants.Error;
import util.Constants.Regex;
import java.io.*;
import java.util.regex.*;

/**
 * Parser for database files according to the specified grammar.
 *
 * @author u-student
 */
public class DatabaseParser {
    private static final int SUBJECT_GROUP = 1;
    private static final int PREDICATE_GROUP = 2;
    private static final int OBJECT_GROUP = 3;
    private static final int NAME_GROUP = 1;
    private static final int ID_GROUP = 2;
    private static final int LINE_START = 1;

    private final Graph graph;

    /**
     * Creates a new database parser that operates on the given graph.
     *
     * @param graph The graph to build
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

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                try {
                    parseLine(line);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(Error.ERROR_IN_LINE + lineNumber + ": " + e.getMessage());
                }
            }
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

        Node sourceNode = parseNode(subject);
        Node targetNode = parseNode(object);

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