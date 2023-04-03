package strimmenos295.SmartAlert;

import android.content.Context;

public class MessageFromAdminClass {
    private String Category;
    private String timestamp;
    private int id;
    private double Latitude, Longtitude;

    public MessageFromAdminClass() {
        //this.id=0;

    }

    public MessageFromAdminClass(double latitude, String category, double longtitude, String timestamp, int id) {
        this.Latitude = latitude;
        this.Category = category;
        this.Longtitude = longtitude;
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

    public double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(double longtitude) {
        Longtitude = longtitude;
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
        //return this.id+" "+this.Category+" "+this.Latitude+" "+this.Longtitude+" "+this.timestamp;
        if(this.Category.equals("πυρκαγιά")){
            return context.getString(R.string.fire)+" "+this.Latitude+" "+this.Longtitude+" "+this.timestamp;
        }else if(this.Category.equals("Πλυμήρα")){
            return context.getString(R.string.flood)+" "+this.Latitude+" "+this.Longtitude+" "+this.timestamp;
        }else{
            return context.getString(R.string.earthquake)+" "+this.Latitude+" "+this.Longtitude+" "+this.timestamp;
        }
    }
}
