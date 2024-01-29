package com.colossus.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE m.id NOT IN (SELECT fm.id FROM User u JOIN u.favoriteMovies fm WHERE u.id = :userId)")
    List<Movie> findMoviesNotInFavorites(@Param("userId") long userId);
}
