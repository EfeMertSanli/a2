package io;

import model.Category;
import model.Edge;
import model.Graph;
import model.RelationshipTypes;
import util.Constants.CLI;
import util.Constants.Dot;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports a graph in DOT notation.
 *
 * @author uuifx
 */
public class DotExport {
    // Relationship order indices
    private static final int CONTAINS_ORDER = 0;
    private static final int CONTAINED_IN_ORDER = 1;
    private static final int PART_OF_ORDER = 2;
    private static final int HAS_PART_ORDER = 3;
    private static final int SUCCESSOR_OF_ORDER = 4;
    private static final int PREDECESSOR_OF_ORDER = 5;
    private static final int DEFAULT_ORDER = 6;

    private final Graph graph;

    /**
     * Creates a new DOT exporter for the given graph.
     *
     * @param graph The graph to export
     * @throws IllegalArgumentException if graph is null
     */
    public DotExport(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        this.graph = graph;
    }

    /**
     * Exports the graph in DOT notation.
     *
     * @return The DOT notation of the graph
     */
    public String export() {
        StringBuilder sb = new StringBuilder();
        sb.append(Dot.DIGRAPH_START).append(CLI.NEW_LINE);

        // First, output all edges sorted by source, target, and relationship
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        sortEdges(sortedEdges);

        for (Edge edge : sortedEdges) {
            // No indentation for edge lines
            sb.append(edge.getSource().getLowerCaseName())
                    .append(Dot.ARROW)
                    .append(edge.getTarget().getLowerCaseName())
                    .append(Dot.LABEL_START)
                    .append(getRelationshipLabel(edge.getRelationship()))
                    .append(Dot.LABEL_END)
                    .append(CLI.NEW_LINE);
        }

        // Then, output all category nodes as boxes
        List<Category> sortedCategories = new ArrayList<>(graph.getCategories());
        sortCategories(sortedCategories);

        for (Category category : sortedCategories) {
            // No indentation for category lines
            sb.append(category.getLowerCaseName())
                    .append(Dot.SHAPE_BOX)
                    .append(CLI.NEW_LINE);
        }

        sb.append(Dot.DIGRAPH_END);
        return sb.toString();
    }

    /**
     * Sorts edges by source, target, and relationship.
     *
     * @param edges The list of edges to sort
     */
    private void sortEdges(List<Edge> edges) {
        // Manual bubble sort implementation to avoid using Comparator
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
     * Sorts categories by name.
     *
     * @param categories The list of categories to sort
     */
    private void sortCategories(List<Category> categories) {
        // Manual bubble sort implementation to avoid using Comparator
        for (int i = 0; i < categories.size() - 1; i++) {
            for (int j = 0; j < categories.size() - i - 1; j++) {
                if (categories.get(j).getLowerCaseName().compareTo(
                        categories.get(j + 1).getLowerCaseName()) > 0) {
                    // Swap categories
                    Category temp = categories.get(j);
                    categories.set(j, categories.get(j + 1));
                    categories.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Compares two edges for sorting by source, target, and relationship.
     *
     * @param e1 The first edge
     * @param e2 The second edge
     * @return Comparison result
     */
    private int compareEdges(Edge e1, Edge e2) {
        int sourceComp = e1.getSource().getLowerCaseName().compareTo(e2.getSource().getLowerCaseName());
        if (sourceComp != 0) {
            return sourceComp;
        }

        int targetComp = e1.getTarget().getLowerCaseName().compareTo(e2.getTarget().getLowerCaseName());
        if (targetComp != 0) {
            return targetComp;
        }

        // Order by relationship type according to the order in the specification
        return getRelationshipOrder(e1.getRelationship()) - getRelationshipOrder(e2.getRelationship());
    }

    /**
     * Gets the label for a relationship type in DOT notation.
     * Removes hyphens from the relationship name.
     *
     * @param relationship The relationship type
     * @return The label for the relationship
     */
    private String getRelationshipLabel(RelationshipTypes relationship) {
        return relationship.getName().replace(Dot.HYPHEN, "");
    }

    /**
     * Gets the order index for a relationship type based on the specification.
     *
     * @param relationship The relationship type
     * @return The order index
     */
    private int getRelationshipOrder(RelationshipTypes relationship) {
        switch (relationship) {
            case CONTAINS: return CONTAINS_ORDER;
            case CONTAINED_IN: return CONTAINED_IN_ORDER;
            case PART_OF: return PART_OF_ORDER;
            case HAS_PART: return HAS_PART_ORDER;
            case SUCCESSOR_OF: return SUCCESSOR_OF_ORDER;
            case PREDECESSOR_OF: return PREDECESSOR_OF_ORDER;
            default: return DEFAULT_ORDER;
        }
    }
}