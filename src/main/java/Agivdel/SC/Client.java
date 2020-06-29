package Agivdel.SC;

import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket clientSocket;
//    private static BufferedReader in;//вариант 1
    private static DataInputStream in;//вариант 2
    private static DataOutputStream out;

    public static void main(String[] args) throws IOException {
        new Client().run();
//        new ClientBot(10000).runOfBots();
//        new ClientBot(8000).runOfBots();
    }

    private void run() throws IOException {
        try {
            try {
                clientSocket = new Socket("localhost", 4050);
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
                System.out.println("Вы подключились к серверу");

                /**
                 * вариант №1 запуска отдельного потока чтения с сервера: через лямбду
                 */
                Runnable readMessage = () -> {
                    try {
                        while (true) {
//                            String wordFromServer = in.readLine();//чтение с сервера
                            String wordFromServer = in.readUTF();//чтение с сервера
                            System.out.println(wordFromServer);//печать ответа сервера
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                new Thread(readMessage).start();

                /**
                 * вариант №2 запуска отдельного потока чтения с сервера: через класс, реализующий Runnable
                 */
//                new Thread(new ReadMessage()).start();


                while (true) {//чтение с консоли и отправка сообщений на сервре - в потоке main
                    String messageFromClient = reader.readLine();
                    if ("exit".equalsIgnoreCase(messageFromClient)) {
                        break;
                    }
                    out.writeUTF(messageFromClient);
                    out.flush();
                }
            } finally {
                in.close();//если случился break, сначала закрываем каналы
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
                    String wordFromServer = in.readLine();//чтение с сервера
//                    String wordFromServer = in.readUTF();//чтение с сервера
                    System.out.println(wordFromServer);//печать ответа сервера
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
