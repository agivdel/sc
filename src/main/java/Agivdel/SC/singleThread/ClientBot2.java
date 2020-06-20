package Agivdel.SC.singleThread;

import java.io.*;
import java.net.Socket;

public class ClientBot2 {

    private static Socket clientSocket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static final String[] LEXICON2 = {
            "Путин святой!",
            "А вы долг США видели?!",
            "Рогозин поднимет Роскосмос!",
            "А что там у хохлов?",
            "Вы хотите как в гейропе?!",
            "Пишут, американцы на Аляске погоду портят",
            "А вот раньше зимы настоящие были, морозные, не то что сейчас!",
            "Завтра дождь обещают",
            "Холод замучил, лето когда уже наступит?!",
            "Слякоть ужасная, грязища - ужас!",
            "А мне вот switch не очень нравится, я больше if-else люблю"};

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
                        int j = (int)(Math.random()*LEXICON2.length);
                        String messageFromClient = LEXICON2[j];
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
