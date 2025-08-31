package com.example.filesimilarity.service;

import com.example.filesimilarity.config.FileSimilarityProperties;
import com.example.filesimilarity.dto.FileSimilarityResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class FileSimilarityService {

    private static final Logger logger = LoggerFactory.getLogger(FileSimilarityService.class);

    private final FileSimilarityProperties properties;
    private final FileReaderService fileReaderService;
    private final WordProcessorService wordProcessorService;
    private final ExecutorService executorService;

    public FileSimilarityService(FileSimilarityProperties properties,
            FileReaderService fileReaderService,
            WordProcessorService wordProcessorService) {
        this.properties = properties;
        this.fileReaderService = fileReaderService;
        this.wordProcessorService = wordProcessorService;
        this.executorService = Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 8));
    }

    @PreDestroy
    public void shutdownExecutor() {
        logger.info("Shutting down ExecutorService...");
        executorService.shutdown();
    }

    public List<FileSimilarityResult> calculateSimilarities() {
        long startTime = System.currentTimeMillis();

        try {
            logger.info("Reading reference file: {}", properties.getReferenceFilePath());
            String referenceContent = fileReaderService.readFileContent(properties.getReferenceFilePath());

            Map<String, Integer> referenceWords = Collections
                    .unmodifiableMap(wordProcessorService.extractWordFrequencies(referenceContent));

            logger.info("Reference file contains {} unique words", referenceWords.size());

            List<Path> poolFiles = fileReaderService.getFilesFromDirectory(properties.getPoolDirectoryPath());
            logger.info("Found {} files in pool directory", poolFiles.size());

            if (poolFiles.isEmpty()) {
                logger.warn("No files found in pool directory");
                return Collections.emptyList();
            }

            List<CompletableFuture<FileSimilarityResult>> futures = poolFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> processFile(file, referenceWords), executorService)
                            .exceptionally(ex -> {
                                logger.error("Error processing file {}: {}", file.getFileName(), ex.getMessage());
                                return null;
                            }))
                    .collect(Collectors.toList());

            List<FileSimilarityResult> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingDouble(FileSimilarityResult::getSimilarityScore).reversed())
                    .collect(Collectors.toList());

            long totalTime = System.currentTimeMillis() - startTime;

            if (!results.isEmpty()) {
                String scoreStr = String.format(Locale.ROOT, "%.2f", results.get(0).getSimilarityScore());
                logger.info("Processing completed in {} ms. Best match: {} with score: {}%",
                        totalTime, results.get(0).getFileName(), scoreStr);
            } else {
                logger.info("Processing completed in {} ms. No matches found", totalTime);
            }

            return results;

        } catch (IOException e) {
            logger.error("Error reading reference or pool files: {}", e.getMessage());
            throw new RuntimeException("Failed to process files", e);
        }
    }

    private FileSimilarityResult processFile(Path filePath, Map<String, Integer> referenceWords) {
        long startTime = System.currentTimeMillis();
        String fileName = filePath.getFileName().toString();

        try {
            logger.debug("Processing file: {}", fileName);

            String content = fileReaderService.readFileContent(filePath.toString());
            Map<String, Integer> fileWords = wordProcessorService.extractWordFrequencies(content);

            double score = wordProcessorService.calculateSimilarityScore(referenceWords, fileWords);
            long processingTime = System.currentTimeMillis() - startTime;

            logger.debug("File {} processed with score {}% in {} ms",
                    fileName, String.format(Locale.ROOT, "%.2f", score), processingTime);

            return new FileSimilarityResult(fileName, score, processingTime);

        } catch (IOException e) {
            logger.error("Error processing file {}: {}", fileName, e.getMessage());
            return null;
        }
    }

    public FileSimilarityResult getBestMatch() {
        List<FileSimilarityResult> results = calculateSimilarities();
        return results.isEmpty() ? null : results.get(0);
    }
}
