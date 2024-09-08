package com.example.filematcherapplication.service;
import com.example.filematcherapplication.model.FileScore;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class FileComparisonService {
    private static final Logger logger = LoggerFactory.getLogger(FileComparisonService.class);
    @Value("${fileA.path}")
    private String fileAPath;

    @Value("${files.pool.path}")
    private String filesPoolPath;

    public List<FileScore> compareFiles() throws IOException {
        logger.info("Starting file comparison.");
        validateFilesInPool(filesPoolPath);
        Set<String> wordsInFileA = getWordsFromFile(fileAPath);
        List<FileScore> scores = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filesPoolPath))) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    validateWordCountInFile(filePath.toString());
                }
            }
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filesPoolPath))) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    logger.info("Comparing with file: {}", filePath.getFileName());
                    Set<String> wordsInPoolFile = getWordsFromFile(filePath.toString());
                    double score = calculateScore(wordsInFileA, wordsInPoolFile);
                    logger.info("Score for {}: {}", filePath.getFileName(), score);
                    scores.add(new FileScore(filePath.getFileName().toString(), score));
                }
            }
        }
        logger.info("File comparison completed.");
        return scores;
    }

    private void validateFilesInPool(String filesPoolPath) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(filesPoolPath))) {
            long fileCount = StreamSupport.stream(stream.spliterator(), false).count();
            if (fileCount == 0||fileCount > 20) {
                throw new IllegalArgumentException("Number of files must be between 1 and 20, found: " + fileCount);
            }
        }

    }

    private void validateWordCountInFile(String filePath) throws IOException {
        long wordCount = Files.lines(Paths.get(filePath))
                .flatMap(line -> Arrays.stream(line.split("\\W+")))
                .count();
        logger.info("File: {}, Word Count: {}", filePath, wordCount);

        if (wordCount == 0) {
            logger.warn("The file {} is empty.", filePath);
        }

        if ( wordCount > 10000000) {
            throw new IllegalArgumentException("Number of words in file " + filePath + " must be between 0 and 10 million, found: " + wordCount);
        }
    }


    private Set<String> getWordsFromFile(String filePath) throws IOException {
        logger.info("Reading words from file: {}", filePath);
        Set<String> words = new HashSet<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.flatMap(line -> Arrays.stream(line.split("\\W+"))).map(String::toLowerCase)
                    .filter(word -> word.matches("[a-zA-Z]+"))
                    .forEach(words::add);
        }
        logger.info("Found {} words in file: {}", words.size(), filePath);
        return words;
    }

    private double calculateScore(Set<String> wordsA, Set<String> wordsB) {
        if (wordsA.equals(wordsB)) {
            return 100.0;
        }

        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);

        double score = (double) intersection.size() / wordsA.size() * 100;
        return Math.round(score * 100.0) / 100.0;
    }
}