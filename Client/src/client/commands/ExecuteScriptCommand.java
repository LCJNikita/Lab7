package client.commands;


import client.CommandParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * ExecuteScriptCommand
 */
public class ExecuteScriptCommand extends AbstractCommand {
    private String fileName;
    private CommandParser parser;

    /**
     * Base constructor
     *
     * @param fileName file with script
     * @param parser   parser to parse commands
     */
    public ExecuteScriptCommand(String fileName, CommandParser parser) {
        this.fileName = fileName;
        this.parser = parser;
    }

    /**
     * execute script from file
     *
     * @param channel main channel
     */
    @Override
    public void execute(SocketChannel channel) {
        File file;
        file = new File(fileName);
        if (!file.canRead()) {
            System.err.println("Cant read file " + fileName + ".\nScript execution failed.");
            return;
        }
        try (Scanner fin = new Scanner(file)) {
            String str;
            Command command;
            while (fin.hasNextLine()) {
                str = fin.nextLine();
                command = parser.parse(str);
                if (command != null && !command.getClass().toString().contains("ExecuteScriptCommand")) {
                    command.execute(channel);
                } else if (command != null && command.getClass().toString().contains("ExecuteScriptCommand")) {
                    String executeFileName = ((ExecuteScriptCommand) command).getFileName();
                    if (this.parser.getExecutedFiles().contains(executeFileName)) {
                        System.err.println("recursive execute_script_command is unavaliable, " + executeFileName + " file was already called.");
                    } else {
                        this.parser.getExecutedFiles().add(fileName);
                        command.execute(channel);
                    }
                } else {
                    System.err.println(String.format("No such command: %s", str));
                }

            }
        } catch (FileNotFoundException ex) {
            System.err.println("No such file " + fileName + ".\nScript execution failed.");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.err.println("Script failed");
        }
    }


    public String getFileName() {
        return fileName;
    }
}
