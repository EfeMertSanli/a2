package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Represents the product graph with nodes and edges.
 *
 * @author uuifx
 */
public class Graph {
    private final NodeNameRegistry nodeRegistry = new NodeNameRegistry();
    private final Set<Node> nodes = new HashSet<>();
    private final Set<Edge> edges = new HashSet<>();
    private final Map<Node, Set<Edge>> outgoingEdges = new HashMap<>();
    private final Map<Node, Set<Edge>> incomingEdges = new HashMap<>();

    /**
     * Adds a node to the graph.
     *
     * @param node The node to add
     * @return true if the node was added, false if a node with the same name already exists
     */
    public boolean addNode(Node node) {
        if (node == null) {
            return false;
        }

        if (!nodeRegistry.registerNode(node)) {
            return false;
        }

        nodes.add(node);
        outgoingEdges.put(node, new HashSet<>());
        incomingEdges.put(node, new HashSet<>());
        return true;
    }

    /**
     * Gets a node by its name (case-insensitive).
     *
     * @param name The name of the node
     * @return The node with the given name, or null if not found
     */
    public Node getNodeByName(String name) {
        return nodeRegistry.getNodeByName(name);
    }

    /**
     * Adds an edge to the graph.
     * Also adds the inverse edge automatically.
     *
     * @param edge The edge to add
     * @return true if the edge was added, false if the edge already exists or is invalid
     */
    public boolean addEdge(Edge edge) {
        if (edge == null || !edge.isValidRelationship()) {
            return false;
        }

        Node source = edge.getSource();
        Node target = edge.getTarget();

        // Make sure both nodes are in the graph
        if (!nodes.contains(source) || !nodes.contains(target)) {
            return false;
        }

        // Check if the edge already exists
        if (edges.contains(edge)) {
            return false;
        }

        // Add the edge
        edges.add(edge);
        outgoingEdges.get(source).add(edge);
        incomingEdges.get(target).add(edge);

        // Add the inverse edge if it doesn't already exist
        Edge inverseEdge = edge.createInverse();
        if (!edges.contains(inverseEdge)) {
            edges.add(inverseEdge);
            outgoingEdges.get(target).add(inverseEdge);
            incomingEdges.get(source).add(inverseEdge);
        }

        return true;
    }

    /**
     * Removes an edge and its inverse from the graph.
     *
     * @param edge The edge to remove
     * @return true if the edge was removed, false if the edge was not in the graph
     */
    public boolean removeEdge(Edge edge) {
        if (edge == null || !edges.contains(edge)) {
            return false;
        }

        Node source = edge.getSource();
        Node target = edge.getTarget();

        // Remove the edge
        edges.remove(edge);
        outgoingEdges.get(source).remove(edge);
        incomingEdges.get(target).remove(edge);

        // Remove the inverse edge
        Edge inverseEdge = edge.createInverse();
        edges.remove(inverseEdge);
        outgoingEdges.get(target).remove(inverseEdge);
        incomingEdges.get(source).remove(inverseEdge);

        // Check if any nodes have become isolated and should be removed
        checkAndRemoveIsolatedNodes(source, target);

        return true;
    }

    /**
     * Checks if the given nodes have become isolated (have no edges) and removes them if so.
     *
     * @param nodesToCheck The nodes to check
     */
    private void checkAndRemoveIsolatedNodes(Node... nodesToCheck) {
        for (Node node : nodesToCheck) {
            if (outgoingEdges.get(node).isEmpty() && incomingEdges.get(node).isEmpty()) {
                removeNode(node);
            }
        }
    }

    /**
     * Removes a node and all its edges from the graph.
     *
     * @param node The node to remove
     * @return true if the node was removed, false if the node was not in the graph
     */
    public boolean removeNode(Node node) {
        if (node == null || !nodes.contains(node)) {
            return false;
        }

        // Remove all edges connected to this node
        Set<Edge> edgesToRemove = new HashSet<>();
        edgesToRemove.addAll(outgoingEdges.get(node));
        edgesToRemove.addAll(incomingEdges.get(node));

        for (Edge edge : edgesToRemove) {
            edges.remove(edge);

            Node otherNode = edge.getSource().equals(node) ? edge.getTarget() : edge.getSource();
            if (edge.getSource().equals(node)) {
                incomingEdges.get(otherNode).remove(edge);
            } else {
                outgoingEdges.get(otherNode).remove(edge);
            }
        }

        // Remove the node
        nodes.remove(node);
        outgoingEdges.remove(node);
        incomingEdges.remove(node);
        nodeRegistry.removeNode(node);

        return true;
    }

    /**
     * Gets all nodes in the graph.
     *
     * @return An unmodifiable view of the nodes
     */
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    /**
     * Gets all edges in the graph.
     *
     * @return An unmodifiable view of the edges
     */
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    /**
     * Gets all product nodes in the graph.
     *
     * @return A set of all product nodes
     */
    public Set<Product> getProducts() {
        Set<Product> products = new HashSet<>();
        for (Node node : nodes) {
            if (node.isProduct()) {
                products.add((Product) node);
            }
        }
        return products;
    }

    /**
     * Gets all category nodes in the graph.
     *
     * @return A set of all category nodes
     */
    public Set<Category> getCategories() {
        Set<Category> categories = new HashSet<>();
        for (Node node : nodes) {
            if (node.isCategory()) {
                categories.add((Category) node);
            }
        }
        return categories;
    }

    /**
     * Gets all outgoing edges from a node.
     *
     * @param node The node
     * @return A set of all outgoing edges, or an empty set if the node is not in the graph
     */
    public Set<Edge> getOutgoingEdges(Node node) {
        if (node == null || !nodes.contains(node)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(outgoingEdges.get(node));
    }

    /**
     * Gets all incoming edges to a node.
     *
     * @param node The node
     * @return A set of all incoming edges, or an empty set if the node is not in the graph
     */
    public Set<Edge> getIncomingEdges(Node node) {
        if (node == null || !nodes.contains(node)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(incomingEdges.get(node));
    }

    /**
     * Gets all outgoing edges of a specific relationship type from a node.
     *
     * @param node The node
     * @param relationship The relationship type
     * @return A set of all matching outgoing edges
     */
    public Set<Edge> getOutgoingEdgesByRelationship(Node node, RelationshipTypes relationship) {
        if (node == null || relationship == null || !nodes.contains(node)) {
            return Collections.emptySet();
        }

        Set<Edge> result = new HashSet<>();
        for (Edge edge : outgoingEdges.get(node)) {
            if (edge.getRelationship() == relationship) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Gets all incoming edges of a specific relationship type to a node.
     *
     * @param node The node
     * @param relationship The relationship type
     * @return A set of all matching incoming edges
     */
    public Set<Edge> getIncomingEdgesByRelationship(Node node, RelationshipTypes relationship) {
        if (node == null || relationship == null || !nodes.contains(node)) {
            return Collections.emptySet();
        }

        Set<Edge> result = new HashSet<>();
        for (Edge edge : incomingEdges.get(node)) {
            if (edge.getRelationship() == relationship) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Clears the graph, removing all nodes and edges.
     */
    public void clear() {
        nodes.clear();
        edges.clear();
        outgoingEdges.clear();
        incomingEdges.clear();
        nodeRegistry.clear();
    }
}