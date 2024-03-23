package com.example.spring18.services.author;

import com.example.spring18.domain.Author;

import java.util.List;

public interface AuthorService {

    long getExistingAuthorIdOrSave(Author author);

    Author getAuthorById(long id);

    List<Author> getAllAuthors();
}
