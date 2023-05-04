package com.project.lab.models;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class Response {
    private List<Article> docs;
}
