package com.example.filesimilarity.dto;

public class FileSimilarityResult {
    private String fileName;
    private double similarityScore;
    private long processingTimeMs;

    public FileSimilarityResult(String fileName, double similarityScore, long processingTimeMs) {
        this.fileName = fileName;
        this.similarityScore = similarityScore;
        this.processingTimeMs = processingTimeMs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    @Override
    public String toString() {
        return String.format("File: %s, Score: %.2f%%, Processing Time: %d ms",
                fileName, similarityScore, processingTimeMs);
    }
}