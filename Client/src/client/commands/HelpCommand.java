package client.commands;

import movies.MovieTreeSet;

import java.nio.channels.SocketChannel;

/**
 * Help command
 */
public class HelpCommand extends AbstractCommand {
    private final String HELP_MESSAGE;

    /**
     * Creates with help message
     *
     * @param helpMessage help message
     */
    public HelpCommand(String helpMessage) {
        this.HELP_MESSAGE = helpMessage;
    }

    /**
     * show help message
     *
     * @param channel main channel
     */
    @Override
    public void execute(SocketChannel channel) {
        System.out.println(this.HELP_MESSAGE);
    }
}
