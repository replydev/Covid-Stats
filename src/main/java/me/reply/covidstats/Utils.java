package me.reply.covidstats;

import java.util.Random;

public class Utils {

    public static String randomFilename(String extension){
        char[] alphabet = "abcdefghijklmnopqrstuvxyz1234567890".toCharArray();
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for(int i = 0; i < 10; i++){
            builder.append(alphabet[r.nextInt(alphabet.length)]);
        }
        return builder.append(extension).toString();
    }
}
