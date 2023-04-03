package strimmenos295.SmartAlert;

import android.content.Context;

import java.io.Serializable;

public class MessageFromUserClass implements Serializable {
    private String Category, Comments, Photo, timestamp;
    private int id;
    private double Latitude, Longtitude;

    public MessageFromUserClass() {
        //this.id=0;

    }

    public MessageFromUserClass(double latitude, String category, String comments, double longtitude, String photo, String timestamp, int id) {
        this.Latitude = latitude;
        this.Category = category;
        this.Comments = comments;
        this.Longtitude = longtitude;
        this.Photo = photo;
        this.timestamp = timestamp;
        this.id = id;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(double longtitude) {
        Longtitude = longtitude;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toStringMessage(Context context){
        if(this.Category.equals("πυρκαγιά")){
            return context.getString(R.string.fire)+" "+this.timestamp;
        }else if(this.Category.equals("Πλυμήρα")){
            return context.getString(R.string.flood)+" "+this.timestamp;
        }else{
            return context.getString(R.string.earthquake)+" "+this.timestamp;
        }
    }
}
