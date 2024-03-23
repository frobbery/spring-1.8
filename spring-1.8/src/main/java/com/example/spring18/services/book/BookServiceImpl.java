package com.example.spring18.services.book;

import com.example.spring18.dao.book.BookDao;
import com.example.spring18.dao.util.BookGenreRelation;
import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import com.example.spring18.domain.Genre;
import com.example.spring18.services.author.AuthorService;
import com.example.spring18.services.genre.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    private final AuthorService authorService;

    private final GenreService genreService;

    @Override
    public long saveBook(Book book) {
        var authorId = authorService.getExistingAuthorIdOrSave(book.getAuthor());
        book.setAuthor(Author.builder().id(authorId).build());
        var id = bookDao.save(book);
        saveGenres(id, book.getGenres());
        return id;
    }

    private void saveGenres(long bookId, List<Genre> genres) {
        if (!isEmpty(genres)) {
            genres.forEach(genre -> saveGenre(genre, bookId));
        }
    }

    private void saveGenre(Genre genre, long bookId) {
        long genreId = genreService.getExistingGenreIdOrSave(genre);
        bookDao.createBookGenreLink(bookId, genreId);
    }

    @Override
    public Book getBookById(long bookId) {
        var book =  bookDao.getById(bookId);
        enrichBook(book, bookDao.getBookGenreRelations(bookId), genreService.getAllGenres());
        return book;
    }

    private void enrichBook(Book book, List<BookGenreRelation> bookGenreRelations, List<Genre> genres) {
        Map<Long, Genre> genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));
        List<Genre> bookGenres = new ArrayList<>();
        bookGenreRelations.stream()
                .filter(relation -> relation.getBookId() == book.getId())
                .forEach(relation -> bookGenres.add(genreMap.get(relation.getGenreId())));
        book.setGenres(bookGenres);
    }

    @Override
    public List<Book> getAllBooks() {
        var books = bookDao.getAll();
        var genres = genreService.getAllGenres();
        var bookGenreRelations = bookDao.getBookGenreRelations();
        books.forEach(book -> enrichBook(book, bookGenreRelations, genres));
        return books;
    }

    @Override
    public void updateBookById(Book newBook) {
        bookDao.updateById(newBook);
        Long authorId = null;
        if (nonNull(newBook.getAuthor()) && nonNull(newBook.getAuthor().getLastName())) {
            authorId = authorService.getExistingAuthorIdOrSave(newBook.getAuthor());
        }
        bookDao.updateAuthorId(newBook.getId(), authorId);
        updateGenres(newBook.getId(), newBook.getGenres());
    }

    private void updateGenres(long bookId, List<Genre> genres) {
        List<Genre> oldGenres = genreService.getGenresByBookId(bookId);
        if (isEmpty(genres)) {
            bookDao.deleteBookGenreLinks(bookId);
        } else {
            genres.stream()
                    .filter(genre -> !oldGenres.contains(genre))
                    .forEach(genre -> bookDao.createBookGenreLink(bookId, genre.getId()));
            oldGenres.stream()
                    .filter(genre -> !genres.contains(genre))
                    .forEach(genre -> bookDao.deleteBookGenreLink(bookId, genre.getId()));
        }
    }

    @Override
    public void deleteBookById(long id) {
        bookDao.delete(id);
    }
}
