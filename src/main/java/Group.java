public class Group       // a persistent class
{
    private String id;
    private int scheduleID;

    public Group() {}
    public Group(String id, int scheduleID) {
        this.id = id;
        this.scheduleID = scheduleID;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID( int scheduleID ) {
        this.scheduleID = scheduleID;
    }
}