package com.example.spring18.dao.book;

import com.example.spring18.dao.util.BookGenreRelation;
import com.example.spring18.domain.Book;

import java.util.List;

public interface BookDao {

     long save(Book book);

     void updateAuthorId(long bookId, Long authorId);

     void createBookGenreLink(long bookId, long genreId);

     Book getById(long bookId);

     List<BookGenreRelation> getBookGenreRelations(long bookId);

     List<Book> getAll();

     List<BookGenreRelation> getBookGenreRelations();

     void updateById(Book newBook);

     void deleteBookGenreLinks(long bookId);

     void deleteBookGenreLink(long bookId, long genreId);

     void delete(long id);
}
