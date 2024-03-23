package com.example.spring18.services.genre;

import com.example.spring18.domain.Genre;

import java.util.List;

public interface GenreService {

    long getExistingGenreIdOrSave(Genre genre);

    List<Genre> getAllGenres();

    List<Genre> getGenresByBookId(long bookId);
}
