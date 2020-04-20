import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler {

    private final ExecutorService threads;

    private DBManager dbManager;

    public void handle(String command,long chatId){

        String[] commandSplitted = command.split(" ");
        String commandName = commandSplitted[0];
        String[] args = getArgs(commandSplitted);

        switch(commandName){
            case "/start":
                threads.submit(() -> sendMessage("Welcome to CovidBot",chatId));
                break;
            case "/add":
                if(args.length != 4){
                    sendMessage("Please provide correct args",chatId);
                    return;
                }
                DayData dayData = new DayData(
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]),
                        LocalDate.now()
                );
                try {
                    dbManager.addData(dayData);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "/infected":
                try {
                    CovidData covidData = dbManager.getData();
                    File f = covidData.currentlyInfectedGraph();
                    sendPhoto(f,chatId);
                    f.delete();
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            default:

        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);
        try {
            dbManager = new DBManager("database.db");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private String[] getArgs(String[] commandSplitted){
        if(commandSplitted.length <= 1)
            return new String[0];
        List<String> temp = Arrays.asList(commandSplitted);
        temp.remove(0);
        return (String[]) temp.toArray();
    }

    private void sendMessage(String text, long chatId){
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendPhoto(File f, long chatId){
        SendPhoto photo = new SendPhoto()
                .setPhoto(f)
                .setChatId(chatId);
        try {
            Bot.getInstance().execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
