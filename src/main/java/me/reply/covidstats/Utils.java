package me.reply.covidstats;

import java.io.IOException;

public class Utils {
    public static String invertDate(String incorrectDate){
        String[] args = incorrectDate.split("-");
        if(args.length != 3)
            return null;
        return args[2] + "-" + args[1] + "-" + args[0];
    }



    public static int executeShell(String command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        Process process = builder.start();
        return process.waitFor();
    }
}
