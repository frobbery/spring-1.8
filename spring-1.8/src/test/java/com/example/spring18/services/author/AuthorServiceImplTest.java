package com.example.spring18.services.author;

import com.example.spring18.dao.author.AuthorDao;
import com.example.spring18.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для работы с авторами должен:")
@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorDao authorDao;

    @InjectMocks
    private AuthorServiceImpl sut;

    @Test
    @DisplayName("Возвращать id существующего автора")
    void shouldReturnExistingAuthorId_whenAuthorExists() {
        //given
        var author = author();
        when(authorDao.getByLastnameAndInitials(author.getLastName(), author.getInitials())).thenReturn(author);

        //when
        var result = sut.getExistingAuthorIdOrSave(author);

        //then
        assertEquals(author.getId(), result);
    }

    @Test
    @DisplayName("Возвращать id сохраненного автора")
    void shouldSaveNewAuthorAndReturnItsId_whenAuthorNotExists() {
        //given
        var author = author();
        var savedAuthorId = 2L;
        when(authorDao.getByLastnameAndInitials(author.getLastName(), author.getInitials())).thenThrow(mock(DataAccessException.class));
        when(authorDao.save(author)).thenReturn(savedAuthorId);

        //when
        var result = sut.getExistingAuthorIdOrSave(author);

        //then
        assertEquals(savedAuthorId, result);
    }

    @Test
    @DisplayName("Должен возвращать автора по id")
    void shouldReturnAuthorById() {
        //given
        var expectedAuthor = author();
        when(authorDao.getById(expectedAuthor.getId())).thenReturn(expectedAuthor);

        //when
        var actualAuthor = sut.getAuthorById(expectedAuthor.getId());

        //then
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Должен возвращать всех авторов")
    void shouldReturnAllAuthors() {
        //given
        var expectedAuthors = List.of(author());
        when(authorDao.getAll()).thenReturn(expectedAuthors);

        //when
        var actualAuthors = sut.getAllAuthors();

        //then
        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthors);
    }

    private static Author author() {
        return Author.builder()
                .id(1L)
                .lastName("lastName")
                .initials("initials")
                .build();
    }
}