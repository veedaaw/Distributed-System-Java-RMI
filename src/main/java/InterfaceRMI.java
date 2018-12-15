import java.io.IOException;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface InterfaceRMI extends Remote
{


    public String addCourse (String courseID, String semester, int capacity, String desc) throws RemoteException;
    public String removeCourse (String courseID, String semester) throws RemoteException;
    public String[] CourseAvailability (String semester) throws RemoteException;

    public String enrolCourse (String studentID, String courseID, String semester) throws RemoteException;
    public String[] getClassSchedule (String studentID) throws RemoteException;
    public String dropCourse (String studentID, String courseID) throws RemoteException;
    public String addUserToEnrolledUser(String name, String id ) throws IOException;

    public boolean hasCapacity(String courseID) throws RemoteException;
}

