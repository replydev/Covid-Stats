package me.reply.covidstats.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("unused")
public class Config {
    public String BOT_TOKEN;
    public String BOT_USERNAME;
    public String UPDATE_TIME;
    public int CHART_WIDTH;
    public int CHART_HEIGHT;

    private final static Logger logger = LoggerFactory.getLogger(Config.class);
    public List<String> admins;
    private int hour;
    private int minutes;

    public static void saveDefaultConfig(String filename) throws IOException {
        File f = new File(filename);
        if(f.exists()) {
            FileUtils.forceDelete(f);
        }
        if(!f.createNewFile()){
            logger.error("Error while creating default config file, createNewFile() has returned false. System directory?");
            System.exit(-1);
        }
        String defaultConfig = "BOT_TOKEN: 'botToken'\n" +
                "BOT_USERNAME: 'botUsername'\n" +
                "UPDATE_TIME: 'time'\n" +
                "CHART_WIDTH: 1536\n" +
                "CHART_HEIGHT: 864";
        FileUtils.write(new File("config/config.yml"),defaultConfig,"UTF-8");
    }

    public void loadAdminsFromFile(String filename) throws IOException {
        this.admins = new Vector<>();
        String envAdmin = System.getenv("ADMIN");
        if(envAdmin != null)
            this.admins.add(envAdmin);
        File f = new File(filename);
        if(!f.exists())
            if(!f.createNewFile())
                logger.error("Errore durante la creazione del file per la lista degli admin");
        try{
            List<String> lines = FileUtils.readLines(new File(filename),"UTF-8");
            admins.addAll(lines);
        }catch (FileNotFoundException e){
            logger.error("File \"admins.list\" non trovato...");
        }
    }

    public void addAdmin(String admin){
        admins.add(admin);
    }

    public static Config load(String filename) throws IOException {
        File f = new File(filename);
        if(!f.exists()){
            saveDefaultConfig(filename);
        }
        Yaml yaml = new Yaml(new Constructor(Config.class));
        Config c = yaml.load(readFile(filename));

        //heroku support
        if(c.BOT_TOKEN.equals("botToken"))
            c.BOT_TOKEN = System.getenv("TOKEN");
        if(c.BOT_USERNAME.equals("botUsername"))
            c.BOT_USERNAME = System.getenv("USERNAME");
        if(c.UPDATE_TIME.equals("time"))
            c.UPDATE_TIME = System.getenv("UPDATE_TIME");

        String[] split = c.UPDATE_TIME.split(":");
        assert split.length == 2;
        c.setUpdateHour(Integer.parseInt(split[0]));
        c.setUpdateMinutes(Integer.parseInt(split[1]));

        return c;
    }

    private static String readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String s;
        StringBuilder builder = new StringBuilder();
        while((s = reader.readLine()) != null){
            builder.append(s).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    public int getUpdateHour(){
        return hour;
    }
    public int getUpdateMinute(){
        return minutes;
    }
    public void setUpdateHour(int hour){
        this.hour = hour;
    }
    public void setUpdateMinutes(int minutes){
        this.minutes = minutes;
    }

    public boolean isInUserlist(String id){
        for(String i : admins){
            if(i.equals(id))
                return true;
        }
        return false;
    }
}
