package server.commands;

import movies.Movie;
import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;

/**
 * Delete command
 */
public class DeleteCommand implements Command {
    private Movie movie;
    private User user;

    /**
     * Delete command constructor
     *
     * @param movie movie to be deleted
     */
    public DeleteCommand(Movie movie, User user) {
        this.movie = movie;
        this.user = user;
    }

    /**
     * delete element from main collection
     *
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            response_text = manager.deleteMovie(this.movie, this.user) && treeSet.remove(this.movie) ? "Movie was removed" : "Movie was not removed";
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = e.getMessage();
        }
        return new Response(response_text, null);
    }
}
