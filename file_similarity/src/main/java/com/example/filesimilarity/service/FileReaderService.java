package com.example.filesimilarity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileReaderService {

    private static final Logger logger = LoggerFactory.getLogger(FileReaderService.class);

    public String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + filePath);
        }

        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + filePath);
        }

        logger.debug("Reading file: {}", filePath);
        return Files.readString(path);
    }

    public List<Path> getFilesFromDirectory(String directoryPath) throws IOException {
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir)) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }

        if (!Files.isDirectory(dir)) {
            throw new IOException("Path is not a directory: " + directoryPath);
        }

        try (Stream<Path> paths = Files.list(dir)) {
            return paths.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }
}