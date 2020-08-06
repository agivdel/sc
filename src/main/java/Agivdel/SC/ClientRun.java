package Agivdel.SC;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRun {

    public static void main(String[] args) throws IOException {
        ExecutorService ex = Executors.newCachedThreadPool();

//        Client client = new Client("localhost", 4050);
        //запуск отдельного потока чтения с сервера, №1
//        new Thread(client.new ReadAndPrint()).start();//вложенный класс нестатический, т.е. принадлежит конкретному объекту
        //запуск отдельного потока чтения с сервера, №2
//        ex.submit(client.new ReadAndPrint());
        //запуск отдельного потока чтения с сервера, №3
//        ex.submit(() -> {
//            try {
//                while (true) {
//                    client.readAndPrint();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        //для человека
        //отдельный поток работы клиентской части вариант, №1
//        ex.submit(new HomoTalk(client));
        //отдельный поток работы клиентской части вариант, №2
//        ex.submit(new HomoTalk(new Client("localhost", 4050)));

        //для бота
//        ex.submit(new BotTalk(client, 8));
        ex.submit(new BotTalk(new Client("localhost", 4050), 8));
    }
}


class HomoTalk implements Runnable {
    Client client;

    public HomoTalk(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (/*!client.getCLIENT_SOCKET().isOutputShutdown()*/ true) {
            String message;//чтение с консоли и отправка на сервер - в потоке main
            try {
                message = client.readConsole();
                if (message.equalsIgnoreCase("exit")) {
                    client.closeClient();
                    System.out.println("Вы отключились");
                }
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


class BotTalk implements Runnable {
    Client client;
    int numberOfMessages;

    public BotTalk(Client client, int numberOfMessages) {
        this.client = client;
        this.numberOfMessages = numberOfMessages;
    }

    @Override
    public void run() {
        ClientBot bot = new ClientBot(client);//вначале создаем объект бота
        try {
            if (bot.requestBotName()) {
                for (int i = 0; i <= numberOfMessages; i++) {
                    try {
                        client.readAndPrint();
                        Thread.sleep(500);
                        client.sendMessage(bot.messageConstructor());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                client.closeClient();//завершаем поток после конца цикла работы бота
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

