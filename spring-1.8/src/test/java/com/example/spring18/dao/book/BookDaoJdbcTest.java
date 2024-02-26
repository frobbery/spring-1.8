package com.example.spring18.dao.book;

import com.example.spring18.dao.author.AuthorDaoJdbc;
import com.example.spring18.dao.genre.GenreDaoJdbc;
import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import com.example.spring18.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dao для работы с книгами должно:")
@JdbcTest
@Import({AuthorDaoJdbc.class, BookDaoJdbc.class, GenreDaoJdbc.class})
class BookDaoJdbcTest {

    @Autowired
    private BookDaoJdbc bookDao;

    @Autowired
    private AuthorDaoJdbc authorDao;

    @Autowired
    private GenreDaoJdbc genreDao;

    @DisplayName("Сохранять книгу с существующим автором")
    @Test
    void saveWithExistingAuthor() {
        //given
        Book bookToBeSaved = Book.builder()
                .name("testBook")
                .author(Author.builder()
                        .initials("Д.А")
                        .lastName("Рубина")
                        .build())
                .genres(List.of(Genre.builder()
                                .name("Приключения")
                                .build(),
                        Genre.builder()
                                .name("Тестовый жанр")
                                .build()))
                .build();

        //when
        int savedBookId = bookDao.save(bookToBeSaved);

        //then
        assertThat(bookDao.getById(savedBookId))
                .usingRecursiveComparison()
                .ignoringFields("id", "author.id", "genres.id")
                .isEqualTo(bookToBeSaved);
        assertDoesNotThrow(() -> genreDao.getByName("Тестовый жанр"));
    }

    @DisplayName("Сохранять книгу с несуществующим автором")
    @Test
    void saveWithNonExistingAuthor() {
        //given
        Author newAuthor = Author.builder()
                .initials("Н.Н.")
                .lastName("Неизвестный")
                .build();
        Book bookToBeSaved = Book.builder()
                .name("testBook")
                .author(newAuthor)
                .genres(List.of())
                .build();

        //when
        int savedBookId = bookDao.save(bookToBeSaved);

        //then
        assertThat(bookDao.getById(savedBookId))
                .usingRecursiveComparison()
                .ignoringFields("id", "author.id", "genres.id")
                .isEqualTo(bookToBeSaved);
        assertDoesNotThrow(() -> authorDao.getByLastnameAndInitials(newAuthor.getLastName(), newAuthor.getInitials()));
    }

    @DisplayName("Получать книгу по Id")
    @Test
    void getById() {
        //given
        Book expectedBook = Book.builder()
                .id(1)
                .name("Обычный приключенческий роман")
                .author(Author.builder()
                        .id(1)
                        .initials("А.С.")
                        .lastName("Пушкин")
                        .build())
                .genres(List.of(Genre.builder()
                        .id(1)
                        .name("Приключения")
                        .build()))
                .build();

        //when
        Book actualBook = bookDao.getById(expectedBook.getId());

        //then
        assertThat(actualBook)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
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
                        .genres(List.of(Genre.builder()
                                .id(1)
                                .name("Приключения")
                                .build()))
                        .build(),
                Book.builder()
                        .id(2)
                        .name("Обычный любовный роман")
                        .author(Author.builder()
                                .id(2)
                                .initials("Д.А")
                                .lastName("Рубина")
                                .build())
                        .genres(List.of(Genre.builder()
                                .id(2)
                                .name("Любовь")
                                .build()))
                        .build(),
                Book.builder()
                        .id(3)
                        .name("Любовно-приключенческий роман")
                        .author(Author.builder()
                                .id(2)
                                .initials("Д.А")
                                .lastName("Рубина")
                                .build())
                        .genres(List.of(Genre.builder()
                                        .id(1)
                                        .name("Приключения")
                                        .build(),
                                Genre.builder()
                                        .id(2)
                                        .name("Любовь")
                                        .build()))
                        .build());

        //when
        List<Book> actualBooks = bookDao.getAll();

        //then
        assertThat(actualBooks)
                .containsAll(expectedBooks)
                .hasSize(3);
    }

    @DisplayName("Обновлять данные книги по id с существующим автором")
    @Test
    void updateByIdWithExistingAuthor() {
        //given
        Book newBook = Book.builder()
                .id(1)
                .name("testBook")
                .author(Author.builder()
                        .initials("Д.А")
                        .lastName("Рубина")
                        .build())
                .genres(List.of(Genre.builder()
                                .name("Приключения")
                                .build(),
                        Genre.builder()
                                .name("Тестовый жанр")
                                .build()))
                .build();

        //when
        bookDao.updateById(newBook);

        //then
        assertThat(bookDao.getById(newBook.getId()))
                .usingRecursiveComparison()
                .ignoringFields("author.id", "genres.id")
                .isEqualTo(newBook);
        assertDoesNotThrow(() -> genreDao.getByName("Тестовый жанр"));
    }

    @DisplayName("Обновлять данные книги по id с несуществующим автором")
    @Test
    void updateByIdWithNonExistingAuthor() {
        //given
        Author newAuthor = Author.builder()
                .initials("Н.Н.")
                .lastName("Неизвестный")
                .build();
        Book newBook = Book.builder()
                .id(3)
                .name("testBook")
                .author(newAuthor)
                .genres(List.of())
                .build();

        //when
        bookDao.updateById(newBook);

        //then
        assertThat(bookDao.getById(newBook.getId()))
                .usingRecursiveComparison()
                .ignoringFields("author.id", "genres.id")
                .isEqualTo(newBook);
        assertDoesNotThrow(() -> authorDao.getByLastnameAndInitials(newAuthor.getLastName(), newAuthor.getInitials()));
        assertThat(genreDao.getGenresByBookId(newBook.getId())).isEmpty();
    }

    @DisplayName("Удалять книгу по id")
    @Test
    void delete() {
        //given
        int bookToBeDeletedId = bookDao.save(Book.builder().build());

        //when
        bookDao.delete(bookToBeDeletedId);

        //then
        assertThrows(DataAccessException.class, () -> bookDao.getById(bookToBeDeletedId));
    }
}