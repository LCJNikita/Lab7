package client.commands;

import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * RemoveByIdCommand
 */
public class RemoveByIdCommand extends AbstractCommand {
    private long id;
    private boolean isCorrectId;

    /**
     * Base constructor
     *
     * @param arg elem id
     */
    public RemoveByIdCommand(String arg) {
        try {
            this.id = Long.parseLong(arg);
            this.isCorrectId = true;
        } catch (NumberFormatException ex) {
            System.err.println("Incorrect id");
        }
    }


    /**
     * remove element by id
     *
     * @param channel main channel
     * @throws IOException can be caused by server
     */
    @Override
    public void execute(SocketChannel channel) throws IOException {
        try {
            Request request = new Request("delete_by_id=" + this.id, null);
            Response response = sendRequest(request, channel.socket());
            if (response == null) {
                System.err.println("Got null server response");
            } else {
                System.out.println(response.getText());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Remove failed");

            System.err.println(e.getMessage());
        }
    }
}
