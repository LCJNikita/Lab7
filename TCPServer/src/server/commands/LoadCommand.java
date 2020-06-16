package server.commands;

import movies.Movie;
import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Load command
 */
public class LoadCommand implements Command {

    private User user;

    public LoadCommand(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * get all elements from collection manager
     *
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";

        try {
            if (manager.checkUser(this.user)) {
                ArrayList<Movie> movies = manager.getMovies();
                treeSet.setTreeSet(new TreeSet<>(movies));
                response_text = "Load all elements of collection";
                return new Response(response_text, treeSet.toArrayList());

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
