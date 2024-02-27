package com.example.spring18.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Author {

    private long id;

    private String initials;

    private String lastName;
}
