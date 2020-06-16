package client.commands;


import server.database.User;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Command {
    void execute(SocketChannel channel) throws IOException;
    void setUser(User user);
    User getUser();
}
