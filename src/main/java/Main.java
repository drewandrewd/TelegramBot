import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnection.connection = null;
        try
        {
            DatabaseConnection.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
                    "your username here", "your password here");
        }
        catch (Exception e)
        {
            System.out.println("Failed to access the database");
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        System.out.println("Opened database successfully");
        DatabaseConnection.connection.close();

        //Creating the database. run this only once
        //DatabaseConnection.createDatabase();

        //ManageDatabase.fillDatabase();    broken

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new UrfuScheduleBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            // log this better
        }
    }
}
