import java.io.Serializable;
import java.util.HashMap;

public class Course implements Serializable
{
    private String id;
    private int capacity;
    private String description;
    private HashMap<String, Student> enrolledUsers;

    public Course(String id, int capacity, String desc)
    {
        this.id = id;
        this.description = desc;
        this.capacity = capacity;
        enrolledUsers = new HashMap<String, Student>(capacity);
    }

    public void addStudentToCourse(Student student)
    {
            enrolledUsers.put(student.getID(), student);
    }

    public void removeStudentFromCourse(String stuID)

    {
        enrolledUsers.remove(stuID);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public HashMap<String, Student> getEnrolledUsers(){ return enrolledUsers;}
}
