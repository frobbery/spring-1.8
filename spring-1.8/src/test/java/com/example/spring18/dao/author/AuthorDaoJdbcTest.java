package com.example.spring18.dao.author;

import com.example.spring18.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Dao для работы с авторами должно:")
@JdbcTest
@Import(AuthorDaoJdbc.class)
class AuthorDaoJdbcTest {

    @Autowired
    private AuthorDaoJdbc authorDao;

    @Test
    @DisplayName("Сохранять автора")
    void save() {
        //given
        Author authorToBeSaved = Author.builder()
                .initials("T.T.")
                .lastName("Test")
                .build();

        //when
        long savedId = authorDao.save(authorToBeSaved);

        //then
        assertThat(authorDao.getById(savedId))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(authorToBeSaved);
    }

    @Test
    @DisplayName("Получать автора по id")
    void getById() {
        //given
        Author expectedAuthor = Author.builder()
                .id(1)
                .initials("A.S.")
                .lastName("Pushkin")
                .build();

        //when
        Author actualAuthor = authorDao.getById(expectedAuthor.getId());

        //then
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Получать автора по инициалам и фамилии")
    void getByLastnameAndInitials() {
        //given
        Author expectedAuthor = Author.builder()
                .id(2)
                .initials("D.A.")
                .lastName("Rubina")
                .build();

        //when
        Author actualAuthor = authorDao.getByLastnameAndInitials(expectedAuthor.getLastName(),
                expectedAuthor.getInitials());

        //then
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Получать всех авторов")
    void getAll() {
        //given
        List<Author> expectedAuthors = List.of(Author.builder()
                .id(1)
                .initials("A.S.")
                .lastName("Pushkin")
                .build(),
                Author.builder()
                        .id(2)
                        .initials("D.A.")
                        .lastName("Rubina")
                        .build());

        //when
        List<Author> actualAuthors = authorDao.getAll();

        //then
        assertThat(actualAuthors).containsAll(expectedAuthors).hasSize(2);
    }

    @Test
    @DisplayName("Обновлять данные автора по id")
    void updateById() {
        //given
        Author newAuthor = Author.builder()
                .id(1)
                .initials("K.U.")
                .lastName("Kushkin")
                .build();

        //when
        authorDao.updateById(newAuthor);

        //then
        assertThat(authorDao.getById(newAuthor.getId()))
                .usingRecursiveComparison()
                .isEqualTo(newAuthor);
    }

    @Test
    @DisplayName("Удалять автора по id")
    void delete() {
        //given
        long authorToBeDeletedId = authorDao.save(Author.builder().build());

        //when
        authorDao.delete(authorToBeDeletedId);

        //then
        assertThrows(DataAccessException.class, () -> authorDao.getById(authorToBeDeletedId));
    }
}