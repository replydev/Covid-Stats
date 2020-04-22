package me.reply.covidstats;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("unused")
public class Config {
    public String BOT_TOKEN;
    public String BOT_USERNAME;
    public String START_DATE;

    public List<String> admins;

    public static void saveDefaultConfig(String filename) throws IOException {
        File f = new File(filename);
        if(f.exists()) {
            if (!f.delete()) {
                System.out.println("Error while deleting corrupted config file, delete() has returned false. System directory?");
                System.exit(-1);
            }
        }
        if(!f.createNewFile()){
            System.out.println("Error while creating default config file, createNewFile() has returned false. System directory?");
            System.exit(-1);
        }
        String defaultConfig = "BOT_TOKEN: 'token_here'\n" +
                "BOT_USERNAME: 'username_here'\n" +
                "START_DATE: '01-01-2000'";
        writeFile("config.yml",defaultConfig);
    }

    public void loadAdminsFromFile(String filename) throws IOException {
        this.admins = new Vector<>();
        try{
            List<String> lines = FileUtils.readLines(new File(filename),"UTF-8");
            admins.addAll(lines);
        }catch (FileNotFoundException ignored){

        }
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

    private static void writeFile(String filename,String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
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
