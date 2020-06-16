package client;

import client.commands.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;

/**
 * Class that parse commands from user input string
 */
public class CommandParser {
    private final HashSet<String> AVAILABLE_COMMANDS;
    private final String HELP_MESSAGE;
    private Deque<String> commandsHistory;
    private HashSet<String> executedFiles;


    /**
     * Parser constructor
     *
     * @param commands    available command names
     * @param helpMessage help message, which will be displayed to user
     */
    CommandParser(HashSet<String> commands, String helpMessage) {
        this.AVAILABLE_COMMANDS = commands;
        this.HELP_MESSAGE = helpMessage;
        this.commandsHistory = new ArrayDeque<>();
        this.executedFiles = new HashSet<>();
    }

    /**
     * Parse command from user str
     *
     * @param userString user string
     * @return command
     */
    public Command parse(String userString) {
        String[] splitCommand = userString.split(" ", 2);
        String commandName = splitCommand[0].toLowerCase();
        commandsHistory.add(commandName);
        String args = "";
        try {
            args = splitCommand[1].toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            // pass
        }

        Command command = null;

        if (AVAILABLE_COMMANDS.contains(commandName)) {
            switch (commandName) {
                case "help":
                    command = new HelpCommand(this.HELP_MESSAGE);
                    break;
                case "add":
                    command = new AddCommand(args);
                    break;
                case "info":
                    command = new InfoCommand();
                    break;
                case "show":
                    command = new ShowCommand();
                    break;
                case "update":
                    command = new UpdateCommand(args);
                    break;
                case "remove_by_id":
                    command = new RemoveByIdCommand(args);
                    break;
                case "clear":
                    command = new ClearCommand();
                    break;
                case "execute_script":
                    command = new ExecuteScriptCommand(args, this);
                    break;
                case "exit":
                    command = new ExitCommand();
                    break;
                case "add_if_max":
                    command = new AddIfMaxCommand(args);
                    break;
                case "add_if_min":
                    command = new AddIfMinCommand(args);
                    break;
                case "history":
                    command = new HistoryCommand(commandsHistory);
                    break;
                case "max_by_oscars_count":
                    command = new MaxByOscarsCountCommand();
                    break;
                case "filter_less_than_mpaa_rating":
                    command = new FilterLessThanMpaaRatingCommand(args);
                    break;
                case "print_field_descending_oscars_count":
                    command = new PrintFieldDescendingOscarsCountCommand();
                    break;
                case "login":
                    command = new LoginCommand();
                    break;
                case "register":
                    command = new RegistrationCommand();
                    break;
            }
        }

        return command;
    }

    public HashSet<String> getExecutedFiles() {
        return executedFiles;
    }
}
