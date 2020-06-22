package Agivdel.SC.singleThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * вариант №1 - через класс с реализацией интерфейса Runnable и объект класса Thread
 */
public class ServerMultiByIRunnable {
    private static ServerSocket server;
    private static final List<NewClient> clientList = new ArrayList<>();
    private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws IOException {

        Runnable sender = () -> {
            try {
                while (true) {
                    String message = queue.take();
                    for (NewClient client : clientList) {
                        System.out.println(Thread.currentThread() + " sender to " + (clientList.indexOf(client) + 1));
                        client.sendMessage(message);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(sender).start();

        try {
            try {
                server = new ServerSocket(4050);
                System.out.println("Сервер открыт");
                while (true) {
                    Socket client = server.accept();
                    NewClient newClient = new NewClient(client);
                    clientList.add(newClient);
                    new Thread(newClient).start();
                }
            } finally {
                server.close();
                System.out.println("Сервер закрыт");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public static void sendToAllClients(String message) throws IOException {
//        for (NewClient client : clientList) {
//            System.out.println(Thread.currentThread());
//            client.sendMessage(message);
//            System.out.println("Сообщение (" + message + ") отослано клиенту " + clientList.indexOf(client));
//        }
//    }



    static class NewClient implements Runnable {
        private final DataInputStream IN;
        private final DataOutputStream OUT;

        public NewClient(Socket newClient) throws IOException {
            this.IN = new DataInputStream(newClient.getInputStream());
            this.OUT = new DataOutputStream(newClient.getOutputStream());
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread());
                System.out.println("Подключился клиент №" + (clientList.indexOf(this) + 1));
                while (true) {
                    String message = IN.readUTF();
                    if (message.equalsIgnoreCase("exit")) closeClient();
                    queue.put((clientList.indexOf(this) + 1) + ": " + message);
                    System.out.println((clientList.indexOf(this) + 1) + ": " + message);
//                    sendToAllClients((clientList.indexOf(this) + 1) + ": " + message);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void closeClient() throws IOException {
            IN.close();
            OUT.close();
            System.out.println("Клиент №" + (clientList.indexOf(this) + 1) + " отключился");
        }

        private void sendMessage(String message) throws IOException {
            OUT.writeUTF(message); //вариант для Data, "\n" ставится автоматом
            OUT.flush();
        }
    }
}

