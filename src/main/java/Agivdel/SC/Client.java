package Agivdel.SC;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Client {

    private static Socket clientSocket;
    private static DataInputStream IN;
    private static DataOutputStream OUT;
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
//        new Client().talk();
    }

    public void talk() throws IOException {//вариант с ручным управлением диалогом
        new Thread(new ReadMessage()).start();//чтение с сервера в отдельном потоке
        while (!clientSocket.isOutputShutdown()) {
            String message = readConsole();//чтение с консоли и отправка на сервре - в потоке main
            sendMessage(message);
        }
    }

    public void botTalk(int numberOfMessages) throws IOException, InterruptedException {//вариант с диалогом бота
        ClientBot bot = new ClientBot();//вначале создаем объект бота
        if (bot.requestBotName()) {
            for (int i = 0; i < numberOfMessages; i++) {
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

    static String readServer() throws IOException {
        return IN.readUTF();
    }

    static String readConsole() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String messageFromConsole = scanner.nextLine();
        if ("exit".equalsIgnoreCase(messageFromConsole)) {
            closeClient();
        }
        return messageFromConsole;
    }

    static void sendMessage(String message) throws IOException {
        OUT.writeUTF(message);
        OUT.flush();
    }

    static void closeClient() throws IOException {
        IN.close();//если случился break, сначала закрываем каналы
        OUT.close();
        clientSocket.close();//потом закрываем сокет
        System.out.println("Вы отключились");
    }

    public static String preMessage() {
        return String.format("%tH:%<tM:%<tS:%<tL: ", new Date());
    }

    /**
     * для клиентов с ручным управленеим чтение сообщений с сервера в отдельном потоке
     */
    static class ReadMessage implements Runnable {
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
}

class ClientBot {
    private String name;

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

    public ClientBot() {
        name = NAMES.remove(0);//для вновь созданного бота вынимаем первое имя из списка
    }

    public String messageConstructor() {
        int i = (int)(Math.random()*FIRST.size());
        int j = (int)(Math.random()*SECOND.size());
        return FIRST.get(i) + SECOND.get(j);
    }

    public boolean requestBotName() throws IOException {
        while (true) {
            String message = Client.readServer();
            if(message.equalsIgnoreCase("Введите ваше имя")) {
                Client.sendMessage(getName());
            }if(message.equalsIgnoreCase("Это имя уже занято, выберите другое")) {
                Client.sendMessage(getNewName());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}