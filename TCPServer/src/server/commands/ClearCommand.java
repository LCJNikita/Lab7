package server.commands;

import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;
import java.util.TreeSet;

/**
 * Clear command
 */
public class ClearCommand implements Command{
    private User user;

    public ClearCommand(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * Clear main collection
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            manager.clear(this.user);
            treeSet.setTreeSet(new TreeSet<>(manager.getMovies()));
            response_text = "Collection was cleared from user movies";
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = e.getMessage();
        }
        return new Response(response_text, null);
    }
}
