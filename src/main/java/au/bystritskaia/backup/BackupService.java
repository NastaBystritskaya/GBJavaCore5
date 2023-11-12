package au.bystritskaia.backup;

import au.bystritskaia.tree.FileTree;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Сервис создания резервной копии
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BackupService {

    /**
     * Создает резервную копию папки
     *
     * @param path Путь к папке
     */
    public static void doBackup(String path) {
        try {
            Path[] paths = getPath(path);
            Path from = paths[0];
            Path to = paths[1];
            createZipFile(from, to);
            System.out.println("Резервная копия успешно создана");
            System.out.println("Файлы");
            printSource(from);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.printf("""
                    Не удалось создать резервную копию файлов.
                    Причина:
                    %s
                    %n""", e.getMessage());
        }
    }

    /**
     * Выводит архивированные файлы
     *
     * @param source Папка с файлами
     */
    private static void printSource(Path source) {
        FileTree root = new FileTree(source.getFileName().toString());
        addFilesToTree(root, source.toFile());
        System.out.println(root);

    }

    /**
     * Добавить файлы в дерево
     *
     * @param tree Дерево
     * @param file Файл
     */
    private static void addFilesToTree(FileTree tree, File file) {
        for (File temp : Objects.requireNonNull(file.listFiles())) {
            FileTree current = new FileTree(temp.getName());
            if (temp.listFiles() != null && Objects.requireNonNull(temp.listFiles()).length > 0)
                addFilesToTree(current, temp);
            tree.getChildren().add(current);
        }
    }

    /**
     * Создает архив
     *
     * @param source    Архив
     * @param backupDir Папка с архивом
     */
    private static void createZipFile(Path source, Path backupDir) {
        try {
            String fileSuffix = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
            Path zipFile = Paths.get(backupDir.toAbsolutePath().toString(), "backup-" + fileSuffix + ".zip");
            if(!Files.exists(zipFile))
                Files.createFile(zipFile);
            ZipUtil.pack(source.toFile(), zipFile.toFile());

        } catch (Throwable ex) {
            throw new RuntimeException("""
                    Не удалось сохранить архив с файлами
                    Причина:
                    %s
                    """.formatted(ex.getMessage()));
        }
    }

    /**
     * Получает пути
     *
     * @param path Путь до папки
     * @return Сформированные пути
     */
    private static Path[] getPath(@NonNull String path) {
        Path[] paths = new Path[2];
        Path out = Paths.get(path);

        if (!Files.exists(out))
            throw new RuntimeException("Путь к файлу не существет");
        paths[0] = out;
        Path backupDir = Paths.get(out.getParent().toAbsolutePath().toString(), ".backup");
        try {
            if (Files.exists(backupDir)) {
                paths[1] = backupDir;
            } else {
                paths[1] = Files.createDirectories(backupDir);
            }

            return paths;
        } catch (IOException e) {
            throw new RuntimeException("""
                    Не удалось создать папку для backup.
                    Сообщение:
                    %s
                    """.formatted(e.getMessage()));
        }
    }
}
