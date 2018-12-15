import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class ImplSOENServer extends UnicastRemoteObject implements InterfaceRMI
{
        private static HashMap<String, String> inner_map_summer;
        private static HashMap<String, String> inner_map_fall;
        private static HashMap<String, String> inner_map_winter;

        private static HashMap<String, HashMap<String, String>> outer_map;
        private static HashMap<String, Student> enrolledUsers;

        // a reference to a course object which stores a course object and its id for further usage.
        private static HashMap<String, Course> courseReference ;



        Logger logger ;
    //    FileHandler fh;



    public ImplSOENServer() throws IOException
        {
            //super();
            inner_map_summer = new HashMap<String, String>();
            inner_map_fall = new HashMap<String, String>();
            inner_map_winter = new HashMap<String, String>();
            outer_map = new HashMap<String, HashMap<String, String>>();
            enrolledUsers = new HashMap<String, Student>();
            courseReference = new HashMap<String, Course>();

        }

        @Override

        public String addCourse(String courseID, String semester, int capacity, String desc) throws RemoteException{
        String result = null;
        Course reference = new Course(courseID, capacity, desc);

        // if the semester already doesn't exists in our outer map which is the main database

        if(!getOuter_map().containsKey(semester))
        {
            HashMap <String, String> _course = new HashMap<String, String>();
            _course.put(courseID, desc);

            //set  a reference of this course base on its id for further needs
            setCourseReference(reference);

            String season = semester.toLowerCase();

            switch(season)
            {
                case "summer":
                    setInner_map_summer(courseID, desc);
                    setOuter_map(semester, _course );
                    break;
                case "fall":
                    setInner_map_fall(courseID, desc);
                    setOuter_map(semester, _course );
                    break;
                case "winter":
                    setInner_map_winter(courseID, desc);
                    setOuter_map(semester, _course );
                    break;
                default:
                    break;
            }


            result = "course " + courseID + " added to " + semester;
            System.out.println(result);

        }


        // if the semester already exists.

        else if(getOuter_map().containsKey(semester))
        {
            // if this course is not repetitive

            if(!getOuter_map().get(semester).containsKey(courseID))

            {

                //set  a reference of this course base on its id for further needs
                setCourseReference(reference);

                HashMap <String, String> _course2 = new HashMap<String, String>();
                _course2.put(courseID, desc);

                String season = semester.toLowerCase();

                switch(season)
                {
                    case "summer":
                        setInner_map_summer(courseID, desc);
                        getOuter_map().get(semester).put(courseID, desc);
                        break;
                    case "fall":
                        setInner_map_fall(courseID, desc);
                        getOuter_map().get(semester).put(courseID, desc);
                        break;
                    case "winter":
                        setInner_map_winter(courseID, desc);
                        getOuter_map().get(semester).put(courseID, desc);
                        break;
                    default:
                        break;
                }


                result = "course " + courseID + " added to " + semester ;
                System.out.println(result);

            }

            // if the course is repetitive in a specific semester
            else
            {
                result = "This course: "+ courseID+ " already exists in database";
                System.out.println(result);


            }
        }

        //logger.info(result);
        return result;

    }


        @Override
        public String removeCourse(String courseID, String semester) throws RemoteException{
        Course course = getCourseReference().get(courseID);

        String result = null;
        if( !getCourseReference().containsKey(courseID))
        {
            result = "Course not found in database";
            System.out.println(result);


        }
        else if (!getOuter_map().containsKey(semester))
        {
            result = "There is no course in this semester to be removed";
            System.out.println(result);


        }
        else if(getOuter_map().containsKey(semester))
        {
            if(semester.toLowerCase().equals("summer"))
            {
                getInner_map_summer().remove(courseID);
                getOuter_map().get(semester).remove(courseID);



                for(Student student : course.getEnrolledUsers().values())
                {
                    dropCourse(student.getID(), courseID);
                }

                result = "Course successfully removed!";
                System.out.println(result);



            }
            else if(semester.toLowerCase().equals("fall"))
            {
                getInner_map_fall().remove(courseID);
                getOuter_map().get(semester).remove(courseID);

                for(Student student : course.getEnrolledUsers().values())
                {
                    dropCourse(student.getID(), courseID);
                }

                result = "Course successfully removed!";
                System.out.println(result);



            }
            else if(semester.toLowerCase().equals("winter"))
            {
                getInner_map_winter().remove(courseID);
                getOuter_map().get(semester).remove(courseID);


                for(Student student : course.getEnrolledUsers().values())
                {
                    dropCourse(student.getID(), courseID);
                }

                result = "Course successfully removed!";
                System.out.println(result);



            }

            else
            {
                result = "Something must be wrong!";
                System.out.println(result);
            }
        }
        // logger.info(result);
        return result;
    }



        @Override
        public String[] CourseAvailability(String semester)  throws RemoteException{

        ArrayList<String> returnCourses = new ArrayList<String>();
        String[] result = new String[returnCourses.size()];


        if(semester.toLowerCase().equals("summer"))
        {
            for(String key: getInner_map_summer().keySet())
            {
                returnCourses.add(getCourseReference().get(key).getId());
                returnCourses.add
                        (String.valueOf
                                ((getCourseReference().get(key).getCapacity() - getCourseReference().get(key).getEnrolledUsers().size()))
                        );
            }
        }

        else if(semester.toLowerCase().equals("fall"))
        {
            for(String key: getInner_map_fall().keySet())
            {
                returnCourses.add(getCourseReference().get(key).getId());
                returnCourses.add
                        (String.valueOf
                                ((getCourseReference().get(key).getCapacity() - getCourseReference().get(key).getEnrolledUsers().size()))
                        );
            }
        }

        else if(semester.toLowerCase().equals("winter"))
        {
            for(String key: getInner_map_winter().keySet())
            {
                returnCourses.add(getCourseReference().get(key).getId());
                returnCourses.add
                        (String.valueOf
                                ((getCourseReference().get(key).getCapacity() - getCourseReference().get(key).getEnrolledUsers().size()))
                        );
            }
        }


        String[] _result = new String[]{"0"};
        if(returnCourses.size()>0)
        {
            _result = returnCourses.toArray(result);

        }

        return _result;

    }

        @Override
        public synchronized String enrolCourse(String studentID, String courseID, String semester) throws RemoteException {
        String result = null;

        Course course = getCourseReference().get(courseID);
        Student student = getEnrolledUsers().get(studentID);

        HashMap<String, String> courseInfo = getOuter_map().get(semester);

        if (!getOuter_map().containsKey(semester))
        {
            result = "There is no course presented in this semester";
            System.out.println(result);

        }



        else if (!courseInfo.containsKey(courseID))
        {
            result = "This course: "+ courseID +" is not defined";
            System.out.println(result);


        }


        else if (!getEnrolledUsers().containsKey(studentID))
        {
            result= "Student "+ studentID +" not found in database";
            System.out.println(result);


        }

        else  if (getCourseReference().containsKey(courseID))

        {

            if((student.getEnrolledCourse_winter().size() +
                    student.getEnrolledCourse_fall().size()+
                    student.getEnrolledCourse_summer().size())
                    >= student.getTotalCapacity())
            {
                result = "Student cannot enroll to more than "+ student.getTotalCapacity() +" totally";
                System.out.println(result);


            }


            else if(semester.toLowerCase().equals("summer"))

            {
                if( student.getEnrolledCourse_summer().size() < student.getCapacity_per_semester())
                {
                    if(course.getEnrolledUsers().size() < course.getCapacity())
                    {
                        if(!course.getEnrolledUsers().containsKey(studentID))
                        {
                            student.addCourse_summer(course);
                            //System.out.println("******" + student.getEnrolledCourse_summer().size());
                            course.addStudentToCourse(student);

                            result = "student " + studentID + " added successfully to " + courseID;
                            System.out.println(result);

                        }
                        else
                        {
                            result= "This student already have this course!";
                            System.out.println(result);

                        }

                    }
                    else
                    {
                        result= "This course: " +courseID + " is full!";
                        System.out.println(result);

                    }


                }

                else {
                    result = "You cannot enroll into more than "+ student.getCapacity_per_semester()+" courses per each semester!";
                    System.out.println(result);


                }

            }
            else if(semester.toLowerCase().equals("fall"))

            {
                if( student.getEnrolledCourse_fall().size() < student.getCapacity_per_semester())
                {
                    if(course.getEnrolledUsers().size() < course.getCapacity())
                    {
                        if(!course.getEnrolledUsers().containsKey(studentID))
                        {
                            student.addCourse_fall(course);
                            System.out.println("******" + student.getEnrolledCourse_fall().size());
                            course.addStudentToCourse(student);

                            result = "student " + studentID + " added successfully to " + courseID;
                            System.out.println(result);

                        }
                        else
                        {
                            result= "This student already have this course!";
                            System.out.println(result);

                        }
                    }
                    else
                    {
                        result = "This course: " +courseID + " is full!";
                        System.out.println(result);

                    }


                    return result;
                }
                else
                {
                    result = "You cannot enroll into more than "+ student.getCapacity_per_semester()+" courses per each semester!";
                    System.out.println(result);


                }


            }
            else if(semester.toLowerCase().equals("winter"))

            {
                if( student.getEnrolledCourse_winter().size() < student.getCapacity_per_semester())
                {
                    if(course.getEnrolledUsers().size() < course.getCapacity())
                    {
                        if(!course.getEnrolledUsers().containsKey(studentID))
                        {
                            student.addCourse_winter(course);
                            System.out.println("******" + student.getEnrolledCourse_winter().size());
                            course.addStudentToCourse(student);

                            result = "student " + studentID + " added successfully to " + courseID;
                            System.out.println(result);

                        }
                        else
                        {
                            result= "This student already have this course!";
                            System.out.println(result);

                        }
                    }
                    else
                    {
                        result= "This course: " +courseID + " is full!";
                        System.out.println(result);

                    }
                }
                else

                    result ="You cannot enroll into more than "+ student.getCapacity_per_semester()+" courses per each semester!";
                System.out.println(result);

            }



        }
        logger.info(result);
        return result;
    }

        @Override
        public String[] getClassSchedule(String studentID) throws RemoteException{

        String result;
        ArrayList<String> returnCourses = new ArrayList<String>();
        String[] _result = new String[returnCourses.size()];

        Student student = getEnrolledUsers().get(studentID);

        if(!getEnrolledUsers().containsKey(studentID))

        {
            result= "Student " + studentID + "is not registered in this system";
            System.out.println(result);

        }

        else
        {


            for (Course course : student.getEnrolledCourse_summer().values())

            {
                returnCourses.add(course.getId());
            }

            for (Course course : student.getEnrolledCourse_fall().values())

            {returnCourses.add(course.getId());
            }

            for (Course course : student.getEnrolledCourse_winter().values())

            {returnCourses.add(course.getId());
            }


        }

        System.out.println("Class Schedule for this student is:");
        logger.info("Class Schedule for this student is:");

        if(returnCourses.size() <1)
        {
            System.out.println("This user doesn't have any course");
            logger.info("This user doesn't have any course");
        }

        else {
            for (String course : returnCourses) {

                System.out.println(course);

                logger.info(course);
            }
        }


        String[] _finalRes;
        _finalRes = returnCourses.toArray(_result);

        return _finalRes;
    }


        @Override
        public String dropCourse(String studentID, String courseID) {
        String result = null;

        if(!getCourseReference().containsKey(courseID))
        {
            result = "Course " + courseID + "not found!";
            System.out.println(result);

        }

        else  if(!getEnrolledUsers().containsKey(studentID))

        {
            result = "Student " + studentID + "is not registered in this system";
            System.out.println(result);

        }

        else if(getCourseReference().containsKey(courseID) && getEnrolledUsers().containsKey(studentID))
        {
            Course course = getCourseReference().get(courseID);
            Student student = getEnrolledUsers().get(studentID);

            if(student.getEnrolledCourse_summer() != null) {
                if (student.getEnrolledCourse_summer().containsKey(courseID))

                {
                    student.getEnrolledCourse_summer().remove(courseID);
                    course.getEnrolledUsers().remove(studentID);
                    result = "Course " + courseID + " successfully removed from summer semester for student " + studentID;
                    System.out.println(result);

                }

            }

            if(student.getEnrolledCourse_fall() != null)
            {
                if (student.getEnrolledCourse_fall().containsKey(courseID)) {
                    student.getEnrolledCourse_fall().remove(courseID);
                    course.getEnrolledUsers().remove(studentID);

                    result = "Course " + courseID + " successfully removed from fall semester for student " + studentID;
                    System.out.println(result);

                }

            }

            if(student.getEnrolledCourse_winter() != null)
            {
                if (student.getEnrolledCourse_winter().containsKey(courseID))
                {
                    course.getEnrolledUsers().remove(studentID);
                    student.getEnrolledCourse_winter().remove(courseID);
                    result = "Course " + courseID + " successfully removed from winter semester for student " + studentID;
                    System.out.println(result);

                }

            }
        }


        logger.info(result);
        return result;
    }

        @Override
        public String addUserToEnrolledUser(String name, String id) throws IOException {
        Student student = new Student(name, id);

        String result= null;
        if(!getEnrolledUsers().containsKey(student.getID()))
        {
            result = "Student " + student.getID() + " added to SOEN / Database!";
            System.out.println(result);
            setEnrolledUsers(student.getID(), student);

        }
        else
        {
            result = "This student already exists!";
            System.out.println(result);

        }

        logger.info(result);
        return result;

    }

        @Override
        public boolean hasCapacity(String courseID) {

        if(courseReference.get(courseID).getCapacity() - courseReference.get(courseID).getEnrolledUsers().size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public String swapCourse(String studentID, String newCourseID, String oldCourseID) {
        return null;
    }


    public HashMap<String, Student> getEnrolledUsers()
    {
        return enrolledUsers;
    }
    public static void setEnrolledUsers(String name , Student stu)
    {
        enrolledUsers.put(name, stu);
    }


    public void setOuter_map(String semester, HashMap<String, String> course )

    {
        outer_map.put(semester, course);
    }

    public HashMap<String, HashMap<String, String>> getOuter_map()
    {
        return outer_map;
    }


    public void setInner_map_summer(String courseId, String description)
    {
        inner_map_summer.put(courseId, description);
    }


    public HashMap<String, String> getInner_map_summer()
    {
        return inner_map_summer;
    }


    public void setInner_map_fall(String courseId, String description)
    {
        inner_map_fall.put(courseId, description);
    }


    public HashMap<String, String> getInner_map_fall()
    {
        return inner_map_fall;
    }


    public void setInner_map_winter(String courseId, String description)
    {
        inner_map_winter.put(courseId, description);
    }


    public HashMap<String, String> getInner_map_winter()
    {
        return inner_map_winter;
    }

    public HashMap<String, Course> getCourseReference()
    {
        return courseReference;
    }

    public void setCourseReference(Course _courseReference)
    {
        courseReference.put(_courseReference.getId(),_courseReference);
    }

}
