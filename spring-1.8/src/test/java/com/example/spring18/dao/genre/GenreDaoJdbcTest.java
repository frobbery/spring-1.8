package com.example.spring18.dao.genre;

import com.example.spring18.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Dao для работы с жанрами должно:")
@JdbcTest
@Import(GenreDaoJdbc.class)
class GenreDaoJdbcTest {

    @Autowired
    private GenreDaoJdbc genreDao;

    @DisplayName("Сохранять жанр")
    @Test
    void save() {
        //given
        Genre genreToBeSaved = Genre.builder()
                .name("testGenre")
                .build();

        //when
        long savedId = genreDao.save(genreToBeSaved);

        //then
        assertThat(genreDao.getById(savedId))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(genreToBeSaved);
    }

    @DisplayName("Получать жанр по id")
    @Test
    void getById() {
        //given
        Genre expectedGenre = Genre.builder()
                .id(1)
                .name("Adventure")
                .build();

        //when
        Genre actualGenre = genreDao.getById(1);

        //then
        assertThat(actualGenre)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenre);
    }

    @DisplayName("Получать жанр по названию")
    @Test
    void getByName() {
        //given
        Genre expectedGenre = Genre.builder()
                .id(1)
                .name("Adventure")
                .build();

        //when
        Genre actualGenre = genreDao.getByName("Adventure");

        //then
        assertThat(actualGenre)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenre);
    }

    @DisplayName("Получать все жанры книги по ее id")
    @Test
    void getGenresByBookId() {
        //given
        int bookId = 3;
        List<Genre> expectedGenres = List.of(Genre.builder()
                        .id(1)
                        .name("Adventure")
                        .build(),
                Genre.builder()
                        .id(2)
                        .name("Romance")
                        .build());

        //when
        List<Genre> actualGenres = genreDao.getGenresByBookId(bookId);

        //then
        assertThat(actualGenres)
                .containsAll(expectedGenres)
                .hasSize(2);
    }

    @DisplayName("Получать все жанры")
    @Test
    void getAll() {
        //given
        List<Genre> expectedGenres = List.of(Genre.builder()
                        .id(1)
                        .name("Adventure")
                        .build(),
                Genre.builder()
                        .id(2)
                        .name("Romance")
                        .build());

        //when
        List<Genre> actualGenres = genreDao.getAll();

        //then
        assertThat(actualGenres)
                .containsAll(expectedGenres)
                .hasSize(2);
    }

    @DisplayName("Обновлять данные жанра по id")
    @Test
    void updateById() {
        //given
        Genre newGenre = Genre.builder()
                .id(1L)
                .name("Not adventure")
                .build();

        //when
        genreDao.updateById(newGenre);

        //then
        assertThat(genreDao.getById(newGenre.getId()))
                .usingRecursiveComparison()
                .isEqualTo(newGenre);
    }

    @DisplayName("Удалять жанр по id")
    @Test
    void delete() {
        //given
        long genreToBeDeletedId = genreDao.save(Genre.builder().build());

        //when
        genreDao.delete(genreToBeDeletedId);

        //then
        assertThrows(DataAccessException.class, () -> genreDao.getById(genreToBeDeletedId));
    }
}