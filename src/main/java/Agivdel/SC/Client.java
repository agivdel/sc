package Agivdel.SC;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Client {

    private Socket clientSocket;
    private DataInputStream IN;
    private DataOutputStream OUT;
    private static final String IP = "localhost";
    private static final int PORT = 4050;

    public Client() throws IOException {
        clientSocket = new Socket (IP, PORT);
        IN = new DataInputStream(clientSocket.getInputStream());
        OUT = new DataOutputStream(clientSocket.getOutputStream());
        System.out.println("Вы подключились к серверу");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Client().botTalk(4);
        new Client().botTalk(3);
//        Client client = new Client();
//        client.talk();
        new Client().talk();
    }

    private void talk() throws IOException, InterruptedException {//вариант с ручным управлением диалогом
        Thread serverReader = new Thread(new ReadMessage());
        serverReader.start();
//        new Thread(new ReadMessage()).start();//чтение с сервера в отдельном потоке
        while (!clientSocket.isOutputShutdown()) {
            String message = readConsole();//чтение с консоли и отправка на сервре - в потоке main
            sendMessage(message);
        }

    }

    private void botTalk(int numberOfMessages) throws IOException, InterruptedException {//вариант с диалогом бота
        ClientBot bot = new ClientBot(this);//вначале создаем объект бота
        if (bot.requestBotName()) {
            for (int i = 0; i <= numberOfMessages; i++) {
                String message = readServer();
                System.out.println(message);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMessage(bot.messageConstructor());
            }
        }
    }

    private String readServer() throws IOException {
        return IN.readUTF();
    }

    private String readConsole() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String messageFromConsole = scanner.nextLine();
        if ("exit".equalsIgnoreCase(messageFromConsole)) {
            closeClient();
        }
        return messageFromConsole;
    }

    private void sendMessage(String message) throws IOException {
        OUT.writeUTF(message);
        OUT.flush();
    }

    private void closeClient() throws IOException {
        IN.close();//если случился break, сначала закрываем каналы
        OUT.close();
        clientSocket.close();//потом закрываем сокет
        System.out.println("Вы отключились");

    }

    /**
     * для клиентов с ручным управленеим чтение сообщений с сервера в отдельном потоке
     */
    class ReadMessage implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = IN.readUTF();//чтение с сервера
                    System.out.println(message);//печать ответа сервера
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static class ClientBot {
        private String name;
        private final Client client;

        private static final String pathname1 = "C:/Users/agivd/IdeaProjects/SC/src/main/resources/the first part of message.txt";
        private static final String pathname2 = "C:/Users/agivd/IdeaProjects/SC/src/main/resources/the second part of message.txt";
        private static final String pathname3 = "C:/Users/agivd/IdeaProjects/SC/src/main/resources/bot names.txt";

        private static List<String> FIRST;
        private static List<String> SECOND;
        private static List<String> NAMES;

        static {
            try {
                FIRST = Files.readAllLines(Paths.get(pathname1), StandardCharsets.UTF_8);
                SECOND = Files.readAllLines(Paths.get(pathname2), StandardCharsets.UTF_8);
                NAMES = Files.readAllLines(Paths.get(pathname3), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Collections.shuffle(NAMES);//перемешиваем список
        }

        public ClientBot(Client client) {
            this.client = client;
            name = NAMES.remove(0);//для вновь созданного бота вынимаем первое имя из списка
        }

        private String messageConstructor() {
            int i = (int)(Math.random()*FIRST.size());
            int j = (int)(Math.random()*SECOND.size());
            return FIRST.get(i) + SECOND.get(j);
        }

        private boolean requestBotName() throws IOException {
            while (true) {
                String message = client.readServer();
                if(message.equalsIgnoreCase("Введите ваше имя")) {
                    client.sendMessage(getName());
                }if(message.equalsIgnoreCase("Это имя уже занято, выберите другое")) {
                    client.sendMessage(getNewName());
                }if(message.contains("Приветствую, ")) {
                    break;
                }
            }
            return true;
        }

        private String getNewName() {
            setName(NAMES.remove(0));
            return getName();
        }

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }
    }
}

