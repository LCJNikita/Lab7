package client.commands;

import movies.Movie;
import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Info command
 */
public class InfoCommand extends AbstractCommand {
    /**
     * Base constructor
     */
    public InfoCommand() {
    }

    /**
     * print collection info
     *
     * @param channel main channel
     */
    @Override
    public void execute(SocketChannel channel) throws IOException{
        try {
            Request request = new Request("info", null);

            Response response =  sendRequest(request, channel.socket());
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
