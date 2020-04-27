package me.reply.covidstats;

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

    private final static Logger logger = LoggerFactory.getLogger(Config.class);
    public List<String> admins;

    public static void saveDefaultConfig(String filename) throws IOException {
        File f = new File(filename);
        if(f.exists()) {
            if (!f.delete()) {
                logger.error("Error while deleting corrupted config file, delete() has returned false. System directory?");
                System.exit(-1);
            }
        }
        if(!f.createNewFile()){
            logger.error("Error while creating default config file, createNewFile() has returned false. System directory?");
            System.exit(-1);
        }
        String defaultConfig = "BOT_TOKEN: 'botToken'\n" +
                "BOT_USERNAME: 'botToken'";
        writeFile(defaultConfig);
    }

    public void loadAdminsFromFile(String filename) throws IOException {
        this.admins = new Vector<>();
        try{
            List<String> lines = FileUtils.readLines(new File(filename),"UTF-8");
            admins.addAll(lines);
        }catch (FileNotFoundException e){
            //maybe this is heroku
            admins.add(System.getenv("ADMIN"));
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
        return yaml.load(readFile(filename));
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

    private static void writeFile(String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("config.yml")));
        writer.write(s);
        writer.flush();
        writer.close();
    }

    public boolean isInUserlist(String id){
        for(String i : admins){
            if(i.equals(id))
                return true;
        }
        return false;
    }
}
