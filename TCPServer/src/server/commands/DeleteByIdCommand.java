package server.commands;

import movies.Movie;
import movies.MovieTreeSet;
import server.AccessException;
import server.Response;
import server.database.DataBaseManager;
import server.database.User;

import java.sql.SQLException;

public class DeleteByIdCommand implements Command {
    private long id;
    private User user;

    /**
     * Delete by id command constructor
     *
     * @param id element id
     */
    public DeleteByIdCommand(long id, User user) {
        this.id = id;
        this.user = user;
    }

    /**
     * remove element by id
     *
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        String response_text = "";
        try {
            Movie m = treeSet.getById(this.id);
            manager.checkUser(this.user);
            if (this.id != this.user.getId()) {
                response_text = "Movie can't be deleted. It's property of another user";
            } else {
                if (m == null) {
                    response_text = "Deletion error";
                } else {
                    response_text = manager.deleteMovie(m, this.user) && treeSet.removeById(this.id) ? "Movie with id " + id + " was removed" : "Movie with id " + id + " was not removed";
                }
            }
        } catch (SQLException e) {
            response_text = "DataBase connection error";
        } catch (AccessException e) {
            response_text = e.getMessage();
        }
        return new Response(response_text, null);
    }
}
