package parser;

import model.Graph;
import util.Constants.CLI;
import util.Constants.Error;
import util.Constants.Regex;

/**
 * Parser for user commands in the recommendation system.
 *
 * @author uuifx
 */
public class CommandParser {
    private static final int COMMAND_PARTS_LIMIT = 2;
    private static final int LOAD_DATABASE_PARTS = 3;
    private static final int PREDICATE_OBJECT_PARTS = 2;
    private static final int COMMAND_INDEX = 0;
    private static final int CONTENT_INDEX = 1;
    private static final int PATH_INDEX = 2;
    private static final int SUBJECT_INDEX = 0;
    private static final int PREDICATE_INDEX = 0;
    private static final int OBJECT_INDEX = 1;

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
                if (parts.length > 1 && parts[CONTENT_INDEX].startsWith(CLI.DATABASE)) {
                    return CommandType.LOAD_DATABASE;
                }
                break;
            case CLI.QUIT:
                return CommandType.QUIT;
            case CLI.ADD:
                return CommandType.ADD;
            case CLI.REMOVE:
                return CommandType.REMOVE;
            case CLI.NODES:
                return CommandType.NODES;
            case CLI.EDGES:
                return CommandType.EDGES;
            case CLI.RECOMMEND:
                return CommandType.RECOMMEND;
            case CLI.EXPORT:
                return CommandType.EXPORT;
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
            System.out.println(Error.PREFIX + "Command empty");
            return null;
        }

        String[] parts = commandStr.trim().split(Regex.COMMAND_SPLIT_REGEX, LOAD_DATABASE_PARTS);
        if (parts.length < LOAD_DATABASE_PARTS || !parts[COMMAND_INDEX].equalsIgnoreCase(CLI.LOAD)
                || !parts[CONTENT_INDEX].equalsIgnoreCase(CLI.DATABASE)) {
            System.out.println(Error.PREFIX + "Invalid load database command format: " + commandStr);
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

        String predicate = predicateAndObject[PREDICATE_INDEX].trim();
        String object = predicateAndObject[OBJECT_INDEX].trim();

        return new String[]{subject, predicate, object};
    }
}