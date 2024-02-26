package com.example.spring18.dao.genre;

import com.example.spring18.domain.Genre;

import java.util.List;

public interface GenreDao {

     int save(Genre genre);

     Genre getById(int id);

     Genre getByName(String name);

     List<Genre> getGenresByBookId(int bookId);

     List<Genre> getAll();

     void updateById(Genre newGenre);

     void delete(int id);
}
