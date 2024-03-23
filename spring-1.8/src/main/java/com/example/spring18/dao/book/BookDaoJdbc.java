package com.example.spring18.dao.book;

import com.example.spring18.dao.util.BookGenreRelation;
import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final BookMapper bookMapper = new BookMapper();

    @Override
    public long save(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", book.getName());
        params.addValue("author_id", nonNull(book.getAuthor()) ? book.getAuthor().getId() : null);
        KeyHolder kh = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update("insert into books (name, author_id) values (:name, :author_id)", params, kh,
                new String[]{"id"});
        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    @Override
    public void updateAuthorId(long bookId, Long authorId) {
        namedParameterJdbcOperations.update("update books set author_id = :author_id where id = :id",
                Map.of("id", bookId, "author_id", authorId));
    }

    @Override
    public void createBookGenreLink(long bookId, long genreId) {
        namedParameterJdbcOperations.update("insert into books_genres (book_id, genre_id) values (:book_id, :genre_id)",
                Map.of("book_id", bookId, "genre_id", genreId));
    }

    @Override
    public Book getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        return namedParameterJdbcOperations.queryForObject(
                "select books.id, books.name, authors.id, authors.initials, authors.lastname from books join authors" +
                        " on authors.id = books.author_id where books.id = :id",
                params, bookMapper);
    }

    public List<BookGenreRelation> getBookGenreRelations(long id) {
        return namedParameterJdbcOperations.query("select book_id, genre_id from books_genres where book_id=:id",
                Map.of("id", id), (rs, i) -> BookGenreRelation.builder()
                        .bookId(rs.getLong(1))
                        .genreId(rs.getLong(2))
                        .build());
    }

    public List<BookGenreRelation> getBookGenreRelations() {
        return namedParameterJdbcOperations.query("select book_id, genre_id from books_genres",
                (rs, i) -> BookGenreRelation.builder()
                        .bookId(rs.getLong(1))
                        .genreId(rs.getLong(2))
                        .build());
    }

    @Override
    public List<Book> getAll() {
        return namedParameterJdbcOperations.query("select books.id, books.name, authors.id, authors.initials, authors.lastname from books join authors " +
                                        "on authors.id = books.author_id", bookMapper);
    }

    @Override
    public void updateById(Book newBook) {
        namedParameterJdbcOperations.update("update books set name = :name where id = :id",
                Map.of("id", newBook.getId(), "name", newBook.getName()));
    }

    public void deleteBookGenreLink(long bookId, long genreId) {
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :book_id and genre_id = :genre_id",
                Map.of("book_id", bookId, "genre_id", genreId));
    }

    @Override
    public void delete(long id) {
        deleteBookGenreLinks(id);
        namedParameterJdbcOperations.update("delete from books where id = :book_id",
                Map.of("book_id", id));
    }

    public void deleteBookGenreLinks(long bookId) {
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
