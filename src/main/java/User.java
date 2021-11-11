public class User       // a persistent class
{
    private long id;     //chatID (64 bit)
    private String groupID;

    public User() {}
    public User(long id, String groupID) {
        this.id = id;
        this.groupID = groupID;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}