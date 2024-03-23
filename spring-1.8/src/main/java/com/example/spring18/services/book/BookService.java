package com.example.spring18.services.book;

import com.example.spring18.domain.Book;

import java.util.List;

public interface BookService {
    long saveBook(Book book);

    Book getBookById(long id);

    List<Book> getAllBooks();

    void updateBookById(Book book);

    void deleteBookById(long id);
}
