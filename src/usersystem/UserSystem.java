package usersystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Date;
import org.apache.log4j.Logger;

public class UserSystem {

    private static final Logger log = Logger.getLogger(UserSystem.class);
    private Connection conn;

    public UserSystem() {
    }

    public void connectToDB() throws SQLException {
        String dbURL = "jdbc:sqlite:UserSystem.db";
        conn = DriverManager.getConnection(dbURL);
        log.info("db connected");
    }

    public void createTableUser() throws SQLException {
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS user (\n"
                + "    id integer PRIMARY KEY,\n"
                + "    firstName text NOT NULL,\n"
                + "    lastName text NOT NULL,\n"
                + "    username text NOT NULL,\n"
                + "    password text NOT NULL,\n"
                + "    egn text NOT NULL,\n"
                + "    placeOfBirth text NOT NULL\n"
                + ");";
        try (Statement statement = conn.createStatement()) {
            int rowsAffected = statement.executeUpdate(createUserTableSql);
            if (rowsAffected > 0) {
                log.info("A table created!");
            }
        }
    }

    public void createTableOdit() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS odit (\n"
                + "    username text NOT NULL,\n"
                + "    time integer NOT NULL\n"
                + ");";
        try (Statement statement = conn.createStatement()) {
            int rowsAffected = statement.executeUpdate(sql);
            if (rowsAffected > 0) {
                log.info("A table created!");
            }
        }
    }

    public void addUser1() throws SQLException {
        String insertSql = "INSERT INTO user (id, firstName, lastName, username, password, egn, placeOfBirth) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pStatement = conn.prepareStatement(insertSql);
        pStatement.setInt(1, 1);
        pStatement.setString(2, "Petar");
        pStatement.setString(3, "Petrov");
        pStatement.setString(4, "petar");
        pStatement.setString(5, "1234");
        pStatement.setString(6, "7711225566");
        pStatement.setString(7, "Sofia");

        int rowsAffected = pStatement.executeUpdate();
        if (rowsAffected > 0) {
            log.info("A new user was inserted successfully!");
        }
    }

    public void addUser2() throws SQLException {
        String insertSql = "INSERT INTO user (id, firstName, lastName, username, password, egn, placeOfBirth) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pStatement = conn.prepareStatement(insertSql);
        pStatement.setInt(1, 2);
        pStatement.setString(2, "Ivan");
        pStatement.setString(3, "Ivanov");
        pStatement.setString(4, "ivan");
        pStatement.setString(5, "4444");
        pStatement.setString(6, "1122334455");
        pStatement.setString(7, "Plovdiv");

        int rowsAffected = pStatement.executeUpdate();
        if (rowsAffected > 0) {
            log.info("A new user was inserted successfully!");
        }
    }

    public void showUsers() throws SQLException {
        String selectSql = "SELECT * FROM user";
        try (Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(selectSql)) {
            int count = 0;
            System.out.println("all users:");
            while (result.next()) {
                String firstName = result.getString("firstName");
                String lastName = result.getString("lastName");
                String username = result.getString("username");
                String password = result.getString("password");

                Integer id = result.getInt("id");
                String egn = result.getString("egn");

                String output = "User #%d: ID: %s Name: %s %s EGN: %s username: %s password: %s ";
                System.out.println(String.format(output, ++count, id, firstName, lastName, egn, username, password));
            }
        }
    }

    public void loginUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username=? AND password=?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    Integer id = result.getInt("id");
                    String egn = result.getString("egn");

                    String output = "User logged in is with ID: %s Name: %s %s EGN: %s";
                    System.out.println(String.format(output, id, firstName, lastName, egn));
                    cleanupFailedLogins(username);
                } else {
                    log.info("Invalid login");
                    registerFailedLogin(username);
                }
            }
        }
    }

    public void registerFailedLogin(String username) throws SQLException {
        String insertSql = "INSERT INTO odit (username, time) VALUES (?, ?)";

        PreparedStatement pStatement = conn.prepareStatement(insertSql);
        pStatement.setString(1, username);
        pStatement.setLong(2, new Date().getTime());

        int rowsAffected = pStatement.executeUpdate();
        if (rowsAffected > 0) {
            log.info("A new odit entry inserted successfully!");
        }
    }

    public void cleanupFailedLogins(String username) throws SQLException {
        String sql = "DELETE FROM odit WHERE username=?";

        PreparedStatement pStatement = conn.prepareStatement(sql);
        pStatement.setString(1, username);

        int rowsAffected = pStatement.executeUpdate();
        if (rowsAffected > 0) {
            log.info(rowsAffected + " rows deleted from odit table");
        }
    }

    public boolean blockLogin(String username) throws SQLException {
        String sql = "SELECT COUNT(*), max(time) FROM odit WHERE username=?";
        PreparedStatement pStatement = conn.prepareStatement(sql);
        pStatement.setString(1, username);
        ResultSet result = pStatement.executeQuery();

        if (result.next()) {
            int count = result.getInt(1);
            long time = result.getLong(2);
            log.debug("odit count=" + count + " time=" + time);
            if (count >= 3) {
                boolean wait = new Date().getTime() < time + 60 * 1000;
                return wait;
            }
        }

        return false;
    }

    void close() throws SQLException {
        conn.close();
        log.info("db connection closed");
    }
}
