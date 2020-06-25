package Agivdel.SC;

import java.io.*;
import java.net.Socket;

public class ClientBot {

    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private int timer;
    private static final String[] LEXICON = {
            "Путину в тюрьму!",
            "Жулики и воры!",
            "Просрали космос!",
            "Такую страну разворовали!",
            "Когда РПЦ налогом обложат?",
            "Такая ужасная погода стоит!",
            "А вот раньше зимы не такие холодные были!",
            "Дышать нечем, воздух совсем испортили своими заводами!",
            "Жара замучила, скорей бы зима!",
            "Что-то урожаи в этом году совсем плохие, в фейсбуке пишут",
            "А ты тоже табом отступы набиваешь?",
            "Кто если не Путин?",
            "А вы долг США видели?",
            "Рогозин молодец, так эту Наса!!!!один",
            "Чё там у хохлов?",
            "Вы хотите как в гейропе?!",
            "Пишут, американцы на Аляске погоду портят",
            "А вот раньше зимы настоящие были, морозные, не то что сейчас!",
            "Завтра дождь обещают",
            "Холод замучил, лето когда уже наступит?!",
            "Слякоть ужасная, грязища - ужас!",
            "А мне вот switch не очень нравится, я больше if-else люблю"};

    public ClientBot(int timer) {
        this.timer = timer;
    }


    public void runOfBots() {
        try {
            try {
                clientSocket = new Socket("localhost", 4050);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//чтение с консоли
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                System.out.println("Вы подключились к серверу");
                while (!clientSocket.isOutputShutdown() && timer > 0) {//работаем, если канал в порядке
                    for (int i = 0; i < 15; i++) {
                        int j = (int)(Math.random()*LEXICON.length);
                        String messageFromClient = LEXICON[j];
                        out.writeUTF(messageFromClient);
                        out.flush();
                        System.out.println(messageFromClient);
                        String wordFromServer = in.readUTF();
                        System.out.println(wordFromServer);//печать ответа сервера
                        Thread.sleep(2000);
                        timer = timer - 2000;
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
