package com.example.spring18.dao.author;

import com.example.spring18.domain.Author;

import java.util.List;

public interface AuthorDao {

     long save(Author author);

     Author getById(long id);

     Author getByLastnameAndInitials(String lastname, String initials);

     List<Author> getAll();

     void updateById(Author newAuthor);

     void delete(long id);
}
