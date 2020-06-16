package server;


import movies.Movie;

import java.util.HashSet;
import java.util.NoSuchElementException;

import server.commands.*;
import server.database.User;

/**
 * Class that parse commands from user input string
 */
public class CommandParser {
    private final HashSet<String> AVAILABLE_COMMANDS;


    /**
     * Parser constructor
     *
     * @param commands    available command names
     */
    CommandParser(HashSet<String> commands) {
        this.AVAILABLE_COMMANDS = commands;
    }

    /**
     * Parse command from client Request object
     *
     * @param request user command request
     * @return command
     * @throws NoSuchElementException if there is no such command
     */
    public Command parse(Request request) throws NoSuchElementException{
        String commandName = request.getMethod();
        String text_arg = "";
        if (commandName.contains("=")){
            String [] splited = commandName.split("=", 2);
            commandName = splited[0];
            text_arg = splited[1];
        }
        Movie arg = request.getArg();
        User user = request.getUser();

        Command command = null;

        if (AVAILABLE_COMMANDS.contains(commandName)) {
            switch (commandName) {
                case "get":
                    command = new LoadCommand(user);
                    break;
                case "add":
                    command = new AddCommand(arg, user);
                    break;
                case "info":
                    command = new InfoCommand(user);
                    break;
                case "delete":
                    command = new DeleteCommand(arg, user);
                    break;
                case "delete_by_id":
                    command = new DeleteByIdCommand(Long.parseLong(text_arg), user);
                    break;
                case "update":
                    command = new UpdateCommand(arg, Long.parseLong(text_arg), user);
                    break;
                case "clear":
                    command = new ClearCommand(user);
                    break;
                case "save":
                    command = new SaveCommand(text_arg);
                    break;
                case "register":
                    command = new AddUserCommand(user);
                    break;
                case "check_user":
                    command = new CheckUserCommand(user);
                    break;
            }
        } else {
            throw new NoSuchElementException("No such server command");
        }

        return command;
    }
}
