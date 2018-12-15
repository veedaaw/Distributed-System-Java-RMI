import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SOENServer
{

    public static int port = 1313;

    private static Registry SOENRegistry;
    private static InterfaceRMI SOENObj;

    public static void main(String arg[]) throws Exception {



        Runnable task1 = () -> {
            try {
                StartServer(arg);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        };

        Runnable task2 = () -> {
            try {
                receive();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        thread1.start();
        thread2.start();

        }


    public static void StartServer (String args[]) throws RemoteException, NotBoundException {



        try {

            ImplSOENServer stub = new ImplSOENServer();
            Registry registry = LocateRegistry.createRegistry(port);

            registry.bind("CRS", stub);

            System.out.println("SOEN Server is started for client");

            SOENRegistry = LocateRegistry.getRegistry(1313);
            SOENObj = (InterfaceRMI) SOENRegistry.lookup("CRS");


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        //******************UDP**************************//
    public static void receive() throws RemoteException, NotBoundException {




        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(1313);
            byte[] buffer = new byte[1000];// to stored the received data from
            // the client.
            System.out.println("Server 1313 Started....");
            while (true) {// non-terminating loop as the server is always in
                // listening mode.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // Server waits for the request to
                // come------------------------------------------------------------------
                aSocket.receive(request);// request received

                byte[] byt = request.getData();
                char[] message = new char[byt.length];

                for (int i = 0; i < byt.length; i++) {
                    message[i] = (char) byt[i];
                }
                String str = String.valueOf(message);
                System.out.println(str.trim());

                String data = " ";
                if (str.trim().toLowerCase().equals("fall")) {
                    if( SOENObj.CourseAvailability("fall").length>0) {
                        for (int i = 0; i < SOENObj.CourseAvailability("fall").length; i++) {
                            data += (SOENObj.CourseAvailability("fall")[i])+ " ";
                        }

                    }


                }
                else if (str.trim().toLowerCase().equals("winter")) {
                    if( SOENObj.CourseAvailability("winter").length>0) {
                        for (int i = 0; i < SOENObj.CourseAvailability("winter").length; i++) {
                            data += (SOENObj.CourseAvailability("winter")[i]) + " ";
                        }
                    }

                }
                else if (str.trim().toLowerCase().equals("summer")) {
                    if( SOENObj.CourseAvailability("summer").length>0)
                    {
                        for (int i = 0; i < SOENObj.CourseAvailability("summer").length; i++) {
                            data += (SOENObj.CourseAvailability("summer")[i])+ " ";
                        }
                    }

                }
                else {
                    data = "-0-";
                }


                DatagramPacket reply = new DatagramPacket(data.getBytes(), data.length(), request.getAddress(),
                        request.getPort());// reply packet ready
                aSocket.send(reply);// reply sent
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

}