package com.example.spring18.dao.author;

import com.example.spring18.domain.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class AuthorDaoJdbc implements AuthorDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final AuthorMapper authorMapper = new AuthorMapper();

    @Override
    public long save(Author author) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("initials", author.getInitials());
        params.addValue("lastname", author.getLastName());
        KeyHolder kh = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update("insert into authors (initials, lastname) values (:initials, :lastname)",
                params, kh, new String[]{"id"});
        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    @Override
    public Author getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        return namedParameterJdbcOperations.queryForObject(
                "select * from authors where id = :id", params, authorMapper);
    }

    @Override
    public Author getByLastnameAndInitials(String lastname, String initials) {
        if (nonNull(initials)) {
            Map<String, Object> params = Map.of("lastname", lastname, "initials", initials);
            return namedParameterJdbcOperations.queryForObject(
                    "select * from authors where initials = :initials and lastname = :lastname", params, authorMapper);
        } else {
            return namedParameterJdbcOperations.queryForObject(
                    "select * from authors where initials is null and lastname = :lastname",
                    Map.of("lastname", lastname), authorMapper);
        }
    }

    @Override
    public List<Author> getAll() {
        return namedParameterJdbcOperations.query("select * from authors", authorMapper);
    }

    @Override
    public void updateById(Author newAuthor) {
        namedParameterJdbcOperations.update("update authors set initials = :initials, lastname = :lastname where id = :id",
                Map.of("id", newAuthor.getId(), "initials", newAuthor.getInitials(), "lastname", newAuthor.getLastName()));
    }

    @Override
    public void delete(long id) {
        namedParameterJdbcOperations.update("delete from authors where id = :id", Map.of("id", id));
    }

    private static class AuthorMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            String initials = rs.getString("initials");
            String lastname = rs.getString("lastname");
            return Author.builder()
                    .id(id)
                    .initials(initials)
                    .lastName(lastname)
                    .build();
        }
    }
}
