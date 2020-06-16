package server.commands;

import movies.MovieTreeSet;
import server.Response;
import server.database.DataBaseManager;

import javax.xml.crypto.Data;

public interface Command {
    Response execute(MovieTreeSet treeSet, DataBaseManager manager);
}
