package io;

import model.*;
import util.Constants.*;
import java.util.*;

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
        sortedEdges.sort((e1, e2) -> {
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
        });

        for (Edge edge : sortedEdges) {
            sb.append(Dot.INDENT)
                    .append(edge.getSource().getLowerCaseName())
                    .append(Dot.ARROW)
                    .append(edge.getTarget().getLowerCaseName())
                    .append(Dot.LABEL_START)
                    .append(getRelationshipLabel(edge.getRelationship()))
                    .append(Dot.LABEL_END)
                    .append(CLI.NEW_LINE);
        }

        // Then, output all category nodes as boxes
        List<Category> sortedCategories = new ArrayList<>(graph.getCategories());
        sortedCategories.sort(Comparator.comparing(Category::getLowerCaseName));

        for (Category category : sortedCategories) {
            sb.append(Dot.INDENT)
                    .append(category.getLowerCaseName())
                    .append(Dot.SHAPE_BOX)
                    .append(CLI.NEW_LINE);
        }

        sb.append(Dot.DIGRAPH_END);
        return sb.toString();
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