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

            switch (commandType) {
                case LOAD_DATABASE:
                    CommandHandler.handleLoadDatabase(userInput, GRAPH, COMMAND_PARSER);
                    break;
                case QUIT:
                    running = false;
                    break;
                case ADD:
                    CommandHandler.handleAddCommand(userInput, GRAPH, COMMAND_PARSER);
                    break;
                case REMOVE:
                    CommandHandler.handleRemoveCommand(userInput, GRAPH, COMMAND_PARSER);
                    break;
                case NODES:
                    CommandHandler.handleNodesCommand(GRAPH);
                    break;
                case EDGES:
                    CommandHandler.handleEdgesCommand(GRAPH);
                    break;
                case RECOMMEND:
                    CommandHandler.handleRecommendCommand(userInput, GRAPH, RECOMMEND_PARSER);
                    break;
                case EXPORT:
                    CommandHandler.handleExportCommand(GRAPH);
                    break;
                case UNKNOWN:
                default:
                    System.out.println(Error.PREFIX + Error.UNKNOWN_COMMAND + userInput);
                    break;
            }
        }
    }
}