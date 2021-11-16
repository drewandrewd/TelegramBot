public class User       // a persistent class
{
    private long id;     //chatID (64 bit)
    private StudentGroup studentGroup;

    public User() {}
    public User(long id, StudentGroup studentGroup) {
        this.id = id;
        this.studentGroup = studentGroup;
    }

    public long getId()
    {
        return id;
    }

    public void setId (long id)
    {
        this.id = id;
    }

    public StudentGroup getStudentGroup()
    {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup)
    {
        this.studentGroup = studentGroup;
    }
}