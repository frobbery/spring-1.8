package com.example.spring18.shell;


import com.example.spring18.domain.Author;
import com.example.spring18.domain.Book;
import com.example.spring18.domain.Genre;
import com.example.spring18.services.book.BookService;
import com.example.spring18.shell.aspect.CatchAndWrite;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@ShellComponent
public class ShellCommands {

    private final BookService bookService;

    @ShellMethod(value = "Add book", key = {"a", "add"})
    @CatchAndWrite
    public void addBook(@ShellOption String bookName, @ShellOption(defaultValue = "__NULL__") String authorInitials,
                        @ShellOption(defaultValue = "__NULL__") String authorLastname,
                        @ShellOption(defaultValue = "__NULL__") String... genreNames) {
        List<Genre> genres = new ArrayList<>();
        if (nonNull(genreNames)) {
            for (String genreName : genreNames) {
                genres.add(Genre.builder()
                        .name(genreName)
                        .build());
            }
        }
        long savedBookId = bookService.saveBook(Book.builder()
                .name(bookName)
                .author(Author.builder()
                        .initials(authorInitials)
                        .lastName(authorLastname)
                        .build())
                .genres(genres)
                .build());
        System.out.println("Saved book id : " + savedBookId);
    }

    @ShellMethod(value = "Get book by id", key = {"g", "get"})
    @CatchAndWrite
    public void getBookById(@ShellOption long id) {
        Book foundBook = bookService.getBookById(id);
        System.out.println(MessageFormat.format("Book by id {0}: {1}", id, foundBook));
    }

    @ShellMethod(value = "Get all books", key = {"all"})
    @CatchAndWrite
    public void getAllBooks() {
        List<Book> foundBooks = bookService.getAllBooks();
        System.out.println("Found books by are :\n" + foundBooks.stream()
                .map(Book::toString)
                .collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "Update book by id", key = {"u", "update"})
    @CatchAndWrite
    public void updateBook(@ShellOption long bookId, @ShellOption String bookName,
                           @ShellOption(defaultValue = "__NULL__") String authorInitials,
                           @ShellOption(defaultValue = "__NULL__") String authorLastname,
                           @ShellOption(defaultValue = "__NULL__") String... genreNames) {
        List<Genre> genres = new ArrayList<>();
        if (nonNull(genreNames)) {
            for (String genreName : genreNames) {
                genres.add(Genre.builder()
                        .name(genreName)
                        .build());
            }
        }
        bookService.updateBookById(Book.builder()
                .id(bookId)
                .name(bookName)
                .author(Author.builder()
                        .initials(authorInitials)
                        .lastName(authorLastname)
                        .build())
                .genres(genres)
                .build());
        System.out.println(MessageFormat.format("Book by id {0} is updated", bookId));
    }

    @ShellMethod(value = "Delete book by id", key = {"d", "delete"})
    @CatchAndWrite
    public void deleteBookById(@ShellOption long id) {
        bookService.deleteBookById(id);
        System.out.println(MessageFormat.format("Book by id {0} is deleted", id));
    }
}
