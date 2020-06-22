package Agivdel.SC.singleThread;

import java.io.*;
import java.net.Socket;

public class Client1 {

    private static Socket clientSocket;
    //    private static BufferedReader in;//вариант 1
    private static DataInputStream in;//вариант 2
    private static DataOutputStream out;

    public static void main(String[] args) throws IOException {

        try {
            clientSocket = new Socket("localhost", 4050);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
            System.out.println("Вы подключились к серверу");
            System.out.println("(Для закрытия соединения введите exit)");
            new Thread(new ReadMessage()).start();//чтение с сервера будет крутиться в отдельном потоке
            while (true) {//чтение с консоли и отправка сообщений на сервре - в потоке main
                System.out.print(": ");
                String messageFromClient = reader.readLine();
                if ("exit".equalsIgnoreCase(messageFromClient)) {
                    break;
                }
                out.writeUTF(messageFromClient);
                out.flush();
            }
        } catch (IOException e) {
            closeSocket();
        }
    }

    public static void closeSocket() throws IOException {
        try {
            if (!clientSocket.isClosed()) {
                in.close();//сначала закрываем каналы
                out.close();
                clientSocket.close();//потом закрываем сокет
                System.out.println("Вы отключились");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ReadMessage implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    String wordFromServer = in.readUTF();//чтение с сервера
                    System.out.println(wordFromServer);//печать ответа сервера
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
