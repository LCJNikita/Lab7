package server.commands;

import movies.MovieTreeSet;
import server.Response;
import server.database.DataBaseManager;

/**
 * Save command
 */
public class SaveCommand implements Command{
    private String fileName;

    /**
     * Save command constructor
     * @param fileName file to save collection
     */
    public SaveCommand(String fileName){
        this.fileName = fileName;
    }

    /**
     * Save collection to JSON file
     * @param treeSet main collection manager
     * @return Response to client
     */
    @Override
    public Response execute(MovieTreeSet treeSet, DataBaseManager manager) {
        return new Response(treeSet.saveJsonFile(this.fileName) ? "Collection was saved to " + this.fileName: "Collection was not saved", null);
    }
}
