package ui;
import parser.CommandParser;
import parser.CommandType;
import parser.RecommendCommandParser;
import util.Constants;
import util.Constants.Error;
import java.io.IOException;
import java.util.Scanner;


/**
 * Main class for the product recommendation system.
 * @author uuifx
 */
public final class Main {
    private static final model.Graph GRAPH = new model.Graph();
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final CommandParser COMMAND_PARSER = new CommandParser();
    private static final RecommendCommandParser RECOMMEND_PARSER = new RecommendCommandParser();
    // Track whether a database has been loaded
    private static boolean databaseLoaded = false;
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
            // First, handle commands that dont need an initialized database
            if (commandType == CommandType.QUIT) {
                running = false;
                continue;
            } else if (commandType == CommandType.LOAD_DATABASE) {
                boolean success = CommandHandler.handleLoadDatabase(userInput, GRAPH, COMMAND_PARSER);
                if (success) {
                    databaseLoaded = true;  // Only set true if loading succeeded
                }
                continue;
            } else if (commandType == CommandType.UNKNOWN) {
                System.out.println(Error.PREFIX + Error.UNKNOWN_COMMAND + userInput);
                continue;
            }
            if (commandType == CommandType.NODES) {   // For nodes and edges commands
                if (!databaseLoaded) {
                    System.out.println(Error.PREFIX + Constants.Error.CANNOT_ADD_RELATIONSHIP_NO_DATABASE);
                    continue;
                }
                CommandHandler.handleNodesCommand(GRAPH);
                continue;
            } else if (commandType == CommandType.EDGES) {
                if (!databaseLoaded) {
                    System.out.println(Error.PREFIX + Constants.Error.CANNOT_ADD_RELATIONSHIP_NO_DATABASE);
                    continue;
                }
                CommandHandler.handleEdgesCommand(GRAPH);
                continue;
            } else if (commandType == CommandType.EXPORT) {
                if (!databaseLoaded) {
                    System.out.println(Error.PREFIX + Constants.Error.CANNOT_ADD_RELATIONSHIP_NO_DATABASE);
                    continue;
                }
                CommandHandler.handleExportCommand(GRAPH);
                continue;
            }
            if (!databaseLoaded) { // For commands that require a loaded database
                System.out.println(Error.PREFIX + Constants.Error.CANNOT_ADD_RELATIONSHIP_NO_DATABASE);
                continue;
            }
            switch (commandType) {
                case ADD:
                    CommandHandler.handleAddCommand(userInput, GRAPH, COMMAND_PARSER);
                    break;
                case REMOVE:
                    CommandHandler.handleRemoveCommand(userInput, GRAPH, COMMAND_PARSER);
                    break;
                case RECOMMEND:
                    CommandHandler.handleRecommendCommand(userInput, GRAPH, RECOMMEND_PARSER);
                    break;
                default:
                    break;
            }
        }
    }
}