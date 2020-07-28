import java.net.*;
import java.io.*;
import java.util.*; 

public class Server extends Thread{
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(100000);
    }

    public void run() {
      Scanner scanner = new Scanner(System.in);

      while(true) {
         try {
            System.out.println("Waiting for client on port " + 
               serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();

            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            InputStreamReader in = new InputStreamReader(server.getInputStream());
            BufferedReader bin = new BufferedReader(in);

            String str = bin.readLine();
            String request = "";
            while (str != null) {
                request += str + "\n";
                str = bin.readLine();
            }
            request = request.substring(0, request.length() - 1);
            System.out.println(request);
            RequestPacket rp = RequestPacket.fromString(request);
            //TODO: handle errors
            //TODO: handle responses

         } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
            break;
         } catch (IOException e) {
            e.printStackTrace();
            break;
         }
      }
   }

    public static void main(String args[]) {
        int port = 8080;
        try {
            Thread t = new Server(port);
            t.start();
            System.out.println("Server is successfully run");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}