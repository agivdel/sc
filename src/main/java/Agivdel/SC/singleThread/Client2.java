package Agivdel.SC.singleThread;

import java.io.*;
import java.net.Socket;

public class Client2 {

    private static Socket clientSocket;
    private static DataInputStream in;//вариант 2
    private static DataOutputStream out;

    public static void main(String[] args) throws IOException {

        try {
            clientSocket = new Socket("localhost", 4050);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Вы подключились к серверу");
            System.out.println("(Для закрытия соединения введите exit)");

            Runnable readMessage = () -> {
                try {
                    while (true) {
                        String wordFromServer = in.readUTF();//чтение с сервера
                        System.out.println(wordFromServer);//печать ответа сервера
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            new Thread(readMessage).start();

            Runnable writeMessage = () -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
                    while (true) {
                        System.out.print(": ");
                        String messageFromClient = reader.readLine();
                        if ("exit".equalsIgnoreCase(messageFromClient)) {
                            break;
                        }
                        out.writeUTF(messageFromClient);
                        out.flush();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    try {
                        closeSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(writeMessage).start();

        } catch (IOException e) {
            closeSocket();
        }
    }


    public static void closeSocket() throws IOException {
        try {
            if (!clientSocket.isClosed())
                in.close();//сначала закрываем каналы
            out.close();
            clientSocket.close();//потом закрываем сокет
            System.out.println("Вы отключились");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


