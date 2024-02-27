package com.example.spring18.dao.genre;

import com.example.spring18.domain.Genre;

import java.util.List;

public interface GenreDao {

     long save(Genre genre);

     Genre getById(long id);

     Genre getByName(String name);

     List<Genre> getGenresByBookId(long bookId);

     List<Genre> getAll();

     void updateById(Genre newGenre);

     void delete(long id);
}
