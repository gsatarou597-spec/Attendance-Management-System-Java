package com.cu.attendance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Small reusable text-file persistence utility.
 * Each record is stored as one pipe-delimited UTF-8 line.
 */
public class FileStorage {
    private final Path dataDirectory;

    public FileStorage(String directory) throws IOException {
        dataDirectory = Paths.get(directory);
        Files.createDirectories(dataDirectory);
    }

    /** Makes a value safe to store in a pipe-delimited, one-line-per-record file. */
    public static String sanitize(String value) {
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }

    public List<String> readLines(String fileName) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        if (!Files.exists(file)) return new ArrayList<String>();
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    public void appendLine(String fileName, String line) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        Files.write(file, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void overwrite(String fileName, List<String> lines) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
