import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class UrfuScheduleBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());

            var receivedMessageText = update.getMessage().getText();

            try {
                if (receivedMessageText.substring(0, 11).equals("/расписание"))// &&
                        //Integer.parseInt(receivedMessageText.substring(12, 18)) >= 980000 &&
                        //Integer.parseInt(receivedMessageText.substring(12, 18)) <= 989999)
                {
                    //message.setText("Database responses not implemented");
                    var text = ManageDatabase.listDatabase();
                    message.setText(text); //"22 October Friday: 09:00-10:30 Physics 10:40-12:10 Physics");
                }
                else
                    message.setText("Bot syntax:\n/расписание <группа>");
            } catch (Exception e) {
                message.setText("Bot syntax:\n/расписание <группа>  An exception occured see console output");
                System.out.println(e);
                e.printStackTrace();
            }

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "urfuschedulebot";
    }

    @Override
    public String getBotToken() {
        return "insert bot token here";
    }
}
