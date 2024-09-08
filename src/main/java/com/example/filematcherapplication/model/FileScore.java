package com.example.filematcherapplication.model;

import lombok.Getter;

@Getter
public class FileScore {
    private String fileName;
    private double score;


    public FileScore(String fileName, double score) {
        this.fileName = fileName;
        this.score = score;
    }

}
