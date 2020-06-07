package me.reply.covidstats;

public class User {
    private final String userId;
    private String region;
    private String province;
    private boolean showNotification;
    private String notificationText;
    private boolean isMarkedForRemove;

    public User(String userId) {
        this.userId = userId;
        this.region = null; //italy
        this.province = null; //no province selected
        this.showNotification = true;
        this.isMarkedForRemove = false;
    }

    public String getUserId() {
        return userId;
    }
    public String getRegion() {
        return region;
    }
    public boolean isShowNotification() {
        return showNotification;
    }
    public void setRegion(String region){
        this.region = region;
    }
    public void setShowNotification(boolean showNotification){
        this.showNotification = showNotification;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province){
        this.province = province;
    }
    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
    public String getNotificationText() {
        return notificationText;
    }
    public boolean isMarkedForRemove() {
        return isMarkedForRemove;
    }
    public void setMarkedForRemove(boolean markedForRemove) {
        isMarkedForRemove = markedForRemove;
    }
}
