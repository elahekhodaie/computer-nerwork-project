import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);

		String serverName = "0.0.0.0";
		int port = 8080;
		try {
		 System.out.println("Connecting to " + serverName + " on port " + port);
		 Socket client = new Socket(serverName, port);
		 
		 System.out.println("Just connected to server");

		 InputStream inFromServer = client.getInputStream();
		 DataInputStream in = new DataInputStream(inFromServer);

		 OutputStream outToServer = client.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);

		 out.writeUTF("salam");
		 
		 client.close();
		} catch (IOException e) {
		 e.printStackTrace();
		}
	}
}