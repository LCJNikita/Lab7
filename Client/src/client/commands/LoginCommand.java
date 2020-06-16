package client.commands;

import server.Request;
import server.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class LoginCommand extends AbstractCommand {

    @Override
    public void execute(SocketChannel channel) throws IOException {
        try {

            Request request = new Request("check_user", null);

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
