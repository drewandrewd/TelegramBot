import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    public static Connection connection;

    public static void createDatabase()
    {
        try
        {
            var connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                    "Your username here", "Your password here");
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE USERS " +
                    "(id NUMERIC PRIMARY KEY NOT NULL," +
                    "studentGroup CHAR(20))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE STUDENTGROUPS " +
                    "(id CHAR(200) PRIMARY KEY NOT NULL," +
                    "schedule INT NOT NULL)";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE SCHEDULES " +
                    "(id INT PRIMARY KEY NOT NULL," +
                    "dayJSON TEXT);";       // "dayJSON JSON);";
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();
        }
        catch (Exception e)
        {
            logger.error("FATAL: failed to create database tables. The exception and stack trace follows.", e);
            System.exit(0);
        }

        logger.debug("Created database tables successfully.");
    }
}
