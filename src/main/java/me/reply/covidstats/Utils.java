package me.reply.covidstats;

public class Utils {
    public static String invertDate(String incorrectDate){
        String[] args = incorrectDate.split("-");
        if(args.length != 3)
            return null;
        return args[2] + "-" + args[1] + "-" + args[0];
    }
}
