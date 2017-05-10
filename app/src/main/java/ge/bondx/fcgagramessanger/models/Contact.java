package ge.bondx.fcgagramessanger.models;

import java.io.Serializable;

/**
 * Created by Admin on 4/23/2017.
 */

public class Contact implements Serializable {


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getFacebookUrl() {
        return FacebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        FacebookUrl = facebookUrl;
    }

    public String getInstagramUrl() {
        return InstagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        InstagramUrl = instagramUrl;
    }

    public String getSkype() {
        return Skype;
    }

    public void setSkype(String skype) {
        Skype = skype;
    }

    public String getLang() {
        return Lang;
    }

    public void setLang(String lang) {
        Lang = lang;
    }

    public String getBiography() {
        return Biography;
    }

    public void setBiography(String biography) {
        Biography = biography;
    }

    public String getUPlayer() {
        return UPlayer;
    }

    public void setUPlayer(String UPlayer) {
        this.UPlayer = UPlayer;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public boolean isInTop4() {
        return IsInTop4;
    }

    public void setInTop4(boolean inTop4) {
        IsInTop4 = inTop4;
    }

    private int Id;
    private String FirstName;
    private String LastName;
    private String Position;
    private String ImageUrl;
    private String FacebookUrl;
    private String InstagramUrl;
    private String Skype;
    private String Lang;
    private String Biography;
    private String UPlayer;
    private String Category;
    private boolean IsInTop4;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    private String Email;

    public static final String CONTACT_ID = "Id";
    public static final String CONTACT_NAME = "FirstName";

    private int notificationId = 0;

    public int getNotificationId(){
        return  notificationId;
    }
    public void setNotificationId(int id){
        notificationId = id;
    }
}
