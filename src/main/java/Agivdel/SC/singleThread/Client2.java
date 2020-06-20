package Agivdel.SC.singleThread;

import java.io.*;
import java.net.Socket;

public class Client2 {

    private static Socket clientSocket;
//    private static BufferedReader in;//вариант 1
    private static DataInputStream in;//вариант 2
//    private static BufferedWriter out;
    private static DataOutputStream out;

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", 4050);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//чтение с сокета
                in = new DataInputStream(clientSocket.getInputStream());
//                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));//отправка на сокет
                out = new DataOutputStream(clientSocket.getOutputStream());
                System.out.println("Вы подключились к серверу");
                System.out.println("(Для закрытия соединения введите exit)");
                while (!clientSocket.isOutputShutdown()) {//работаем, если канал в порядке
                    System.out.print(": ");
                    String messageFromClient = reader.readLine();
                    if ("exit".equalsIgnoreCase(messageFromClient)) {
                        break;
                    }
                    out.writeUTF(messageFromClient);
                    out.flush();
                    String wordFromServer = in.readUTF();
                    System.out.println(wordFromServer);//печать ответа сервера
                }
            } finally {
                in.close();//сначала азкрываем каналы
                out.close();
                clientSocket.close();//потом закрываем сокет
                System.out.println("Вы отключились");
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
