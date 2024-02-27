package com.example.spring18.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Book {

    private long id;

    private String name;

    private Author author;

    private List<Genre> genres;
}
