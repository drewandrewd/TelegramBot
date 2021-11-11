import org.json.JSONObject;

public class Schedule       // a persistent class
{
    private int id;
    private String dayJSON;

    public Schedule() {}
    public Schedule(int id, String dayJSON) {
        this.id = id;
        this.dayJSON = dayJSON;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getDayJSON() {
        return dayJSON;
    }

    public void setDayJSON( String dayJSON ) {
        this.dayJSON = dayJSON;
    }
}