package me.reply.covidstats.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("unused")
public class Config {
    @SerializedName("BOT_TOKEN")
    private String bot_token;
    @SerializedName("BOT_USERNAME")
    private String bot_username;
    @SerializedName("UPDATE_TIME")
    private String update_time;

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
            logger.error("Errore durante la creazione del file di configurazione, hai i permessi di scrittura in questa cartella?");
            System.exit(-1);
        }
        String defaultConfig = "{\"BOT_TOKEN\":\"bot_token_here\",\"BOT_USERNAME\":\"bot_username_here\",\"UPDATE_TIME\":\"time\"}";
        FileUtils.write(new File("config/config.json"),defaultConfig,"UTF-8");
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
        Gson g = new Gson();
        Config c = g.fromJson(FileUtils.readFileToString(f,"UTF-8"),Config.class);

        //heroku support
        if(c.bot_token.equals("bot_token_here"))
            c.bot_token = System.getenv("TOKEN");
        if(c.bot_username.equals("bot_username_here"))
            c.bot_username = System.getenv("USERNAME");
        if(c.update_time.equals("time"))
            c.update_time = System.getenv("UPDATE_TIME");

        String[] split = c.update_time.split(":");
        assert split.length == 2;
        c.setUpdateHour(Integer.parseInt(split[0]));
        c.setUpdateMinutes(Integer.parseInt(split[1]));

        return c;
    }

    public String getBot_token() {
        return bot_token;
    }

    public String getBot_username() {
        return bot_username;
    }

    public String getUpdate_time() {
        return update_time;
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

    public boolean isInAdminsList(String id){
        for(String i : admins){
            if(i.equals(id))
                return true;
        }
        return false;
    }
}
