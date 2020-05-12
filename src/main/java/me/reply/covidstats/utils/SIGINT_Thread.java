package me.reply.covidstats.utils;

import me.reply.covidstats.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SIGINT_Thread implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(SIGINT_Thread.class);
    @Override
    public void run() {
        if(Bot.getInstance() == null)
            return;
        try {
            Bot.getInstance().backupUserList();
        } catch (IOException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }
}
