import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StudentClient
{
    private static Registry COMPRegistry;
    private static InterfaceRMI COMPObj;

    private static Registry SOENRegistry;
    private static InterfaceRMI SOENObj;

    private static Registry INSERegistry;
    private static InterfaceRMI INSEObj;

    public static void main(String[] args) {

        try {

            COMPRegistry = LocateRegistry.getRegistry(COMPServer.port);
            COMPObj = (InterfaceRMI) COMPRegistry.lookup("CRS");

            SOENRegistry = LocateRegistry.getRegistry(SOENServer.port);
            SOENObj = (InterfaceRMI) SOENRegistry.lookup("CRS");

            INSERegistry = LocateRegistry.getRegistry(INSEServer.port);
            INSEObj = (InterfaceRMI) INSERegistry.lookup("CRS");


            System.out.println("-----------------------------------");

            //************** multithreading test case*************//

            Runnable task1 = () ->
            {

                try {
                    swapCourse("COMPS1234", "COMP1111", "COMP2222", "fall" );
                } catch (ExecutionException e) {

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            };
            Runnable task2 = () -> {

                try {
                    System.out.println(COMPObj.enrolCourse("COMPS4567", "COMP2222", "fall"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            };


            Thread thread1 = new Thread(task1);
            Thread thread2 = new Thread(task2);


            thread1.start();
            thread2.start();

            //***************** CONSOLE *************//

            Scanner c = new Scanner(System.in);
            System.out.println("Welcome to DCSR system!");
            for (;;) {
                System.out.println("please enter your student id: ");
                String id = c.nextLine();

                if(id.startsWith("COMPS") ||id.startsWith("SOENS") || id.startsWith("INSES") )
                {
                    for(;;) {
                        System.out.println("-----------------------------------");
                        System.out.println("What do you want to do? Please enter a number:");

                        System.out.println("1- Enroll in a course");
                        System.out.println("2- Drop a course");
                        System.out.println("3- Get class schedule");
                        System.out.println("4- Swap courses");
                        System.out.println("-----------------------------------");
                        String number = c.nextLine();
                        int option = Integer.parseInt(number);
                        switch (option) {


                            case 1:
                                System.out.println("Enter student id/course id/semester");
                                String enroll = c.nextLine();
                                enrolCourse(enroll.split(" ")[0], enroll.split(" ")[1], enroll.split(" ")[2]);
                                continue;
                            case 2:
                                System.out.println("Enter student id/course id");
                                String drop = c.nextLine();
                                dropCourse(drop.split(" ")[0], drop.split(" ")[1]);
                                continue;

                            case 3:
                                System.out.println("Enter student id");
                                String schedule= c.nextLine();
                                getClassSchedule(schedule);
                                continue;
                            case 4:
                                System.out.println("Enter student id/current course/ old course/ semester");
                                String swap= c.nextLine();
                                swapCourse(swap.split(" ")[0], swap.split(" ")[1],
                                        swap.split(" ")[2], swap.split(" ")[3]);

                                continue;
                            default:
                                System.out.println("Invalid input");
                                continue;

                        }
                    }
                }

                else
                {
                    System.out.println("Your id is not correct");
                }

                System.out.println("-----------------------------------");
            }



        } catch (Exception e) {
            System.out.println(" Client exception: " + e);
            e.printStackTrace();
        }
    }


        //************ student functions ********************//

    public static void enrolCourse (String studentID, String courseID, String semester) throws Exception {

        String res= null;
        if(courseID.startsWith("SOEN"))
        {

            res = SOENObj.enrolCourse(studentID, courseID, semester);
        }

        if(courseID.startsWith("COMP"))
        {
            res = COMPObj.enrolCourse(studentID, courseID, semester);
        }

        if(courseID.startsWith("INSE"))
        {
            res = INSEObj.enrolCourse(studentID, courseID, semester);
        }


        System.out.println(res);


    }

    public static String dropCourse(String studentID, String courseID)  throws RemoteException
    {
        String res= null;
        if(courseID.startsWith("SOEN"))
        {
            res = SOENObj.dropCourse(studentID, courseID);
        }

        else if(courseID.startsWith("COMP"))
        {
            res = COMPObj.dropCourse(studentID, courseID);
        }

        else if(courseID.startsWith("INSE"))
        {
            res = INSEObj.dropCourse(studentID, courseID);
        }

        System.out.println(res);
        return res;
    }

    public static void getClassSchedule(String studentID) throws RemoteException
    {
        ArrayList<String> res = new ArrayList<>();


        for(int i=0; i< SOENObj.getClassSchedule(studentID).length; i++ )
        {
            res.add(SOENObj.getClassSchedule(studentID)[i]);

        }

        for(int i=0; i< COMPObj.getClassSchedule(studentID).length; i++ )
        {
            res.add(COMPObj.getClassSchedule(studentID)[i]);

        }

        for(int i=0; i< INSEObj.getClassSchedule(studentID).length; i++ )
        {
            res.add(INSEObj.getClassSchedule(studentID)[i]);

        }



        System.out.println("Class Schedule for this student is:");
        for(String course:res )
        {
            System.out.println(course);
        }


    }



    public static void swapCourse (String student_id, String oldCourse_id, String newCourse_id, String semester) throws ExecutionException, InterruptedException, RemoteException {


        String result= "";
        String data= " ";

        //********************COMP***********************//

        if(oldCourse_id.startsWith("COMP"))

        {
            if (newCourse_id.startsWith("COMP"))
            {

                String[] res = COMPObj.getClassSchedule(student_id);
                String[] res2 = COMPObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (COMPObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + COMPObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }

            if (newCourse_id.startsWith("SOEN"))
            {
                java.util.concurrent.Callable<String> callable = () -> {

                    return  COMPServer.sendMessage(1313, semester);
                };

                Runnable task1 = () -> {
                    COMPServer.receive();
                };


                Thread thread1 = new Thread(task1);
                thread1.start();

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<String> future = executorService.submit(callable);
                String courseList = future.get();


                String[] old = COMPObj.getClassSchedule(student_id);

                String [] splitted = courseList.split(" ");
                for(String s : splitted) {
                    if (s.equals(newCourse_id) && Arrays.stream(old).anyMatch(oldCourse_id::equals))

                    {
                        if (s.indexOf(newCourse_id) + 1 > 0) // capacity of new course
                        {
                            result += dropCourse(student_id, oldCourse_id) + " and //";
                            result += " " + SOENObj.enrolCourse(student_id, newCourse_id, semester);

                        } else {
                            System.out.println("Unfortunately the course you want to swap with is full");
                        }


                    }


                }

            }



            if (newCourse_id.startsWith("INSE"))
            {
                java.util.concurrent.Callable<String> callable = () -> {

                    return  COMPServer.sendMessage(1414, semester);
                };

                Runnable task1 = () -> {
                    COMPServer.receive();
                };


                Thread thread1 = new Thread(task1);
                thread1.start();

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<String> future = executorService.submit(callable);
                String courseList = future.get();


                String[] old = COMPObj.getClassSchedule(student_id);

                String [] splitted = courseList.split(" ");
                for(String s : splitted) {
                    if (s.equals(newCourse_id) && Arrays.stream(old).anyMatch(oldCourse_id::equals))

                    {
                        if (s.indexOf(newCourse_id) + 1 > 0) // capacity of new course
                        {
                            result += dropCourse(student_id, oldCourse_id) + " and //";
                            result += " " + INSEObj.enrolCourse(student_id, newCourse_id, semester);

                        } else {
                            System.out.println("Unfortunately the course you want to swap with is full");
                        }


                    }


                }

            }


        }

        //**********************SOEN*******************************//

        else  if(oldCourse_id.startsWith("SOEN"))
        {
            if (newCourse_id.startsWith("COMP"))
            {

                String[] res = SOENObj.getClassSchedule(student_id);
                String[] res2 = COMPObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (COMPObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + COMPObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }

            else if (newCourse_id.startsWith("SOEN"))
            {

                String[] res = SOENObj.getClassSchedule(student_id);
                String[] res2 = SOENObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (SOENObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + SOENObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }

            else if (newCourse_id.startsWith("INSE"))
            {

                String[] res = SOENObj.getClassSchedule(student_id);
                String[] res2 = INSEObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (INSEObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + INSEObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }
        }


        //*************************************INSE****************************//

        else  if(oldCourse_id.startsWith("INSE"))
        {
            if (newCourse_id.startsWith("COMP"))
            {

                String[] res = INSEObj.getClassSchedule(student_id);
                String[] res2 = COMPObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (COMPObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + COMPObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }

            else if (newCourse_id.startsWith("SOEN"))
            {

                String[] res = INSEObj.getClassSchedule(student_id);
                String[] res2 = SOENObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (SOENObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + SOENObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }

            else if (newCourse_id.startsWith("INSE"))
            {

                String[] res = INSEObj.getClassSchedule(student_id);
                String[] res2 = INSEObj.CourseAvailability(semester);
                if (Arrays.stream(res).anyMatch(oldCourse_id::equals) && Arrays.stream(res2).anyMatch(newCourse_id::equals)) {

                    if (INSEObj.hasCapacity(newCourse_id)) {


                        result += dropCourse(student_id, oldCourse_id) + " and //";
                        result += " " + INSEObj.enrolCourse(student_id, newCourse_id, semester);

                    } else {
                        System.out.println("Unfortunately the course you want to swap with is full");
                    }


                } else {
                    result = "either you don't have this course or the new course is not defined";
                    System.out.println(result);
                }

            }
        }



        System.out.println(result);



    }



}



