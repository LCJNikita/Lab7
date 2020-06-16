package client.commands;

import movies.Movie;
import server.Request;
import server.Response;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * AddIfMin Command
 */
public class AddIfMinCommand extends AbstractCommand {
    private String jsonStr;

    /**
     * Base constructor
     *
     * @param arg json str
     */
    public AddIfMinCommand(String arg) {
        this.jsonStr = arg;
    }

    /**
     * add movie if it is min of existance
     * @param channel main channel
     * @throws IOException
     */
    @Override
    public void execute(SocketChannel channel) throws IOException {
        try {
            Movie movie = Movie.getFromUserInput(System.in, System.out, "");
            SocketAddress adr = channel.getRemoteAddress();
            Request request = new Request("get", null);
            Response response = sendRequest(request, channel.socket());
            channel = SocketChannel.open(adr);
            if (response == null) {
                System.err.println("Got null server response");
            } else {
                ArrayList<Movie> movies = response.getMovies();
                if (movies.isEmpty() || movie.compareTo(movies.stream().min(Movie::compareTo).get()) <= 0) {
                    request = new Request("add", movie);
                    response = sendRequest(request, channel.socket());
                    if (response == null) {
                        System.err.println("Got null server response");
                    } else {
                        System.out.println(response.getText());
                    }
                }
            }

        } catch (IOException e){
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
