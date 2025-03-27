package ui;
import parser.CommandParser;
import parser.CommandType;
import parser.RecommendCommandParser;
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

            // For nodes and edges, we show empty results without an error
            if (commandType == CommandType.NODES) {
                CommandHandler.handleNodesCommand(GRAPH);
                continue;
            } else if (commandType == CommandType.EDGES) {
                CommandHandler.handleEdgesCommand(GRAPH);
                continue;
            } else if (commandType == CommandType.EXPORT) {
                CommandHandler.handleExportCommand(GRAPH);
                continue;
            }
            // For commands that require a loaded database
            if (!databaseLoaded) {
                System.out.println(Error.PREFIX + "No database loaded. Please load a database first.");
                continue;
            }

            // Handle the remaining database dependent commands
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
                    // Should never get here
                    break;
            }
        }
    }
}