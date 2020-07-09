package Agivdel.SC;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * вариант №1 - через класс с реализацией интерфейса Runnable и объект класса Thread
 */
public class ServerMultiByIRunnable {
    private static ServerSocket server;
    private static final Map<String, Client> clients = Collections.synchronizedMap(new HashMap<>());//синхронизирующая (потокобезопасная) оболочка
    private static final BlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(100);
    private static final List<String> story = new ArrayList<>();

    public static void main(String[] args) {
        new Thread(new Sender()).start();//стартуем поток отправки сообщений
        try {
            try {
                server = new ServerSocket(4050);
                System.out.println("Сервер открыт");
                while (true) {
                    Socket clientSocket = server.accept();
                    new Thread(new Client(clientSocket)).start();//создаем объект и запускаем run() нового потока
                }
            } finally {
                server.close();
                System.out.println("Сервер закрыт");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String nameRequest(Client client) throws IOException, InterruptedException {
        String name;
        client.sendMessage("Введите ваше имя");
        while (true) {
            name = client.readMessage();
            if (!clients.containsKey(name)) {
                break;
            }
            client.sendMessage("Это имя уже занято, выберите другое");
        }
        client.sendMessage("Приветствую, " + name + "! (Для закрытия соединения введите exit)");
        return name; //если совпадений имен нет, возвращаем введенное имя
    }

    private static void sendStory(Client client) throws IOException {
        if (story.size() > 0) {
            client.sendMessage("Последние сообщения:");
            for (String s : story) {
                client.sendMessage(s);
            }
            client.sendMessage("Конец истории.");
        }
    }


    static class Sender implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String message = QUEUE.take();//в бесконечном цикле берем первый элемент очереди
                    System.out.println(message);
                    story.add(message);
                    clients.forEach((clientName, client) -> { //перебираем все отображение
                        try {
                            client.sendMessage(message);//каждому клиенту отправляем сообщение из очереди
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class Client implements Runnable {
        private final DataInputStream IN;
        private final DataOutputStream OUT;
        private final Socket clientSocket;
        private String name;

        public Client(Socket clientSocket) throws IOException {
            this.IN = new DataInputStream(clientSocket.getInputStream());
            this.OUT = new DataOutputStream(clientSocket.getOutputStream());
            this.clientSocket = clientSocket;
            this.name = "";
            sendStory(this);//отсюда отправка истории работает без ошибок. работает поток main
        }

        @Override
        public void run() {
            try {
                name = nameRequest(this);//запрашиваем имя
                clients.put(name, this);//создаем новую запись в отображении
                QUEUE.put(preMessageHMS(name) + "подключился.");
//                sendStory(this);//отсюда отправка истории работает с ошибками
                while (true) {
                    String message = readMessage();
                    QUEUE.put( preMessageHMS(name) + message);
                }
            } catch (IOException | InterruptedException e) {
                try {
                    closeClient();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        private void sendMessage(String message) throws IOException {
            OUT.writeUTF(message); //вариант для Data, "\n" ставится автоматом
            OUT.flush();
        }

        private String readMessage() throws IOException, InterruptedException {
            String message = IN.readUTF();
            if (message.equalsIgnoreCase("exit")) {
                closeClient();
            }
            if (message.equalsIgnoreCase("size")) {
                sendMessage("story size: " + story.size());
            }
            if (message.equalsIgnoreCase("story")) {
                sendStory(this);
            }
            return message;
        }

        private void closeClient() throws IOException, InterruptedException {
            clientSocket.close();
            IN.close();
            OUT.close();
            QUEUE.put(preMessageHMS(name) + "отключился.");
            clients.remove(name);//удаление из списка клиентов
        }

        private String preMessageHMS (String name) {
            return new SimpleDateFormat("HH:mm:ss, ").format(new Date()) + name + ":";
        }
    }

}

