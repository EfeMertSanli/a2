package parser;

import model.Graph;
import util.Constants;
import util.Constants.CLI;
import util.Constants.Error;
import util.Constants.Regex;

import static parser.CommandParserConstants.COMMAND_PARTS_LIMIT;
import static parser.CommandParserConstants.LOAD_DATABASE_PARTS;
import static parser.CommandParserConstants.PATH_INDEX;
import static parser.CommandParserConstants.PREDICATE_OBJECT_PARTS;
import static parser.RecommendCommandParserConstants.COMMAND_INDEX;
import static parser.RecommendCommandParserConstants.CONTENT_INDEX;
import static ui.CommandHandlerConstants.SUBJECT_INDEX;

/**
 * Parser for user commands in the recommendation system.
 *
 * @author uuifx
 */
public class CommandParser {
    /**
     * Parses a command string and determines the command type.
     *
     * @param commandStr The command string
     * @return The command type
     */
    public CommandType parseCommandType(String commandStr) {
        if (commandStr == null || commandStr.trim().isEmpty()) {
            return CommandType.UNKNOWN;
        }

        String[] parts = commandStr.trim().split(Regex.COMMAND_SPLIT_REGEX, COMMAND_PARTS_LIMIT);
        String command = parts[COMMAND_INDEX].toLowerCase();

        switch (command) {
            case CLI.LOAD:
                if (parts.length == parser.CommandParserConstants.COMMAND_PARTS_LIMIT
                        && parts[CommandParserConstants.CONTENT_INDEX].startsWith(CLI.DATABASE)) {
                    return CommandType.LOAD_DATABASE;
                }
                break;
            case CLI.QUIT:
                // Quit should have no additional arguments
                if (parts.length == 1) {
                    return CommandType.QUIT;
                }
                break;
            case CLI.ADD:
                return CommandType.ADD;
            case CLI.REMOVE:
                return CommandType.REMOVE;
            case CLI.NODES:
                // Nodes should have no additional arguments
                if (parts.length == 1) {
                    return CommandType.NODES;
                }
                break;
            case CLI.EDGES:
                // Edges should have no additional arguments
                if (parts.length == 1) {
                    return CommandType.EDGES;
                }
                break;
            case CLI.RECOMMEND:
                return CommandType.RECOMMEND;
            case CLI.EXPORT:
                // Export should have no additional arguments
                if (parts.length == 1) {
                    return CommandType.EXPORT;
                }
                break;
            default:
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses a load database command and extracts the file path.
     * @param commandStr The command string
     * @return The file path or null if the command format is invalid
     */
    public String parseLoadDatabase(String commandStr) {
        if (commandStr == null || commandStr.trim().isEmpty()) {
            System.out.println(Error.PREFIX + Constants.Error.COMMAND_EMPTY);
            return null;
        }

        String[] parts = commandStr.trim().split(Regex.COMMAND_SPLIT_REGEX, LOAD_DATABASE_PARTS);
        if (parts.length < LOAD_DATABASE_PARTS || !parts[COMMAND_INDEX].equalsIgnoreCase(CLI.LOAD)
                || !parts[CONTENT_INDEX].equalsIgnoreCase(CLI.DATABASE)) {
            System.out.println(Error.PREFIX + Constants.Error.INVALID_LOAD_DATABASE_FORMAT + commandStr);
            return null;
        }

        return parts[PATH_INDEX];
    }

    /**
     * Parses an add or remove command and extracts the subject, predicate, and object.
     *
     * @param commandStr The command string
     * @return An array containing [subject, predicate, object]
     * @throws IllegalArgumentException If the command format is invalid
     */
    public String[] parseAddOrRemove(String commandStr) {
        if (commandStr == null || commandStr.trim().isEmpty()) {
            throw new IllegalArgumentException(Error.COMMAND_EMPTY);
        }

        // Extract command type (add or remove)
        String[] parts = commandStr.trim().split(Regex.COMMAND_SPLIT_REGEX, COMMAND_PARTS_LIMIT);
        if (parts.length < COMMAND_PARTS_LIMIT || (!parts[COMMAND_INDEX].equalsIgnoreCase(CLI.ADD)
                && !parts[COMMAND_INDEX].equalsIgnoreCase(CLI.REMOVE))) {
            throw new IllegalArgumentException(Error.COMMAND_START_ADD_REMOVE);
        }

        // Use the database parser's line parsing logic for the rest
        DatabaseParser dummyParser = new DatabaseParser(new Graph());
        dummyParser.parseLine(parts[CONTENT_INDEX]);
        // This is just for validation, we can't easily get the parsed values
        // so we'll just split the input string ourselves


        // Split into three parts with regex that respects the grammar
        String content = parts[CONTENT_INDEX].trim();
        String[] components = content.split(Regex.PREDICATE_SPLIT_REGEX, COMMAND_PARTS_LIMIT);

        if (components.length != COMMAND_PARTS_LIMIT) {
            throw new IllegalArgumentException(Error.INVALID_COMMAND_FORMAT + Error.SPLIT_SUBJECT_OBJECT);
        }

        String subject = components[SUBJECT_INDEX].trim();

        // Find the predicate
        String remainder = content.substring(subject.length()).trim();
        String[] predicateAndObject = remainder.split(Regex.COMMAND_SPLIT_REGEX, COMMAND_PARTS_LIMIT);

        if (predicateAndObject.length != PREDICATE_OBJECT_PARTS) {
            throw new IllegalArgumentException(Error.INVALID_COMMAND_FORMAT + Error.EXTRACT_PREDICATE_OBJECT);
        }
        String predicate = predicateAndObject[0].trim();  // Uses index 0 instead of PREDICATE_INDEX
        String object = predicateAndObject[1].trim();     // Uses index 1 instead of OBJECT_INDEX

        return new String[]{subject, predicate, object};
    }
}