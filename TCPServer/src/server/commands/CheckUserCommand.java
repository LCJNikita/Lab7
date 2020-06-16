package server.commands;

import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;

public class CheckUserCommand implements Command{
    private User user;

    public CheckUserCommand(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            response_text = manager.checkUser(this.user) ? "Success" : "Error";
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = e.getMessage();
        }
        return new Response(response_text, null);
    }
}
