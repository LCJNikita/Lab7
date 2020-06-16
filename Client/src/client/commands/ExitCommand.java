package client.commands;

import movies.MovieTreeSet;

import java.nio.channels.SocketChannel;

/**
 * Exit Command
 */
public class ExitCommand extends AbstractCommand {
    /**
     * Base constructor
     */
    public ExitCommand() {
    }

    /**
     * end client working
     * @param channel main channel
     */
    @Override
    public void execute(SocketChannel channel){
        try {
            channel.finishConnect();
            System.out.println("Connection finished");
        } catch (Exception e){
            System.err.println("Got exeption when connection finishing" + e.getMessage());
        }
    }
}
