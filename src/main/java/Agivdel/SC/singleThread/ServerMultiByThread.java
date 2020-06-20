package Agivdel.SC.singleThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * вариант №1 - через класс-наследник Thread и объект этого наследника
 */
public class ServerMultiByThread {

    private static ServerSocket server;

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(4050);
                System.out.println("Сервер открыт");
                int i = 1;
                while (true) {
                    Socket clientSocket = server.accept();
                    System.out.println("Подключение №" + i + " установлено");
                    i++;
                    /**вариант №1:*/
//                    NewThread newThread = new NewThread(clientSocket);//создание нового потока через объект наследника Thread
//                    newThread.start();//запуск потока через вызов метода run()
                    /**вариант №2: короче*/
                    new NewThread(clientSocket).start();
                }
            } finally {
                server.close();//закрываем сокет сервера (при многопоточности это не нужно)
                System.out.println("Сервер закрыт");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * класс-наследник от Thread. Прямо в классе нужно прописать, что именно делает созданный здесь поток-нить
     */
    public static class NewThread extends Thread {
        private final DataInputStream IN;
        private final DataOutputStream OUT;

        public NewThread(Socket newClientSocket) throws IOException {
            IN = new DataInputStream(newClientSocket.getInputStream());
            OUT = new DataOutputStream(newClientSocket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {//возвращает ложь, пока поток не прерван
                    String messageFromClient = IN.readUTF();
                    if ("exit".equalsIgnoreCase(messageFromClient)) {
                        System.out.println("Клиент отключился");
                        IN.close();//закрываем каналы этого клиента
                        OUT.close();
                    }
                    System.out.println(messageFromClient);
                    OUT.writeUTF("echoing: " + messageFromClient); //вариант для Data, "\n" ставится автоматом
                    OUT.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
