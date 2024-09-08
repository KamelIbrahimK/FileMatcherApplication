package com.example.filematcherapplication.controller;

import com.example.filematcherapplication.model.FileScore;
import com.example.filematcherapplication.service.FileComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileComparisonController {

    @Autowired
    private FileComparisonService fileComparisonService;

    @GetMapping("/compare")
    public List<FileScore> compareFiles() {
        try {
            return fileComparisonService.compareFiles();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); 
        }
    }
}