import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class COMPServer {

     public static int port = 1111;

    public static void main(String arg[]) throws Exception {

        ImplCompServer stub = new ImplCompServer();
        Registry registry = LocateRegistry.createRegistry(port);

        registry.bind("CRS", stub);

        System.out.println("COMP Server is started for client");
    }


    //************* UDP communication *****************//

public static String sendMessage(int serverPort, String msg)

{
    String result= null;
    DatagramSocket aSocket = null;
    try {
        aSocket = new DatagramSocket();
        byte[] message = msg.getBytes();
        InetAddress aHost = InetAddress.getByName("localhost");
        DatagramPacket request = new DatagramPacket(message, msg.length(), aHost, serverPort);
        aSocket.send(request);

			System.out.println("Request message sent from COMP SERVER to server with port number " + serverPort + " is: "
					+ new String(request.getData()));

        byte[] buffer = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

        aSocket.receive(reply);
        if(reply.getData() == null)
        {
            System.out.println("no courses found");
            result = " ";
        }

        else
        {
            System.out.println("Reply received from the server with port number " + serverPort + " is: "
                    + new String(reply.getData()).trim());
            result = new String(reply.getData()).trim();
        }
    } catch (SocketException e) {
        System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("IO: " + e.getMessage());
    } finally {
        if (aSocket != null)
            aSocket.close();
    }

    return result;
}

    public static void receive() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(1212);
            byte[] buffer = new byte[1000];
            System.out.println("Server 1212/COMP Started............");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }


}