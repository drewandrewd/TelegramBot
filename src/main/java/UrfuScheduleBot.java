import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class UrfuScheduleBot extends TelegramLongPollingBot
{
    private static final Logger logger = LoggerFactory.getLogger(UrfuScheduleBot.class);

    @Override
    public void onUpdateReceived(Update update)
    {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText())
        {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());

            var receivedMessage = update.getMessage();
            var receivedMessageText = receivedMessage.getText();

            var helpText = "Команды:\n/myschedule\n/groupschedule <group>\n/setgroup <group>";
            try
            {
                if (receivedMessageText.substring(0, 5).equals("/help"))
                {
                    message.setText(helpText);
                }
                else if (receivedMessageText.substring(0, 9).equals("/database"))
                {
                    var text = ManageDatabase.listDatabase();
                    message.setText(text);
                }
                else if (receivedMessageText.substring(0, 9).equals("/setgroup"))
                {
                    if (!ManageDatabase.tryChangeStudentGroup(receivedMessage.getChatId(), receivedMessageText.substring(10)))
                        message.setText("Группа не найдена, проверьте правильность написания");
                    else
                        message.setText("Успех");
                }
                else if (receivedMessageText.substring(0, 11).equals("/deleteinfo"))
                {
                    var response = ManageDatabase.tryDeleteInfo(receivedMessage.getChatId());
                    message.setText(response);
                }
                else if (receivedMessageText.substring(0, 11).equals("/myschedule"))
                {
                    var tuple = ManageDatabase.tryGetOwnSchedule(receivedMessage.getChatId());
                    var userHasGroup = tuple.getItem1();
                    var schedule = tuple.getItem2();
                    if (userHasGroup)
                        message.setText(schedule);
                    else
                        message.setText("У вас ещё не задана своя группа, воспользуйтесь командой /setgroup <group>");
                }
                else if (receivedMessageText.substring(0, 14).equals("/groupschedule"))
                {
                    var tuple = ManageDatabase.tryGetScheduleByStudentGroupName(receivedMessageText.substring(15));
                    var groupExists = tuple.getItem1();
                    var schedule = tuple.getItem2();
                    if (groupExists)
                        message.setText(schedule);
                    else
                        message.setText("Группа не найдена, проверьте правильность написания");
                }
                else
                    message.setText(helpText);
            }
            catch (Exception e)
            {
                logger.error("Error reading a message. The exception and stack trace follows.", e);
                message.setText(helpText + "\nAn exception occured, see logs");
            }

            try
            {
                execute(message); // Call method to send the message
            }
            catch (TelegramApiException e)
            {
                logger.error("Error sending a message. The exception and stack trace follows.", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "Bot username goes here";
    }

    @Override
    public String getBotToken()
    {
        return "Bot token goes here";
    }
}
