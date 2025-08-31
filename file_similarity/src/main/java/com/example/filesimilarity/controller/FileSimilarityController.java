package com.example.filesimilarity.controller;

import com.example.filesimilarity.dto.FileSimilarityResult;
import com.example.filesimilarity.service.FileSimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/similarity")
public class FileSimilarityController {

    @Autowired
    private FileSimilarityService fileSimilarityService;

    @GetMapping("/calculate")
    public ResponseEntity<List<FileSimilarityResult>> calculateSimilarities() {
        try {
            List<FileSimilarityResult> results = fileSimilarityService.calculateSimilarities();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/best-match")
    public ResponseEntity<FileSimilarityResult> getBestMatch() {
        try {
            FileSimilarityResult bestMatch = fileSimilarityService.getBestMatch();
            if (bestMatch != null) {
                return ResponseEntity.ok(bestMatch);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
