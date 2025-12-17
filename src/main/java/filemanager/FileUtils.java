package filemanager;

import java.io.IOException;
import java.nio.file.*;

/**
 * Утилитарный класс для работы с файлами.
 */
public class FileUtils {

    /**
     * Копирует файл в указанную директорию.
     *
     * @param source исходный файл
     * @param targetDir директория назначения
     * @throws IOException при ошибке копирования
     */
    public static void copyFile(Path source, Path targetDir) throws IOException {
        if (!Files.isDirectory(targetDir)) {
            throw new IOException("Целевая директория не существует");
        }

        Path target = targetDir.resolve(source.getFileName());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Возвращает размер файла в байтах.
     *
     * @param path путь к файлу
     * @return размер файла
     * @throws IOException при ошибке чтения
     */
    public static long getFileSize(Path path) throws IOException {
        return Files.size(path);
    }
}

