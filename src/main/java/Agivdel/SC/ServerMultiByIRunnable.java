package Agivdel.SC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * вариант №1 - через класс с реализацией интерфейса Runnable и объект класса Thread
 */
public class ServerMultiByIRunnable {
    private static ServerSocket server;
    private static final Map<NewClient, String> clientMap = Collections.synchronizedMap(new HashMap<>());//синхронизирующая (потокобезопасная) оболочка
    private static final BlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(100);
    private static final ArrayList<String> story = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Runnable sender = () -> {
            while (true) {
                try {
                    String message = QUEUE.take();//в бесконечном цикле берем первый элемент очереди
                    clientMap.forEach((client, clientName) -> { //перебираем все отображение
                        System.out.println(Thread.currentThread() + " sender to " + clientName);
                        try {
                            client.sendMessage(message);//каждому клиенту отправляем сообщение из очереди
                            System.out.println(clientMap.get(client) + ": " + message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(sender).start();//стартуем поток отправки сообщений

        try {
            try {
                server = new ServerSocket(4050);
                System.out.println("Сервер открыт");
                while (true) {
                    Socket clientSocket = server.accept();
                    NewClient newClient = new NewClient(clientSocket);//создаем объект на основе нового сокета
                    String name = nameRequest(newClient);//запрашиваем имя
                    clientMap.put(newClient, name);//создаем новую запись в отображении
                    new Thread(newClient).start();//запускаем метод run() нового потока
                }
            } finally {
                server.close();
                System.out.println("Сервер закрыт");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String nameRequest(NewClient newClient) throws IOException {
        newClient.sendMessage("Введите ваше имя");
        String name = newClient.readMessage();
        //вариант №1 - с коллекцией значений
        //если бы имя было ключом, можн было бы применить метод containsKey(name)
        Collection <String> clientNames = clientMap.values();//из отображения берем коллекцию значений (строк с именами)
        for (String clientName : clientNames) {
            if (clientName.equals(name)) {
                try {
                    newClient.sendMessage("Это имя уже занято, выберите другое");
                    nameRequest(newClient);//если имя занято, запрашиваем вновь
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //вариант №2 - с отображением пар ключ-значение
//        clientMap.forEach((client, clientName) -> {
//            if (clientName.equals(name)) {
//                try {
//                    newClient.sendMessage("Это имя уже занято, выберите другое");
//                    nameRequest(newClient);//если имя занято, запрашиваем вновь
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        //вариант №3 - тоже с отображением пар ключ-значение, но подлиннее:
//            for (Map.Entry<NewClient, String> entry : clientMap.entrySet()) {
//                String k = entry.getKey();
//                NewClient v = entry.getValue();
//                if (k.equals(name)) {
//                    try {
//                        sendMessage("Это имя уже занято, выберите другое");
//                        nameRequest();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        newClient.sendMessage("Приветствую, " + name + "!");
        return name; //если совпадений имен нет, возвращаем введенное имя
    }

    private static void sendStory() {
    }

    private static void listClient() {
        clientMap.forEach((k, v) -> {
            System.out.println("socket: " + k + ", name: " + v);
        });
    }


    static class NewClient implements Runnable {
        private final DataInputStream IN;
        private final DataOutputStream OUT;
        private final Socket clientSocket;

        public NewClient(Socket clientSocket) throws IOException {
            this.IN = new DataInputStream(clientSocket.getInputStream());
            this.OUT = new DataOutputStream(clientSocket.getOutputStream());
            this.clientSocket = clientSocket;
//            sendStory();
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread());
                QUEUE.put(clientMap.get(this) + " подключился.");
                while (true) {
                    String message = readMessage();
                    QUEUE.put(clientMap.get(this) + ": " + message);
                }
            } catch (IOException | InterruptedException e) {
                try {
                    closeClient();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        private void sendMessage(String message) throws IOException {
            OUT.writeUTF(message); //вариант для Data, "\n" ставится автоматом
            OUT.flush();
        }

        private String readMessage() throws IOException {
            String message = IN.readUTF();
            if (message.equalsIgnoreCase("exit")) {
                closeClient();
            }
            if (message.equalsIgnoreCase("list")) {
                listClient();
            }
            return message;
        }

        private void closeClient() throws IOException {
            clientSocket.close();
            IN.close();
            OUT.close();
            System.out.println(Thread.currentThread() + clientMap.get(this) + " отключился.");
            clientMap.remove(this);//удаление из списка клиентов. ключ - объект класса NewClient
        }
    }
}

