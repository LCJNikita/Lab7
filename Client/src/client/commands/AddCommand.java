package client.commands;

import movies.Movie;
import server.Request;
import server.Response;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Add command
 */
public class AddCommand extends AbstractCommand {
    private String jsonStr;

    /**
     * Base constructor
     *
     * @param arg json str
     */
    public AddCommand(String arg) {
        this.jsonStr = arg;
    }


    /**
     * add new movie to server
     * @param channel main channel
     * @throws IOException may be caused by server connection
     */
    @Override
    public void execute(SocketChannel channel) throws IOException {
        try {
            Movie movie = Movie.getFromUserInput(System.in, System.out, "");
            Request request = new Request("add", movie);
            Response response = sendRequest(request, channel.socket());
            if (response == null) {
                System.err.println("Got null server response");
            } else {
                System.out.println(response.getText());
            }

        } catch (IOException e){
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
