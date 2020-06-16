package client.commands;

import movies.Movie;
import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Show command
 */
public class ShowCommand extends AbstractCommand {
    /**
     * Base constructor
     */
    public ShowCommand() {
    }

    /**
     * show all collection elements
     * @param channel main channel
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
                movies.forEach(System.out::println);
                if (movies.isEmpty()){
                    System.out.println("Empty");
                }
            }
        } catch (IOException e){
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
