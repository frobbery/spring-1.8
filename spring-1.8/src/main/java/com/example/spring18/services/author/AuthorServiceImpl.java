package com.example.spring18.services.author;

import com.example.spring18.dao.author.AuthorDao;
import com.example.spring18.domain.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService{

    private final AuthorDao authorDao;

    @Override
    public long getExistingAuthorIdOrSave(Author author) {
        try {
            Author authorFromDb = authorDao.getByLastnameAndInitials(author.getLastName(), author.getInitials());
            return authorFromDb.getId();
        } catch (DataAccessException dae) {
            return authorDao.save(author);
        }
    }

    @Override
    public Author getAuthorById(long id) {
        return authorDao.getById(id);
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorDao.getAll();
    }
}
