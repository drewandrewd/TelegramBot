import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class DatabaseUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);

    public static Tuple<Boolean, String> tryGetNewScheduleForGroup(String group)
    {
        var runtime = Runtime.getRuntime();
        Process process;
        try
        {
            System.out.println("GROUP:           " + group);
            process = runtime.exec("nodejs PATH TO src/main/java/js/Main.js " + group);
            process.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            logger.error("SEVERE: failed to execute nodejs parser script. The exception and stack trace follows.", e);
            return new Tuple<>(false, null);
        }

        JSONArray resultJSONArray = null;
        try
        {
            resultJSONArray = (JSONArray) new JSONParser().parse(new FileReader("result.json"));
        }
        catch (java.io.IOException | org.json.simple.parser.ParseException e)
        {
            logger.error("SEVERE: failed to read the new schedule's json object. The exception and stack trace follows.", e);
            return new Tuple<>(false, null);
        }

        var resultString = new StringBuilder();
        for (Object dayObject : resultJSONArray)
        {
            var dayJSONObject = (JSONObject)dayObject;
            resultString.append(dayJSONObject.get("weekday")).append(" ").append(dayJSONObject.get("weekdayName")).append("\n");
            for (var lessonObject : (JSONArray)dayJSONObject.get("lessons"))
            {
                var lessonJSONObject = (JSONObject)lessonObject;
                resultString.append(lessonJSONObject.get("time")).append("\n");
                var lessonName = lessonJSONObject.get("name").toString().replace("\n                                \n                                \n                                    ", "\n").replace("\n                                    \n                                        \n                                            ", "\n");
                resultString.append(lessonName).append("\n");
            }

            if (((JSONArray) dayJSONObject.get("lessons")).size() == 0)
                resultString.append("Ничего\n");
        }
        return new Tuple<>(true, resultString.toString());
    }
}
