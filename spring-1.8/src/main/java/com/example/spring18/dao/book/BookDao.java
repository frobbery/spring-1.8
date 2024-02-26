package com.example.spring18.dao.book;

import com.example.spring18.domain.Book;

import java.util.List;

public interface BookDao {

     int save(Book book);

     Book getById(int id);

     List<Book> getAll();

     void updateById(Book newBook);

     void delete(int id);
}
