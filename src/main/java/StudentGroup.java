public class StudentGroup       // a persistent class
{
    private String id;
    private Schedule schedule;

    public StudentGroup() {}
    public StudentGroup(String id, Schedule schedule) {
        this.id = id;
        this.schedule = schedule;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
    }
}