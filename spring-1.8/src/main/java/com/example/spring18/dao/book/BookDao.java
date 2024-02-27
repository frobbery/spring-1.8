package com.example.spring18.dao.book;

import com.example.spring18.domain.Book;

import java.util.List;

public interface BookDao {

     long save(Book book);

     Book getById(long id);

     List<Book> getAll();

     void updateById(Book newBook);

     void delete(long id);
}
