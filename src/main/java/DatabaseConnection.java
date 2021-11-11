import org.hibernate.Session;
import org.hibernate.Transaction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection
{
    public static Connection connection;       // pointless?

    public static void createDatabase()
    {
        try
        {
            var connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                    "postgres", "ATOM intel1000 #ANVIL_932");
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE USERS " +
                    "(id NUMERIC PRIMARY KEY NOT NULL," +
                    "groupID CHAR(20))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE GROUPS " +
                    "(id CHAR(20) PRIMARY KEY NOT NULL," +
                    "scheduleID INT NOT NULL)";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE SCHEDULES " +
                    "(id INT PRIMARY KEY NOT NULL," +
                    "dayJSON JSON);";
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("Failed to access the database");
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        System.out.println("Created database successfully");
    }
}
