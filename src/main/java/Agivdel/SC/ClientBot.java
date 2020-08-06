package Agivdel.SC;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

class ClientBot {
    private String name;
    private final Client client;

    private static final String pathname1 = "resources/the first part of message.txt";
    private static final String pathname2 = "resources/the second part of message.txt";
    private static final String pathname3 = "resources/bot names.txt";

    private static List<String> FIRST;
    private static List<String> SECOND;
    private static List<String> NAMES;
//    File file1 = new File(this.getClass().getClassLoader().getResource(pathname1).getFile());
//    String file2 = this.getClass().getClassLoader().getResource(fileName).getFile();
//    String file3 = this.getClass().getClassLoader().getResource(fileName).getFile();

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

    String messageConstructor() {
        int i = (int)(Math.random()*FIRST.size());
        int j = (int)(Math.random()*SECOND.size());
        return FIRST.get(i) + SECOND.get(j);
    }

    boolean requestBotName() throws IOException {
        while (true) {
            String message = client.readMessage();
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
