package com.example.spring18.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Author {

    private int id;

    private String initials;

    private String lastName;
}
