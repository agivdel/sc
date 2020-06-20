package Agivdel.SC.singleThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSingleThread {

    private static ServerSocket server;
    //    private static BufferedReader in;
    private static DataInputStream in;
//    private static BufferedWriter out;
    private static DataOutputStream out;


    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(4050);
                System.out.println("Сервер открыт");
                try (Socket clientSocket = server.accept()) {//на стопе, пока не подлключится клиент
                    System.out.println("Подключение установлено");
//                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    in = new DataInputStream(clientSocket.getInputStream());
//                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                    out = new DataOutputStream(clientSocket.getOutputStream());

//                    while (true) {
                    while (!clientSocket.isClosed()) {//другой вариант цикла - пока клиент не отключился
//                        word = in.readLine();//вариант для Buffered
                        String word = in.readUTF();//вариант для Data
                        if ("exit".equalsIgnoreCase(word)) {
                            break;
                        }
                        System.out.print(word);
//                        out.write(word + ", бля" + "\n");//вариант для Buffered, "\n" нужно прописывать
                        out.writeUTF("echoing: " + word);//вариант для Data, "\n" ставится автоматом
                        out.flush();
                    }
                } finally {
                    System.out.println("Клиент отключился");
                    in.close();//сначала закрываем каналы
                    out.close();
                    //потом закрываем сокет на стороне сервера
                }
            } finally {
                server.close();//закрываем сокет сервера (при многопоточности это не нужно)
                System.out.println("Сервер закрыт");
            }
        } catch (IOException e) {
//            System.err.println(e);
            e.printStackTrace();
        }
    }
}
