package mainPKG;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

public class db {

    public final String dbName = "OSU-Audit-Files";
    private final String dbUsername = "postgres";
    private final String dbPassword = "kellen";

    public db() {
        Connection connection = getConnection();

    }

    private Connection getConnection() {

        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbName, dbUsername, dbPassword);
            if (connection != null) {
                System.out.println("Connection established.");
            } else {
                System.out.println("Connection failed.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return connection;
    }
}
