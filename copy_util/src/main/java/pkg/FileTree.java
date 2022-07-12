package pkg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileTree {
    public static List<Path> get(final Path root) throws IOException {
        List<Path> list;
        try (Stream<Path> stream = Files.walk(root)) {
            list = stream.filter(Files::isRegularFile).toList();
        }
        return list;
    }
}
