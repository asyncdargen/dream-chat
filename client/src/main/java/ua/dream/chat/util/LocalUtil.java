package ua.dream.chat.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class LocalUtil {

    public File FOLDER = new File(System.getenv("appdata"), "DreamChat");

    @SneakyThrows
    public void setContent(String path, Consumer<BufferedWriter> writer) {
        checkAndMkdirFolder();

        val file = new File(FOLDER, path);

        if (!file.exists())
            file.createNewFile();

        val fileOutputStream = new FileOutputStream(file);
        val outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        val bufferedWriter = new BufferedWriter(outputStreamWriter);

        writer.accept(bufferedWriter);

        bufferedWriter.flush();
        bufferedWriter.close();
    }

    @SneakyThrows
    public <T> T getContent(String path, Function<BufferedReader, T> transform) {
        checkAndMkdirFolder();

        val file = new File(FOLDER, path);

        if (!file.exists())
            return null;

        val fileInputStream = new FileInputStream(file);
        val inputStreamReader = new InputStreamReader(fileInputStream);
        val bufferedReader = new BufferedReader(inputStreamReader);

        val result = transform.apply(bufferedReader);

        bufferedReader.close();

        return result;
    }

    public String getStringContent(String path) {
        return getContent(path, reader -> reader.lines().collect(Collectors.joining("\n")));
    }

    public void checkAndMkdirFolder() {
        if (!FOLDER.exists())
            FOLDER.mkdirs();

        if (!FOLDER.isDirectory()) {
            FOLDER.delete();
            checkAndMkdirFolder();
        }
    }

}
