import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Student implements Serializable
{
    private String ID;
    private String Name;
    private HashMap<String ,Course> enrolledCourse_summer;
    private HashMap<String ,Course> enrolledCourse_fall;
    private HashMap<String ,Course> enrolledCourse_winter;

    private int capacity_per_semester;
    private int totalCapacity;
    Logger logger;
    FileHandler fh;


    public Student(String id, String name) throws IOException

    {
        this.ID = id;
        this.Name = name;
        this.capacity_per_semester = 3;
        this.totalCapacity = 9; //(3 semesters, each semester 3 course max)
        enrolledCourse_summer = new HashMap<String ,Course>();
        enrolledCourse_fall = new HashMap<String ,Course>();
        enrolledCourse_winter = new HashMap<String ,Course>();

        logger = Logger.getLogger("advisor");
        fh = new FileHandler("/Users/veedaa/Desktop/DCRS/src/Logs/student.log");

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.info("advisor " + this.getID() + " created!" );
        logger.info(this.ID);
        logger.info(this.Name);


    }
    public String getName()
    {
        return this.Name;
    }

    public void addCourse_summer(Course course)
    {

        enrolledCourse_summer.put(course.getId(),course);
    }

    public HashMap<String ,Course> getEnrolledCourse_summer() {
        return enrolledCourse_summer;
    }

    public void addCourse_fall(Course course)
    {

        enrolledCourse_fall.put(course.getId(), course);
    }

    public HashMap<String ,Course> getEnrolledCourse_fall() {
        return enrolledCourse_fall;
    }

    public void addCourse_winter(Course course)
    {

        enrolledCourse_winter.put(course.getId(),course);
    }

    public HashMap<String ,Course> getEnrolledCourse_winter() {
        return enrolledCourse_winter;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getCapacity_per_semester() {
        return capacity_per_semester;
    }

    public void setCapacity_per_semester(int capacity_per_semester) {
        this.capacity_per_semester = capacity_per_semester;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

}
