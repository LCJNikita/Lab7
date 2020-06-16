package client.commands;

import movies.MovieTreeSet;

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * HistoryCommand
 */
public class HistoryCommand extends AbstractCommand {
    private Deque<String> commands;

    /**
     * Base constructor
     *
     * @param commands commands names deque
     */
    public HistoryCommand(Deque<String> commands) {
        this.commands = new ArrayDeque<>(commands);
    }

    /**
     * Print last 15 commands
     *
     * @param channel main channel
     */
    @Override
    public void execute(SocketChannel channel) {
        for (int i = 0; i < 15; i++) {
            System.out.println(commands.pollLast());
            if (commands.isEmpty()) {
                break;
            }
        }
    }
}
