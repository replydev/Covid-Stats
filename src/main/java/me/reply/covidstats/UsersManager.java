package me.reply.covidstats;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class UsersManager {

    private final Vector<User> users;

    private final static Logger logger = LoggerFactory.getLogger(UsersManager.class);

    public UsersManager(){
        users = new Vector<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void addAll(User[] users){
        this.users.addAll(Arrays.asList(users));
    }

    public String getNotificationTextFromUser(String userId){
        for(User user : users){
            if(user.getUserId().equals(userId))
                return user.getNotificationText();
        }
        return null;
    }

    public void setNotificationText(String userId,String text){
        for(User user : users){
            if(user.getUserId().equals(userId))
                user.setNotificationText(text);
        }
    }

    public boolean isInUserList(String userid){
        for(User u : users){
            if(u.getUserId().equals(userid))
                return true;
        }
        return false;
    }

    public String getProvinceFromUser(String userId){
        for(User u : users){
            if(u.getUserId().equalsIgnoreCase(userId))
                return u.getProvince();
        }
        return null;
    }

    public String getRegionFromUser(String userId){
        for(User u : users){
            if(u.getUserId().equals(userId))
                return u.getRegion();
        }
        return null;
    }

    public File backupUserList() throws IOException {
        Gson g = new Gson();
        unlockRequests(users);
        String json = g.toJson(users);
        File f = new File("config/users_backup.json");
        if(f.exists())
            FileUtils.forceDelete(f);
        FileUtils.write(f,json,"UTF-8");
        logger.info("Ho salvato le impostazioni degli utenti");
        return f;
    }

    public void unlockRequests(Vector<User> users){
        for (User user : users)
            user.setCanMakeRequest(true);
    }

    public void setNotification(String userId,boolean value){
        for(User u : users){
            if(u.getUserId().equals(userId))
                u.setShowNotification(value);
        }
    }

    public void setRegion(String userId,String region){
        if(region.equalsIgnoreCase("Italia"))
            region = null;
        for(User user : users){
            if(user.getUserId().equals(userId)){
                user.setRegion(region);
                user.setProvince(null);
                return;
            }
        }
    }

    public void setProvince(String userId,String province){
        if(province.equalsIgnoreCase("Nessuna provincia"))
            province = null;
        for(User user : users){
            if(user.getUserId().equals(userId)){
                user.setProvince(province);
                return;
            }
        }
    }

    public void addUser(User u){
        this.users.add(u);
    }

    public void cleanUsers(){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).isMarkedForRemove()){
                users.remove(i);
                i--;
            }
        }
    }

    public int registeredUsers(){
        return this.users.size();
    }

    public void setMarkedForRemove(int id,boolean value){
        users.get(id).setMarkedForRemove(value);
    }
    public void setCanMakeRequest(int id,boolean value){
        users.get(id).setCanMakeRequest(value);
    }

    public int getIdFromUser(String userId){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserId().equalsIgnoreCase(userId))
                return i;
        }
        return -1;
    }
}
