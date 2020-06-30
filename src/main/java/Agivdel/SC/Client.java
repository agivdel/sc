package Agivdel.SC;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public static void main(String[] args) throws IOException {
        new Client().run(30000);//если время не указывать, сообщение для сервера читается с консоли
    }

    private void run() throws IOException {//вариант с ручным управлением диалогом
        new Thread(new ReadMessage()).start();//чтение с сервера в отдельном потоке
        while (!clientSocket.isOutputShutdown()) {
            String message = readConsol();//чтение с консоли и отправка на сервре - в потоке main
            sendMessage(message);
        }
    }

    private void run(int timer) throws IOException {//вариант с диалогом бота
        new Thread(new ReadMessage()).start();//если боты, читать не обязательно
        ClientBot bot = new ClientBot();
        while (timer > 0/*& !clientSocket.isOutputShutdown()*/) {//работаем, если канал в порядке и время есть
            String message = bot.messageConstructor();
            sendMessage(message);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer -= 2000;
        }
    }

    static String readConsol() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
        String messageFromConsol = reader.readLine();
        if ("exit".equalsIgnoreCase(messageFromConsol)) {
            closeClient();
        }
        return messageFromConsol;
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

    static class ReadMessage implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String wordFromServer = IN.readUTF();//чтение с сервера
                    System.out.println(wordFromServer);//печать ответа сервера
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientBot {
    private static final String[] FIRST = {
            "Путин и Рогозин ",
            "РПЦ ",
            "Госдолг США ",
            "Чё там у них, ",
            "Вы хотите как там - ",
            "Американцы ",
            "Китайцы ",
            "Погода и синоптики ",
            "Дожди ",
            "Холод и слякоть ",
            "Лето и жара ",
            "Зима и снег ",
            "Картошка и капуста ",
            "Грибы ",
            "Отступы ",
            "Завтра ",};
    private static final String[] SECOND = {
            "жулики и воры!",
            "Рассею спасли!",
            "с колен поднимаются.",
            "на батуте с утра прыгают, по радио передавали.",
            "у хохлов?",
            "у пендосов?",
            "во Франции?",
            "все нас боялись!",
            "всю Сибирь уже захватили.",
            "уже берегов не видят.",
            "одна наша защита.",
            "поперли, косить можно!",
            "совсем не уродились.",
            "лучше табом или пробелом? ",
            "достали уже, сил нет терпеть!",
            "скоро кончатся, все нормально будет.",
            "мне нравятся, весь год их жду!",
            "каждый год короче и короче, куда все катится...",
            "радуют каждый день!",
            "полное кю",
            "почему не в намордниках?",
            "так все чатлане, а как работать, так..."};

    public String messageConstructor() {
        int i = (int)(Math.random()*FIRST.length);
        int j = (int)(Math.random()*SECOND.length);
        return FIRST[i] + SECOND[j];
    }
}