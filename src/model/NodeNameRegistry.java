package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for node names to ensure uniqueness (case-insensitive).
 *
 * @author u-student
 */
public class NodeNameRegistry {
    private final Map<String, Node> nodesByLowerCaseName = new HashMap<>();

    /**
     * Registers a node in the registry.
     *
     * @param node The node to register
     * @return true if the node was registered, false if a node with the same name already exists
     */
    public boolean registerNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node must not be null");
        }

        String lowerCaseName = node.getLowerCaseName();
        if (nodesByLowerCaseName.containsKey(lowerCaseName)) {
            return false;
        }

        nodesByLowerCaseName.put(lowerCaseName, node);
        return true;
    }

    /**
     * Gets a node by its name (case-insensitive).
     *
     * @param name The name of the node
     * @return The node with the given name, or null if not found
     */
    public Node getNodeByName(String name) {
        if (name == null) {
            return null;
        }
        return nodesByLowerCaseName.get(name.toLowerCase());
    }

    /**
     * Removes a node from the registry.
     *
     * @param node The node to remove
     * @return true if the node was removed, false if the node was not in the registry
     */
    public boolean removeNode(Node node) {
        if (node == null) {
            return false;
        }
        return nodesByLowerCaseName.remove(node.getLowerCaseName()) != null;
    }

    /**
     * Removes a node by its name (case-insensitive).
     *
     * @param name The name of the node
     * @return true if the node was removed, false if no node with the given name was found
     */
    public boolean removeNodeByName(String name) {
        if (name == null) {
            return false;
        }
        return nodesByLowerCaseName.remove(name.toLowerCase()) != null;
    }

    /**
     * Checks if a node with the given name exists in the registry.
     *
     * @param name The name to check
     * @return true if a node with the given name exists, false otherwise
     */
    public boolean containsNodeName(String name) {
        if (name == null) {
            return false;
        }
        return nodesByLowerCaseName.containsKey(name.toLowerCase());
    }

    /**
     * Gets the number of nodes in the registry.
     *
     * @return The number of nodes
     */
    public int size() {
        return nodesByLowerCaseName.size();
    }

    /**
     * Clears the registry.
     */
    public void clear() {
        nodesByLowerCaseName.clear();
    }
}