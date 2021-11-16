import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.hibernate.cfg.Configuration;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnection.connection = null;
        try
        {
            DatabaseConnection.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",  // db should have been created via terminal by this point
                    "insert username here", "insert password here");
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

        // Creating the database. run this only once
        // this should be a separate method
        // if the database is deleted, this file should be manually deleted
        var databaseTablesCreatedFile = new File("database_tables_created");
        if (!databaseTablesCreatedFile.isFile())
        {
            try
            {
                databaseTablesCreatedFile.createNewFile();  //creates a new file
            }
            catch (IOException e)
            {
                System.out.println("Failed to create tables in the new database");
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);
            }

            DatabaseConnection.createDatabase();
        }

        //initializing sessionfactory
        try
        {
            ManageDatabase.factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex)
        {
            System.out.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        //ManageDatabase.fillDatabase();    only once really

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new UrfuScheduleBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            // log this better
        }
    }
}