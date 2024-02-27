package com.example.spring18.dao.util;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookGenreRelation {

    private long bookId;

    private long genreId;
}
