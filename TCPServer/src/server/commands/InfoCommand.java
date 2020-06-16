package server.commands;

import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;

/**
 * Info command
 */
public class InfoCommand implements Command {

    private User user;

    public InfoCommand(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * get info about collection from main manager
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            if (manager.checkUser(this.user)){
                response_text = treeSet.getInfo();
            } else {
                response_text = "User has no access";
            }
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = e.getMessage();
        }
        return new Response(response_text, null);
    }
}
