package server.commands;

import movies.Movie;
import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;

/**
 * Add command
 */
public class AddCommand implements Command {
    private Movie movie;
    private User user;

    /**
     * Add command constructor
     * @param movie movie to be added
     */
    public AddCommand(Movie movie, User user) {
        this.movie = movie;
        this.user = user;
        this.movie.setCreationDate(java.time.LocalDate.now());
    }

    /**
     * add movie to main collection
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            response_text = manager.addMovie(movie, this.user) && treeSet.add(this.movie) ? "Movie was added" : "Movie was not added";
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = "User access error";
        }
        return new Response(response_text, null);
    }
}
