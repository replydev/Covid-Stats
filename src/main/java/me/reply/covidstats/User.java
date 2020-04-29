package me.reply.covidstats;

public class User {

    private String userid;
    private String region;
    private String province;
    private boolean showNotification;


    public User(String userid,boolean showNotification) {
        this.userid = userid;
        this.region = null; //italy
        this.province = null; //no province selected
        this.showNotification = showNotification;
    }


    public String getUserid() {
        return userid;
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
}
