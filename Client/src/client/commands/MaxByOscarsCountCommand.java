package client.commands;

import movies.Movie;
import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * MaxByOscarsCountCommand
 */
public class MaxByOscarsCountCommand extends AbstractCommand {
    /**
     * Base constructor
     */
    public MaxByOscarsCountCommand() {
    }

    /**
     * show elem with max oscars count
     *
     * @param channel
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
                Movie maxMovie = movies.stream().max(Movie::compareOscarsCountTo).get();
                System.out.println(maxMovie.toString());
            }

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
