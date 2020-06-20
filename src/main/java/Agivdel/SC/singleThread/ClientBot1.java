package Agivdel.SC.singleThread;

import java.io.*;
import java.net.Socket;

public class ClientBot1 {

    private static Socket clientSocket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static final String[] LEXICON1 = {
            "Путину в тюрьму!",
            "Жулики и воры!",
            "Просрали космос!",
            "Такую страну разворовали!",
            "Мизулину в дурдом!",
            "Такая ужасная погода стоит!",
            "А вот раньше зимы не такие холодные были!",
            "Дышать нечем, воздух совсем испортили своими заводами!",
            "Жара замучила, скорей бы зима!",
            "Что-то урожаи в этом году совсем плохие, в фейсбуке пишут",
            "А ты тоже табом отступы набиваешь?"};

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", 4050);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                System.out.println("Вы подключились к серверу");
                while (!clientSocket.isOutputShutdown()) {//работаем, если канал в порядке
                    for (int i = 0; i < 15; i++) {
                        int j = (int)(Math.random()*LEXICON1.length);
                        String messageFromClient = LEXICON1[j];
                        out.writeUTF(messageFromClient);
                        out.flush();
                        System.out.println(messageFromClient);
                        String wordFromServer = in.readUTF();
                        System.out.println(wordFromServer);//печать ответа сервера
                        Thread.sleep(2000);
                    }
                    break;
                }
            } finally {
                in.close();//сначала азкрываем каналы
                out.close();
                clientSocket.close();//потом закрываем сокет
                System.out.println("Вы отключились");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }
}
