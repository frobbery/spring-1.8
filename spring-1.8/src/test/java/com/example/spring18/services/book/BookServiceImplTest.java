package com.example.spring18.services.book;

import com.example.spring18.dao.book.BookDao;
import com.example.spring18.dao.util.BookGenreRelation;
import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import com.example.spring18.domain.Genre;
import com.example.spring18.services.author.AuthorService;
import com.example.spring18.services.genre.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Сервис для работы с книгами должен:")
@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookDao bookDao;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private BookServiceImpl sut;

    @Test
    @DisplayName("Должен сохранять книгу")
    void shouldSaveBook() {
        //given
        var author = Author.builder()
                .lastName("lastname")
                .initials("initials")
                .build();
        var genre = Genre.builder()
                .name("genreName")
                .build();
        var book = Book.builder()
                .name("name")
                .author(author)
                .genres(List.of(genre))
                .build();
        var savedBookId = 1L;
        var authorId = 2L;
        var genreId = 3L;
        when(bookDao.save(book)).thenReturn(savedBookId);
        when(authorService.getExistingAuthorIdOrSave(author)).thenReturn(authorId);
        when(genreService.getExistingGenreIdOrSave(genre)).thenReturn(genreId);

        //when
        var result = sut.saveBook(book);

        //then
        assertEquals(savedBookId, result);
        verify(bookDao, times(1)).createBookGenreLink(savedBookId, genreId);
    }

    @Test
    @DisplayName("Должен получать книгу по id")
    void shouldGetBookById() {
        //given
        var author = Author.builder()
                .id(2L)
                .lastName("lastname")
                .initials("initials")
                .build();
        var genre = Genre.builder()
                .id(3L)
                .name("genreName")
                .build();
        var book = Book.builder()
                .id(1L)
                .name("name")
                .author(author)
                .build();
        var expectedBook = Book.builder()
                .id(1L)
                .name("name")
                .author(author)
                .genres(List.of(genre))
                .build();
        when(bookDao.getById(book.getId())).thenReturn(book);
        when(bookDao.getBookGenreRelations(book.getId())).thenReturn(List.of(BookGenreRelation.builder()
                .bookId(book.getId())
                .genreId(genre.getId())
                .build()));
        when(genreService.getAllGenres()).thenReturn(List.of(genre));

        //when
        var actualBook = sut.getBookById(book.getId());

        //then
        assertThat(actualBook)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("Должен получать все книги")
    void shouldGetAllBooks() {
        //given
        var author = Author.builder()
                .id(2L)
                .lastName("lastname")
                .initials("initials")
                .build();
        var genre = Genre.builder()
                .id(3L)
                .name("genreName")
                .build();
        var books = List.of(Book.builder()
                .id(1L)
                .name("name")
                .author(author)
                .build());
        var expectedBooks = List.of(Book.builder()
                .id(1L)
                .name("name")
                .author(author)
                .genres(List.of(genre))
                .build());
        when(bookDao.getAll()).thenReturn(books);
        when(bookDao.getBookGenreRelations()).thenReturn(List.of(BookGenreRelation.builder()
                .bookId(books.get(0).getId())
                .genreId(genre.getId())
                .build()));
        when(genreService.getAllGenres()).thenReturn(List.of(genre));

        //when
        var actualBooks = sut.getAllBooks();

        //then
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .isEqualTo(expectedBooks);
    }

    @Test
    @DisplayName("Должен обновлять книгу, у которой нет жанров")
    void shouldUpdateBook_whenNoGenres() {
        //given
        var author = Author.builder()
                .id(2L)
                .lastName("lastname")
                .initials("initials")
                .build();
        var genre = Genre.builder()
                .id(3L)
                .name("genreName")
                .build();
        var expectedBook = Book.builder()
                .id(1L)
                .name("newName")
                .author(author)
                .genres(List.of())
                .build();
        when(genreService.getGenresByBookId(expectedBook.getId())).thenReturn(List.of(genre));
        when(authorService.getExistingAuthorIdOrSave(author)).thenReturn(author.getId());

        //when
        sut.updateBookById(expectedBook);

        //then
        verify(bookDao, times(1)).updateById(expectedBook);
        verify(bookDao, times(1)).updateAuthorId(expectedBook.getId(), author.getId());
        verify(bookDao, times(1)).deleteBookGenreLinks(expectedBook.getId());
    }

    @Test
    @DisplayName("Должен обновлять книгу c новыми жанрами")
    void shouldUpdateBook_whenNewGenres() {
        //given
        var oldGenre = Genre.builder()
                .id(3L)
                .name("genreName")
                .build();
        var newGenre = Genre.builder()
                .id(4L)
                .name("newGenreName")
                .build();
        var expectedBook = Book.builder()
                .id(1L)
                .name("newName")
                .author(null)
                .genres(List.of(newGenre))
                .build();
        when(genreService.getGenresByBookId(expectedBook.getId())).thenReturn(List.of(oldGenre));

        //when
        sut.updateBookById(expectedBook);

        //then
        verify(bookDao, times(1)).updateById(expectedBook);
        verify(bookDao, times(1)).updateAuthorId(expectedBook.getId(), null);
        verify(bookDao, times(1)).createBookGenreLink(expectedBook.getId(), newGenre.getId());
        verify(bookDao, times(1)).deleteBookGenreLink(expectedBook.getId(), oldGenre.getId());
    }

    @Test
    @DisplayName("Должен удалять книгу по id")
    void shouldDeleteBookById() {
        //given
        var book = Book.builder().id(1L).build();

        //when
        sut.deleteBookById(book.getId());

        //then
        verify(bookDao, times(1)).delete(book.getId());
    }
}