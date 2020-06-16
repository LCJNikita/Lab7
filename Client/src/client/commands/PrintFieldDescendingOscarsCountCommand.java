package client.commands;

import movies.Movie;
import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;

/**
 * PrintFieldDescendingOscarsCountCommand
 */
public class PrintFieldDescendingOscarsCountCommand extends AbstractCommand {
    /**
     * Base constructor
     */
    public PrintFieldDescendingOscarsCountCommand() {
    }

    /**
     * print oscars count field values in reversive sorting
     *
     * @param channel main server channel
     * @throws IOException can be caused by server
     */
    @Override
    public void execute(SocketChannel channel) throws IOException {
        try {
            Request request = new Request("get", null);
            Response response = sendRequest(request, channel.socket());
            if (response == null) {
                System.err.println("Got null server response");
            } else {
                ArrayList<Movie> movies = response.getMovies();
                if (movies.isEmpty()) {
                    System.out.println("Empty");
                    return;
                }
                movies.sort(Movie::compareOscarsCountTo);
                Collections.reverse(movies);
                System.out.println();
                movies.forEach(movie -> System.out.print(movie.getOscarsCount() + " "));
                System.out.println();
            }

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
