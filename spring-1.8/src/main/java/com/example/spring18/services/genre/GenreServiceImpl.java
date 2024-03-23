package com.example.spring18.services.genre;

import com.example.spring18.dao.genre.GenreDao;
import com.example.spring18.domain.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService{

    private final GenreDao genreDao;

    @Override
    public long getExistingGenreIdOrSave(Genre genre) {
        try {
            return genreDao.getByName(genre.getName()).getId();
        } catch (DataAccessException dae) {
            return genreDao.save(genre);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreDao.getAll();
    }

    @Override
    public List<Genre> getGenresByBookId(long bookId) {
        return genreDao.getGenresByBookId(bookId);
    }
}
