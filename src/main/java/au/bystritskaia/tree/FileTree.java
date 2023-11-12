package au.bystritskaia.tree;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Дерево файлов
 */
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileTree {

    /**
     * Имя файла
     */
    final String fileName;

    /**
     * Дочерние элементы
     */
    @Setter
    List<FileTree> children = new LinkedList<>();

    /**
     * Выводит дерево элементов
     * @param buffer Буффер
     * @param prefix Префикс элемента
     * @param childrenPrefix Префикс дочернего элемента
     */
    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(this.fileName);
        buffer.append("\n");
        for (Iterator<FileTree> it = children.iterator(); it.hasNext();) {
            FileTree next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }

    /**
     * Строчное представление дерева
     * @return Представление
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.print(builder, "", "");
        return builder.toString();
    }
}
