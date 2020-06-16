package server.database;

import movies.*;
import server.AccessException;

import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DataBaseManager {
    private Connection connection;
    private Statement stmt = null;
    private transient PrintStream out;
    private Logger logger;

    public DataBaseManager(String dbName, int dbPort, String dbHost, String dbUser, String dbPassword) throws SQLException, ClassNotFoundException {
        this.out = System.out;
        Class.forName("org.postgresql.Driver");


        try {
            connection = DriverManager
                    .getConnection(String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbName), dbUser, dbPassword);
            this.stmt = connection.createStatement();
        } catch (NullPointerException ex) {
            throw new SQLException("Database connection error");

        }
    }

    private String toSQLDecimal(double d) {
        return Double.toString(d).replace(',', '.');
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * if logger is set up log to logger else sout
     *
     * @param msg msg to be logged
     */
    public void logMsg(String msg) {
        if (this.logger != null) {
            logger.info(msg);
        }
    }

    /**
     * if logger is set up log to logger else serr
     *
     * @param msg err to be logged
     */
    public void logErr(String msg) {
        if (this.logger != null) {
            logger.severe(msg);
        }
    }

    /**
     * Get MD5 hash from string
     *
     * @param st string to get hash from
     * @return SHA-256 hash in HEX format
     */
    public static String hash(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        StringBuilder hex = new StringBuilder(bigInt.toString(16));

        while (hex.length() < 32) {
            hex.insert(0, "0");
        }

        return hex.toString();
    }

    /**
     * @return
     */
    public ArrayList<Movie> getMovies() {
        ArrayList<Movie> list = new ArrayList<>();
        ArrayList<Integer> coor_ids = new ArrayList<>();
        ArrayList<Integer> pers_ids = new ArrayList<>();
        try {
            stmt.close();
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, creation_date,  oscars_count, total_box_office, rating, screenwriter_id, genre, id, coordinates_id, user_id FROM movie");
            logMsg("SELECT ALL FROM DB");
            while (!rs.isClosed() && rs.next()) {
                MpaaRating rating = null;
                MovieGenre genre = null;
                Person screenwriter = null;

                String mpaa_rating = rs.getString("rating");
                if (mpaa_rating != null) {
                    rating = MpaaRating.getByName(mpaa_rating);
                }

                pers_ids.add(rs.getInt("screenwriter_id"));

                if (rs.getString("genre") != null) {
                    genre = MovieGenre.getByName(rs.getString("genre"));
                }

                String name = rs.getString("name");
                int id = rs.getInt("id");
                long oscarsCount = rs.getLong("oscars_count");
                Long totalBoxOffice = rs.getLong("total_box_office");
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                int coor_id = rs.getInt("coordinates_id");
                coor_ids.add(coor_id);


                Movie m = new Movie(name, new Coordinates(0, 0), oscarsCount, totalBoxOffice, genre, rating, screenwriter);
                m.setCreationDate(creationDate);
                m.setId(id);
                logMsg("GET MOVIE FROM BD: " + m.toString());
                list.add(m);
            }
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setCoordinates(findCoordinatesById(coor_ids.get(i)));
                if (pers_ids.get(i) != null) {
                    list.get(i).setScreenwriter(findPersonById(pers_ids.get(i)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public User findUser(String username) throws SQLException {
        try {
            logMsg("Find user by name " + username);
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM movie_user WHERE username LIKE '%s' LIMIT 1", username));
            if (rs.next()) {
                return new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"));
            }
        } catch (SQLException e) {
            logErr(e.getMessage());
        }
        return null;

    }

    public User findUser(int id) throws SQLException {
        try {
            logMsg("Find user by id " + id);
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM movie_user WHERE id = %d LIMIT 1", id));
            if (rs.next()) {
                return new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"));
            }
        } catch (SQLException e) {
            logErr(e.getMessage());
        }
        return null;
    }

    public int findPersonId(Person person) throws SQLException {
        try {
            String query = String.format("SELECT id FROM person WHERE name LIKE '%s' AND weight = %s AND height = %d LIMIT 1",
                    person.getName(), toSQLDecimal(person.getWeight()), person.getHeight());
            logMsg("EXECUTE QUERY " + query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            logErr(e.getMessage());
        }
        return -1;
    }

    public Person findPersonById(int id) throws SQLException {
        try {
            String query = String.format("SELECT * FROM person WHERE id = %d LIMIT 1", id);
            logMsg("EXECUTE QUERY " + query);

            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return new Person(rs.getString("name"), rs.getLong("height"), rs.getFloat("weight"));
            }
        } catch (SQLException e) {
            logErr(e.getMessage());
        }
        return null;
    }


    public int findCoordinatesId(Coordinates coor) throws SQLException {
        try {
            String query = String.format("SELECT id FROM coordinates WHERE x = %d AND y = %s LIMIT 1",
                    coor.getX(), toSQLDecimal(coor.getY()));
            logMsg("EXECUTE QUERY " + query);

            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            logErr(e.getMessage());
        }
        return -1;
    }

    public Coordinates findCoordinatesById(int id) throws SQLException {
        String query = String.format("SELECT * FROM coordinates WHERE id = %d LIMIT 1", id);
        logMsg("EXECUTE QUERY " + query);

        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return new Coordinates(rs.getInt("x"), rs.getFloat("y"));
        }
        return null;
    }

    public boolean addPerson(Person person) {
        String query = String.format("INSERT INTO person ( name, height, weight ) VALUES ( '%s', %d, %s )",
                person.getName(), person.getHeight(), toSQLDecimal(person.getWeight()));
        logMsg("EXECUTE QUERY " + query);

        try {
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addCoordinates(Coordinates coor) {
        String query = String.format("INSERT INTO coordinates ( x, y ) VALUES ( %d, %s )",
                coor.getX(), toSQLDecimal(coor.getY()));
        logMsg("EXECUTE QUERY " + query);

        try {
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addMovie(Movie movie, User user) throws SQLException, AccessException {
        if (!checkUser(user)) {
            return false;
        }
        try {
            int coordinatesId = findCoordinatesId(movie.getCoordinates());
            if (coordinatesId < 0) {
                addCoordinates(movie.getCoordinates());
                coordinatesId = findCoordinatesId(movie.getCoordinates());
            }
            logMsg("COORDINATES_ID = " + coordinatesId);
            String screenwriterId = null;
            if (movie.getScreenwriter() != null) {
                int personId = findPersonId(movie.getScreenwriter());
                if (personId < 0) {
                    addPerson(movie.getScreenwriter());
                }
                screenwriterId = Integer.toString(findPersonId(movie.getScreenwriter()));
            }
            logMsg("SCREENWRITER_ID = " + screenwriterId);

            String query = String.format("INSERT INTO movie" +
                            " ( oscars_count, total_box_office, name, coordinates_id, user_id , rating, genre, screenwriter_id )" +
                            " VALUES ( %d, %d, '%s', %d, %d, %s, %s, %s)",
                    movie.getOscarsCount(),
                    movie.getTotalBoxOffice(),
                    movie.getName(),
                    coordinatesId,
                    user.getId(),
                    movie.getMpaaRating() != null ? String.format("'%s'", movie.getMpaaRating().getName()) : "NULL",
                    movie.getGenre() != null ? String.format("'%s'", movie.getGenre().getName()).toLowerCase() : "NULL",
                    screenwriterId != null ? String.format("%s", screenwriterId) : "NULL"
            );
            logMsg("EXECUTE QUERY  " + query);

            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            logErr(e.getMessage());
            return false;
        }
    }

    public boolean deleteMovie(Movie movie, User user) throws AccessException, SQLException {
        if (!checkUser(user)) {
            return false;
        }
        try {
            int coordinatesId = findCoordinatesId(movie.getCoordinates());
            String screenwriterId = null;
            if (movie.getScreenwriter() != null) {
                int personId = findPersonId(movie.getScreenwriter());
                if (personId < 0) {
                    addPerson(movie.getScreenwriter());
                }
                screenwriterId = Integer.toString(findPersonId(movie.getScreenwriter()));
            }
            String query = String.format("DELETE FROM movie WHERE oscars_count = %d " +
                            "AND total_box_office = %d AND name = '%s' AND rating %s AND genre %s " +
                            "AND screenwriter_id %s AND coordinates_id = %d AND user_id = %d",
                    movie.getOscarsCount(),
                    movie.getTotalBoxOffice(),
                    movie.getName(),
                    movie.getMpaaRating() != null ? String.format("= '%s'",movie.getMpaaRating().getName()) : "IS NULL",
                    movie.getGenre() != null ? String.format("= '%s'", movie.getGenre().getName()).toLowerCase() : "IS NULL",
                    screenwriterId != null ? String.format("= '%s'", screenwriterId): "IS NULL",
                    coordinatesId,
                    user.getId()
            );
            logMsg("EXECUTE QUERY " + query);
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void clear(User user) throws SQLException, AccessException {
        checkUser(user);
        String query = String.format("DELETE FROM movie WHERE user_id = %d", user.getId());
        logMsg("EXECUTE QUERY " + query);

        stmt.execute(query);
    }

    public boolean checkUser(User user) throws SQLException, AccessException {
        if (user == null) {
            throw new AccessException("User does not exist");
        }
        logMsg("CHECK USER " + user.toString());
        User found = findUser(user.getName());
        if (found == null) {
            throw new AccessException("User does not exist");
        } else {
            logMsg("CHECK hash and pass hash " + hash(user.getPassword()) + "  ==  " + found.getPassword());
            if (hash(user.getPassword()).equals(found.getPassword())) {
                user.setId(found.getId());
                return true;
            }
        }
        return false;
    }

    public boolean registerUser(String username, String password) throws SQLException, AccessException {
        User found = findUser(username);
        if (found != null) {
            throw new AccessException("User already exist");
        } else {
            String query = String.format("INSERT INTO movie_user (username, password_hash) VALUES ( '%s', '%s' )", username, hash(password));
            try {
                stmt.execute(query);
                return true;
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    public boolean registerUser(User user) throws SQLException, AccessException {
        if (user == null) {
            throw new AccessException("Got null user");
        }
        return registerUser(user.getName(), user.getPassword());
    }

    /**
     * @param len
     * @return
     */
    public static String getRandomPassword(int len) {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < len; i++) {
            password.append((char) ((int) (Math.random() * 20) + 65));
        }
        return password.toString();
    }


    public void setOut(PrintStream out) {
        this.out = out;
    }
}