package com.example.spring18.dao.book;

import com.example.spring18.dao.author.AuthorDao;
import com.example.spring18.dao.genre.GenreDao;
import com.example.spring18.dao.util.BookGenreRelation;
import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import com.example.spring18.domain.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final AuthorDao authorDao;

    private final GenreDao genreDao;

    private final BookMapper bookMapper = new BookMapper();

    @Override
    public long save(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", book.getName());
        Long authorId = null;
        if (nonNull(book.getAuthor()) && nonNull(book.getAuthor().getLastName())) {
            authorId = getAuthorId(book.getAuthor());
        }
        KeyHolder kh = new GeneratedKeyHolder();
        if (nonNull(authorId)) {
            params.addValue("author_id", authorId);
            namedParameterJdbcOperations.update("insert into books (name, author_id) values (:name, :author_id)",
                    params, kh, new String[]{"id"});
        } else {
            namedParameterJdbcOperations.update("insert into books (name) values (:name)", params, kh,
                    new String[]{"id"});
        }
        long bookId = Objects.requireNonNull(kh.getKey()).longValue();
        saveGenres(book, bookId);
        return bookId;
    }

    private long getAuthorId(Author author) {
        try {
            Author authorFromDb = authorDao.getByLastnameAndInitials(author.getLastName(), author.getInitials());
            return authorFromDb.getId();
        } catch (DataAccessException dae) {
            return authorDao.save(author);
        }
    }

    private void saveGenres(Book book, long bookId) {
        if (!isEmpty(book.getGenres())) {
            book.getGenres().forEach(genre -> saveGenre(genre, bookId));
        }
    }

    private void saveGenre(Genre genre, long bookId) {
        long genre_id;
        try {
            genre_id = genreDao.getByName(genre.getName()).getId();
        } catch (DataAccessException dae) {
            genre_id = genreDao.save(genre);
        }
        namedParameterJdbcOperations.update("insert into books_genres (book_id, genre_id) values (:book_id, :genre_id)",
                Map.of("book_id", bookId, "genre_id", genre_id));
    }

    @Override
    public Book getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        Book book =  namedParameterJdbcOperations.queryForObject(
                "select books.id, books.name, authors.id, authors.initials, authors.lastname from books join authors" +
                        " on authors.id = books.author_id where books.id = :id",
                params, bookMapper);
        List<BookGenreRelation> bookGenreRelations = getBookGenreRelations(id);
        List<Genre> genres = genreDao.getAll();
        enrichBook(Objects.requireNonNull(book), bookGenreRelations, genres);
        return book;
    }

    private List<BookGenreRelation> getBookGenreRelations(long id) {
        return namedParameterJdbcOperations.query("select book_id, genre_id from books_genres where book_id=:id",
                Map.of("id", id), (rs, i) -> BookGenreRelation.builder()
                        .bookId(rs.getLong(1))
                        .genreId(rs.getLong(2))
                        .build());
    }

    private List<BookGenreRelation> getBookGenreRelations() {
        return namedParameterJdbcOperations.query("select book_id, genre_id from books_genres",
                (rs, i) -> BookGenreRelation.builder()
                        .bookId(rs.getLong(1))
                        .genreId(rs.getLong(2))
                        .build());
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
    public List<Book> getAll() {
        List<Book> books = namedParameterJdbcOperations.query("select books.id, books.name, authors.id, authors.initials, authors.lastname from books join authors " +
                                        "on authors.id = books.author_id", bookMapper);
        List<BookGenreRelation> bookGenreRelations = getBookGenreRelations();
        List<Genre> genres = genreDao.getAll();
        books.forEach(book -> enrichBook(Objects.requireNonNull(book), bookGenreRelations, genres));
        return books;
    }

    @Override
    public void updateById(Book newBook) {
        Long authorId = null;
        if (nonNull(newBook.getAuthor()) && nonNull(newBook.getAuthor().getLastName())) {
            authorId = getAuthorId(newBook.getAuthor());
        }
        namedParameterJdbcOperations.update("update books set name = :name, author_id = :author_id where id = :id",
                Map.of("id", newBook.getId(), "name", newBook.getName(), "author_id", authorId));
        updateGenres(newBook);
    }

    private void updateGenres(Book book) {
        List<Genre> oldGenres = genreDao.getGenresByBookId(book.getId());
        if (isNull(book.getGenres())) {
            deleteBookGenreLinks(book.getId());
        } else {
            book.getGenres().stream()
                    .filter(genre -> !oldGenres.contains(genre))
                    .forEach(genre -> saveGenre(genre, book.getId()));
            oldGenres.stream()
                    .filter(genre -> !book.getGenres().contains(genre))
                    .forEach(genre -> deleteBookGenreLink(book.getId(), genre.getId()));
        }
    }

    private void deleteBookGenreLink(long bookId, long genreId) {
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :book_id and genre_id = :genre_id",
                Map.of("book_id", bookId, "genre_id", genreId));
    }

    @Override
    public void delete(long id) {
        deleteBookGenreLinks(id);
        namedParameterJdbcOperations.update("delete from books where id = :book_id",
                Map.of("book_id", id));
    }

    private void deleteBookGenreLinks(long bookId) {
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :book_id",
                Map.of("book_id", bookId));
    }

    private static class BookMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Book.builder()
                    .id(rs.getLong(1))
                    .name(rs.getString(2))
                    .author(Author.builder()
                            .id(rs.getLong(3))
                            .initials(rs.getString(4))
                            .lastName(rs.getString(5))
                            .build())
                    .build();
        }
    }
}
