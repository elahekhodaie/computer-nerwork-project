
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Random;



public class Server extends Thread {
    private Random random = new Random();
    private static Server instance = new Server();
    private int port;
    private DatagramSocket datagramSocket;

    private InetAddress clientInetAddress;
    private int clientPort;

    private byte[] data = {1, 2, 3, 4, 5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private ConnectionState state = ConnectionState.CLOSED;


    ServerWindow window;

    private Server() {
        port = SERVER_PORT;
        clientPort = CLIENT_PORT;
        try {
            clientInetAddress = InetAddress.getLocalHost();
            datagramSocket = new DatagramSocket(port);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        readImage();
    }

    private void readImage(){
        try {
            BufferedImage bImage = null;
            bImage = ImageIO.read(new File(SERVER_FILE_PATH));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos );
            data = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Server getInstance() {
        return instance;
    }

    public synchronized void send(TCPPacket tcpPacket, boolean deterministic) {
        byte[] buffer = tcpPacket.getBuffer();
        if (!deterministic){
            double r = random.nextDouble();
            if (r <= CORRUPT_PROB) {
                System.out.println("Packet " + tcpPacket.getSeqNum() + " has corrupted.");
                buffer = TCPPacketManager.getCorruptBuffer(tcpPacket.getBuffer(), tcpPacket.getBufferLength());
            }
            r = random.nextDouble();
            if (r <= DROP_PROB){
                System.out.println("Packet " + tcpPacket.getSeqNum() + " has dropped.");
                return;
            }
        }
//        System.out.println("Packet " + tcpPacket.getSeqNum() + " has been sent." + " ACK= " +
//                TCPPacketManager.getAckFlag(buffer) + " FIN= " + TCPPacketManager.getFinFlag(buffer));
        try {
            datagramSocket.send(new DatagramPacket(buffer, tcpPacket.getBufferLength(),
                    clientInetAddress, clientPort));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //listener
    @Override
    public void run() {
        while (true){
            byte[] buffer = new byte[UDP_MSS];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, UDP_MSS);
            try {
                datagramSocket.receive(receivedPacket);
                checkPacket(buffer, receivedPacket.getLength() - HEADER_LENGTH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void handshake(){
        window = new ServerWindow(this);
        TCPPacket firstPacket = new TCPPacket(window, port, clientPort,
                INITIAL_SEQ_NUM, INITIAl_ACK_NUM,
                true, true, false, data,0);
        window.getWindow().add(firstPacket);
        send(firstPacket, true);
        state = ConnectionState.SYN_SENT;
    }

    private void sendFINAck(byte[] buffer){
        send(new TCPPacket(window, port, clientPort,
                data.length + 1, SERVER_FIN_ACK_NUM,
                true, false, true, data,0), true);
        window.ackPackets(TCPPacketManager.getAckNum(buffer));
    }

    private void checkPacket(byte[] buffer, int dataLength){
        if (TCPPacketManager.calCheckSum(buffer, dataLength)
                == TCPPacketManager.getCheckSum(buffer)){
            if (TCPPacketManager.getSynFlag(buffer) && state == ConnectionState.CLOSED){
                handshake();
            }
            else if (TCPPacketManager.getFinFlag(buffer) && state == ConnectionState.FIN_SENT){
                sendFINAck(buffer);
                System.out.println("Connection Closed.");
                state = ConnectionState.CLOSED;
            }
            else if (TCPPacketManager.getAckFlag(buffer)) {
                if (state == ConnectionState.SYN_SENT){
                    System.out.println("Connection Established.");
                    state = ConnectionState.ESTABLISHED;
                }
                System.out.println("Packet " + TCPPacketManager.getAckNum(buffer) + " acked.");
                window.ackPackets(TCPPacketManager.getAckNum(buffer));
//                window.getLog();
            }

        }
    }

    public  void closeConnection(){
        if (state == ConnectionState.ESTABLISHED){
            TCPPacket FINPacket = new TCPPacket(window, port, clientPort,
                    data.length + 1, INITIAl_ACK_NUM,
                    false, false, true, data,0);
            send(FINPacket, true);
            state = ConnectionState.FIN_SENT;
        }
    }
    public int getPort() {
        return port;
    }

    public int getClientPort() {
        return clientPort;
    }

    public byte[] getData() {
        return data;
    }

    public int getDataLength() {
        return data.length;
    }

    public ConnectionState getConnectionState() {
        return state;
    }
}