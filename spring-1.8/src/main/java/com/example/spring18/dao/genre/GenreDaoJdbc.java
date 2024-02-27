package com.example.spring18.dao.genre;

import com.example.spring18.domain.Genre;
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

@RequiredArgsConstructor
@Repository
public class GenreDaoJdbc implements GenreDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final GenreMapper genreMapper = new GenreMapper();

    @Override
    public long save(Genre genre) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", genre.getName());
        KeyHolder kh = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update("insert into genres (name) values (:name)", params, kh, new String[]{"id"});
        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    @Override
    public Genre getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        return namedParameterJdbcOperations.queryForObject(
                "select * from genres where id = :id", params, genreMapper);
    }

    @Override
    public Genre getByName(String name) {
        Map<String, Object> params = Collections.singletonMap("name", name);
        return namedParameterJdbcOperations.queryForObject(
                "select * from genres where name = :name", params, genreMapper);
    }

    @Override
    public List<Genre> getGenresByBookId(long bookId) {
        Map<String, Object> params = Collections.singletonMap("book_id", bookId);
        return namedParameterJdbcOperations.query(
                "select * from genres where id in (select genre_id from books_genres where book_id = :book_id)",
                params, new GenreMapper());
    }

    @Override
    public List<Genre> getAll() {
        return namedParameterJdbcOperations.query("select * from genres", genreMapper);
    }

    @Override
    public void updateById(Genre newGenre) {
        namedParameterJdbcOperations.update("update genres set name = :name where id = :id",
                Map.of("id", newGenre.getId(), "name", newGenre.getName()));
    }

    @Override
    public void delete(long id) {
        namedParameterJdbcOperations.update("delete from genres where id = :id", Map.of("id", id));
    }

    private static class GenreMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Genre.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    }
}
