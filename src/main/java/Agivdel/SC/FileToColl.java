package Agivdel.SC;

/**
 * методы класса преобразуют данные из текстовых файлов в одномерные массивы строк и списочные массивы строк.
 * пока переменные типа использовать не стал.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileToColl {

    /**
     * метод возвращает списочный массив, созданный из файла по указанному пути.
     * конструктором списочному массиву служит массив, возвращаемый методом toArray().
     * @param pathName путь к файлу
     * @return списочный массив
     * @throws IOException
     */
    public static ArrayList<String> toArrayList(String pathName) throws IOException {
        return new ArrayList<>(Arrays.asList(toArray(pathName)));
    }

    /**
     * метод возвращает одномерный массив строк, созданный из файла по указанному пути
     * @param pathName путь к файлу
     * @return массив строк
     * @throws IOException
     */
    public static String[] toArray(String pathName) throws IOException {
        FileInputStream stream = new FileInputStream(pathName);
        int length = stream.available();
        byte[] data = new byte[length];
        stream.read(data);
        String text = new String(data);
        return text.split("\r\n");
    }
}
