package com.example.filesimilarity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.similarity")
public class FileSimilarityProperties {

    private String referenceFilePath;
    private String poolDirectoryPath;

    public String getReferenceFilePath() {
        return referenceFilePath;
    }

    public void setReferenceFilePath(String referenceFilePath) {
        this.referenceFilePath = referenceFilePath;
    }

    public String getPoolDirectoryPath() {
        return poolDirectoryPath;
    }

    public void setPoolDirectoryPath(String poolDirectoryPath) {
        this.poolDirectoryPath = poolDirectoryPath;
    }
}