import org.json.simple.parser.JSONParser;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.hibernate.cfg.Configuration;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static Map<String, Tuple<String, Long>> groupMap = new HashMap<>();
    public static Map<String, String> idMap = new HashMap<>();     // a reverse map

    public static void main(String[] args)
    {
        logger.debug("Started main()");

        logger.debug("Reading groups.json");
        JSONArray groupsJSONArray = null;
        try
        {
            groupsJSONArray = (JSONArray) new JSONParser().parse(new FileReader("groups.json"));
        }
        catch (java.io.IOException | org.json.simple.parser.ParseException e)
        {
            logger.error("FATAL: failed to read the groups json object. The exception and stack trace follows.", e);
            System.exit(0);
        }

        for (Object group : groupsJSONArray)
        {
            var groupJSONObject = (JSONObject) group;
            groupMap.put(String.valueOf(groupJSONObject.get("id")), new Tuple<>((String)groupJSONObject.get("title"), 0L));
            idMap.put((String)groupJSONObject.get("title"), String.valueOf(groupJSONObject.get("id")));
            // System.out.println(String.valueOf(groupJSONObject.get("id")) + " " + (String)groupJSONObject.get("title"));
        }

        DatabaseConnection.connection = null;
        try
        {
            DatabaseConnection.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",  // db should have been created via terminal by this point
                    "Your username here", "Your password here");
        }
        catch (Exception e)
        {
            logger.error("FATAL: failed to access the database. The exception and stack trace follows.", e);
            System.exit(0);
        }

        logger.debug("Accessed the database successfully");
        try
        {
            DatabaseConnection.connection.close();
        }
        catch (SQLException e)
        {
            logger.warn("Unable to close connection to the database successfully. May be irrelevant but is definitely odd", e);
        }

        // Creating the database
        // this should be a separate method
        // if the database is deleted, this file should be manually deleted
        var databaseTablesCreatedFile = new File("database_tables_created");
        if (!databaseTablesCreatedFile.isFile())
        {
            logger.warn("database_tables_created file not found. This is expected behavior if the database was just created.");
            try
            {
                databaseTablesCreatedFile.createNewFile();  //creates a new file
            }
            catch (IOException e)
            {
                logger.error("FATAL: failed to access the database. The exception and stack trace follows.", e);
                System.exit(0);
            }

            DatabaseConnection.createDatabase();

            //initializing sessionfactory
            try
            {
                ManageDatabase.factory = new Configuration().configure().buildSessionFactory();
            }
            catch (Throwable e)
            {
                logger.error("FATAL: failed to create sessionFactory object. The exception and stack trace follows.", e);
                System.exit(0);
            }
            ManageDatabase.fillDatabase();
        }
        else
        {
            //initializing sessionfactory
            try
            {
                ManageDatabase.factory = new Configuration().configure().buildSessionFactory();
            }
            catch (Throwable e)
            {
                logger.error("FATAL: failed to create sessionFactory object. The exception and stack trace follows.", e);
                System.exit(0);
            }
        }

        try
        {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new UrfuScheduleBot());
        }
        catch (TelegramApiException e)
        {
            logger.error("FATAL: failed to start the telegram bot. The exception and stack trace follows.", e);
            System.exit(0);
        }
    }
}