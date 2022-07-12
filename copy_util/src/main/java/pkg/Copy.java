package pkg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class Copy {
    public static void copyFile(final Path source, Path destination) throws IOException {
        Objects.requireNonNull(source);
        Objects.requireNonNull(destination);

        if (!Files.exists(source)) {
            throw new IllegalStateException("Source not exists " + source);
        }

        final Path parent = destination.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.exists(destination)) {
            Files.delete(destination);
        }

//        Files.createFile(destination);

        try (final BufferedInputStream input = new BufferedInputStream(Files.newInputStream(source, StandardOpenOption.READ))) {
            try (final BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(destination, StandardOpenOption.CREATE_NEW))) {
                input.transferTo(output);
            }
        }
    }
}
