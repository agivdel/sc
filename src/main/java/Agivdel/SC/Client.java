package Agivdel.SC;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final DataInputStream IN;
    private final DataOutputStream OUT;
    private final Socket CLIENT_SOCKET;
    private String name = "";

    public Client(Socket CLIENT_SOCKET) throws IOException {
        this.CLIENT_SOCKET = CLIENT_SOCKET;
        this.IN = new DataInputStream(CLIENT_SOCKET.getInputStream());
        this.OUT = new DataOutputStream(CLIENT_SOCKET.getOutputStream());
    }

    public Client(String ip, int port) throws IOException {
        this.CLIENT_SOCKET = new Socket(ip, port);
        this.IN = new DataInputStream(CLIENT_SOCKET.getInputStream());
        this.OUT = new DataOutputStream(CLIENT_SOCKET.getOutputStream());
        System.out.println("Вы подключились к серверу");//поток main клиента
    }

    String readMessage() throws IOException {
        return IN.readUTF();
    }

    void readAndPrint() throws IOException {
            String message = readMessage();
            System.out.println(message);
    }

    void sendMessage(String message) throws IOException {
        OUT.writeUTF(message);
        OUT.flush();
    }

    String readConsole() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    void closeClient() throws IOException {
        IN.close();
        OUT.close();
        CLIENT_SOCKET.close();
    }

    public Socket getCLIENT_SOCKET() {
        return CLIENT_SOCKET;
    }



    public class ReadAndPrint implements Runnable {
        @Override
        public void run() {
            System.out.println("ReadAndPrint is starting!");
            try {
                while (true) {
                    readAndPrint();
//                    String message = readMessage();//чтение с сервера
//                    System.out.println(message);//печать ответа сервера
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
