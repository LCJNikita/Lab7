package server.commands;

import movies.Movie;
import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;

/**
 * Update Command
 */
public class UpdateCommand implements Command{
    private Movie movie;
    private long id;
    private User user;

    /**
     * Update command constructor
     * @param movie new elem
     * @param id id of old elem
     * @param user user
     */
    public UpdateCommand(Movie movie, long id, User user) {
        this.movie = movie;
        this.id = id;
        this.user = user;
    }

    /**
     * Replace elem with id with new data
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            Movie m = treeSet.getById(this.id);
            if (m == null) {
                response_text = "Deletion error";
            } else if (manager.deleteMovie(m, this.user) && manager.addMovie(this.movie, this.user)) {
                response_text = treeSet.update(this.id, this.movie) ? "Movie was updated" : "Movie was not updated";
            } else {
                response_text = "Updating failed";
            }
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = e.getMessage();
        }
        return new Response(response_text, null);
    }
}
