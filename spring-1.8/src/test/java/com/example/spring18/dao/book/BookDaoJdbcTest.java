package com.example.spring18.dao.book;

import com.example.spring18.dao.author.AuthorDaoJdbc;
import com.example.spring18.dao.genre.GenreDaoJdbc;
import com.example.spring18.dao.util.BookGenreRelation;
import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Dao для работы с книгами должно:")
@JdbcTest
@Import({AuthorDaoJdbc.class, BookDaoJdbc.class, GenreDaoJdbc.class})
class BookDaoJdbcTest {

    @Autowired
    private BookDaoJdbc bookDao;

    @DisplayName("Сохранять книгу")
    @Test
    void saveBook() {
        //given
        Book bookToBeSaved = Book.builder()
                .name("testBook")
                .author(Author.builder()
                        .id(1L)
                        .initials("А.С.")
                        .lastName("Пушкин")
                        .build())
                .build();

        //when
        long savedBookId = bookDao.save(bookToBeSaved);

        //then
        assertThat(bookDao.getById(savedBookId))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookToBeSaved);
    }

    @DisplayName("Обновить id автора книги")
    @Test
    void updateAuthorId() {
        //given
        var bookId = 1L;
        var authorId = 2L;
        Book expectedBook = Book.builder()
                .id(1L)
                .name("Обычный приключенческий роман")
                .author(Author.builder()
                        .id(2)
                        .initials("Д.А")
                        .lastName("Рубина")
                        .build())
                .build();

        //when
        bookDao.updateAuthorId(bookId, authorId);

        //then
        assertThat(bookDao.getById(expectedBook.getId()))
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @DisplayName("Создать связь между книгой и жанром")
    @Test
    void createBookGenreLink() {
        //given
        var bookId = 1L;
        var genreId = 2L;
        var expectedBookGenreRelation = BookGenreRelation.builder()
                .bookId(bookId)
                .genreId(genreId)
                .build();

        //when
        bookDao.createBookGenreLink(bookId, genreId);

        //then
        assertThat(bookDao.getBookGenreRelations(bookId))
                .hasSize(2)
                .contains(expectedBookGenreRelation);
    }

    @DisplayName("Получать книгу по Id")
    @Test
    void getById() {
        //given
        Book expectedBook = Book.builder()
                .id(1L)
                .name("Обычный приключенческий роман")
                .author(Author.builder()
                        .id(1L)
                        .initials("А.С.")
                        .lastName("Пушкин")
                        .build())
                .build();

        //when
        Book actualBook = bookDao.getById(expectedBook.getId());

        //then
        assertThat(actualBook)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @DisplayName("Получать связи между определенной книгой и ее жанрами")
    @Test
    void getBookGenreRelationsById() {
        //given
        var bookId = 1L;
        var genreId = 1L;
        var expectedBookGenreRelations = List.of(BookGenreRelation.builder()
                .bookId(bookId)
                .genreId(genreId)
                .build());

        //when
        var actualBookGenreRelations = bookDao.getBookGenreRelations(bookId);

        //then
        assertThat(actualBookGenreRelations)
                .usingRecursiveComparison()
                .isEqualTo(expectedBookGenreRelations);
    }

    @DisplayName("Получать связи между книгами и жанрами")
    @Test
    void getBookGenreRelations() {
        //given
        var expectedBookGenreRelations = List.of(BookGenreRelation.builder()
                        .bookId(1L)
                        .genreId(1L)
                        .build(),
                BookGenreRelation.builder()
                        .bookId(2L)
                        .genreId(2L)
                        .build(),
                BookGenreRelation.builder()
                        .bookId(3L)
                        .genreId(1L)
                        .build(),
                BookGenreRelation.builder()
                        .bookId(3L)
                        .genreId(2L)
                        .build());

        //when
        var actualBookGenreRelations = bookDao.getBookGenreRelations();

        //then
        assertThat(actualBookGenreRelations)
                .usingRecursiveComparison()
                .isEqualTo(expectedBookGenreRelations);
    }

    @DisplayName("Получать все книги")
    @Test
    void getAll() {
        //given
        List<Book> expectedBooks = List.of(Book.builder()
                        .id(1)
                        .name("Обычный приключенческий роман")
                        .author(Author.builder()
                                .id(1)
                                .initials("А.С.")
                                .lastName("Пушкин")
                                .build())
                        .build(),
                Book.builder()
                        .id(2)
                        .name("Обычный любовный роман")
                        .author(Author.builder()
                                .id(2)
                                .initials("Д.А")
                                .lastName("Рубина")
                                .build())
                        .build(),
                Book.builder()
                        .id(3)
                        .name("Любовно-приключенческий роман")
                        .author(Author.builder()
                                .id(2)
                                .initials("Д.А")
                                .lastName("Рубина")
                                .build())
                        .build());

        //when
        List<Book> actualBooks = bookDao.getAll();

        //then
        assertThat(actualBooks)
                .containsAll(expectedBooks)
                .hasSize(3);
    }

    @DisplayName("Обновлять данные книги по id")
    @Test
    void updateById() {
        //given
        Book newBook = Book.builder()
                .id(1)
                .name("testBook")
                .author(Author.builder()
                        .initials("А.С.")
                        .lastName("Пушкин")
                        .build())
                .build();

        //when
        bookDao.updateById(newBook);

        //then
        assertThat(bookDao.getById(newBook.getId()))
                .usingRecursiveComparison()
                .ignoringFields("author.id")
                .isEqualTo(newBook);
    }

    @DisplayName("Удалять связь между книгой и жанром")
    @Test
    void deleteBookGenreLink() {
        //given
        var bookId = 3L;
        var genreId = 2L;
        var expectedBookGenreRelation = BookGenreRelation.builder()
                .bookId(bookId)
                .genreId(1L)
                .build();

        //when
        bookDao.deleteBookGenreLink(bookId, genreId);

        //then
        assertThat(bookDao.getBookGenreRelations(bookId))
                .hasSize(1)
                .contains(expectedBookGenreRelation);
    }

    @DisplayName("Удалять все связи между книгой и жанрами")
    @Test
    void deleteBookGenreLinks() {
        //given
        var bookId = 3L;

        //when
        bookDao.deleteBookGenreLinks(bookId);

        //then
        assertThat(bookDao.getBookGenreRelations(bookId))
                .hasSize(0);
    }

    @DisplayName("Удалять книгу по id")
    @Test
    void delete() {
        //given
        long bookToBeDeletedId = bookDao.save(Book.builder().build());

        //when
        bookDao.delete(bookToBeDeletedId);

        //then
        assertThrows(DataAccessException.class, () -> bookDao.getById(bookToBeDeletedId));
    }
}