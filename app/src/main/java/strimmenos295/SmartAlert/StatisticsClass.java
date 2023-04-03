package strimmenos295.SmartAlert;

import static android.provider.Settings.System.getString;

import android.content.Context;

public class StatisticsClass {
    private String Category, timestamp, uid, status;
    private int id;
    private double Latitude, Longtitude;


    public StatisticsClass() {
    }

    public StatisticsClass(String category, String timestamp, String uid, String status, int id, double latitude, double longtitude) {
        Category = category;
        this.timestamp = timestamp;
        this.uid = uid;
        this.status = status;
        this.id = id;
        Latitude = latitude;
        Longtitude = longtitude;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(double longtitude) {
        Longtitude = longtitude;
    }

    public String toStringMessage(Context context){
        if (this.status.equals("Σε Αναμονή")&&this.Category.equals("πυρκαγιά")){
            return context.getString(R.string.onDemand)+" "+context.getString(R.string.fire)+" "+this.timestamp;
        }else if (this.status.equals("Σε Αναμονή")&&this.Category.equals("Πλυμήρα")){
            return context.getString(R.string.onDemand)+" "+context.getString(R.string.flood)+" "+this.timestamp;

        }else if (this.status.equals("Σε Αναμονή")&&this.Category.equals("Σεισμός")){
            return context.getString(R.string.onDemand)+" "+context.getString(R.string.earthquake)+" "+this.timestamp;

        }else if (this.status.equals("Ενεργοποιήθηκε")&&this.Category.equals("πυρκαγιά")){
            return context.getString(R.string.energized)+" "+context.getString(R.string.fire)+" "+this.timestamp;

        }else if (this.status.equals("Ενεργοποιήθηκε")&&this.Category.equals("Πλυμήρα")){
            return context.getString(R.string.energized)+" "+context.getString(R.string.flood)+" "+this.timestamp;

        }else if (this.status.equals("Ενεργοποιήθηκε")&&this.Category.equals("Σεισμός")){
            return context.getString(R.string.energized)+" "+context.getString(R.string.earthquake)+" "+this.timestamp;

        }else if (this.status.equals("Απορρίφθηκε")&&this.Category.equals("πυρκαγιά")){
            return context.getString(R.string.rejected)+" "+context.getString(R.string.fire)+" "+this.timestamp;

        }else if (this.status.equals("Απορρίφθηκε")&&this.Category.equals("Πλυμήρα")){
            return context.getString(R.string.rejected)+" "+context.getString(R.string.flood)+" "+this.timestamp;

        }else {
            return context.getString(R.string.rejected)+" "+context.getString(R.string.earthquake)+" "+this.timestamp;
        }
    }
}
