package parser;

/**
 * Enum representing the different command types.
 *
 * @author uuifx
 */
public enum CommandType {
    /**
     * Command to load a database from a file.
     */
    LOAD_DATABASE,

    /**
     * Command to exit the application.
     */
    QUIT,

    /**
     * Command to add a node or edge to the graph.
     */
    ADD,

    /**
     * Command to remove a node or edge from the graph.
     */
    REMOVE,

    /**
     * Command to list or show information about nodes in the graph.
     */
    NODES,

    /**
     * Command to list or show information about edges in the graph.
     */
    EDGES,

    /**
     * Command to generate product recommendations based on the graph.
     */
    RECOMMEND,

    /**
     * Command to export the graph to a file in a specific format.
     */
    EXPORT,

    /**
     * Represents an unrecognized or invalid command.
     */
    UNKNOWN
}