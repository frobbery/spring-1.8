package com.example.spring18.dao.author;

import com.example.spring18.domain.Author;

import java.util.List;

public interface AuthorDao {

     int save(Author author);

     Author getById(int id);

     Author getByLastnameAndInitials(String lastname, String initials);

     List<Author> getAll();

     void updateById(Author newAuthor);

     void delete(int id);
}
