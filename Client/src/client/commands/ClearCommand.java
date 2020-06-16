package client.commands;

import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * ClearCommand
 */
public class ClearCommand extends AbstractCommand {
    /**
     * Base constructor
     */
    public ClearCommand() {
    }

    /**
     * clear collection on server
     *
     * @param channel main channel
     * @throws IOException can be caused from server
     */
    @Override
    public void execute(SocketChannel channel) throws IOException {
        try {
            Request request = new Request("clear", null);
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
