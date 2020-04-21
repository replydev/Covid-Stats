package me.reply.covidstats;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

@SuppressWarnings("unused")
public class Config {

    public String BOT_TOKEN;
    public String BOT_USERNAME;

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
                "BOT_USERNAME: 'username_here'";
        writeFile("config.yml",defaultConfig);
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
}
