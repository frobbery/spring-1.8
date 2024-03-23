package com.example.spring18.services.genre;

import com.example.spring18.dao.genre.GenreDao;
import com.example.spring18.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для работы с жанрами должен:")
@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreDao genreDao;

    @InjectMocks
    private GenreServiceImpl sut;

    @Test
    @DisplayName("Возвращать id существующего жанра")
    void shouldReturnExistingGenreId_whenGenreExists() {
        //given
        var genre = genre();
        when(genreDao.getByName(genre.getName())).thenReturn(genre);

        //when
        var result = sut.getExistingGenreIdOrSave(genre);

        //then
        assertEquals(genre.getId(), result);
    }

    @Test
    @DisplayName("Возвращать id сохраненного жанра")
    void shouldSaveNewGenreAndReturnItsId_whenGenreNotExists() {
        //given
        var genre = genre();
        var savedGenreId = 2L;
        when(genreDao.getByName(genre.getName())).thenThrow(mock(DataAccessException.class));
        when(genreDao.save(genre)).thenReturn(savedGenreId);

        //when
        var result = sut.getExistingGenreIdOrSave(genre);

        //then
        assertEquals(savedGenreId, result);
    }

    @Test
    @DisplayName("Должен возвращать все жанры")
    void shouldReturnAllGenres() {
        //given
        var expectedGenres = List.of(genre());
        when(genreDao.getAll()).thenReturn(expectedGenres);

        //when
        var actualGenres = sut.getAllGenres();

        //then
        assertThat(actualGenres)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);
    }

    @Test
    @DisplayName("Должен возвращать жанры книги по ее id")
    void shouldReturnGenresByBookId() {
        //given
        var bookId = 2L;
        var expectedGenres = List.of(genre());
        when(genreDao.getGenresByBookId(bookId)).thenReturn(expectedGenres);

        //when
        var actualGenres = sut.getGenresByBookId(bookId);

        //then
        assertThat(actualGenres)
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);
    }

    private static Genre genre() {
        return Genre.builder()
                .id(1L)
                .name("name")
                .build();
    }
}