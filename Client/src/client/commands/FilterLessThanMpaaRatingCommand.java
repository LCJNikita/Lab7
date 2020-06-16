package client.commands;

import movies.Movie;
import movies.MpaaRating;
import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Filter less than mpaa rating command
 */
public class FilterLessThanMpaaRatingCommand extends AbstractCommand {
    private MpaaRating rating;

    /**
     * constructor
     *
     * @param arg string rating name
     */
    public FilterLessThanMpaaRatingCommand(String arg) {
        this.rating = MpaaRating.getByName(arg);
    }

    /**
     * filter movies with rating less than arg
     *
     * @param channel main channel
     * @throws IOException can be caused by server
     */
    @Override
    public void execute(SocketChannel channel) throws IOException {
        if (this.rating != null) {
            try {
                Request request = new Request("get", null);
                Response response = sendRequest(request, channel.socket());
                if (response == null) {
                    System.err.println("Got null server response");
                } else {
                    ArrayList<Movie> movies = response.getMovies();
                    movies.removeIf(movie -> movie.getMpaaRating() == null || movie.getMpaaRating().compareTo(rating) > 0);
                    movies.forEach(System.out::println);

                }

            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("Incorrect rating");
        }
    }
}
