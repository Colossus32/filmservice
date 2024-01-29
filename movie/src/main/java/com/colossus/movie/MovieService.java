package com.colossus.movie;

import com.colossus.user.User;
import com.colossus.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class MovieService {

    private MovieRepository movieRepository;
    private UserRepository userRepository;

    public Page<Movie> getMovies(Pageable pageable) {
        log.info("Getting movies by pagination - page:{}, page size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        if (pageable.isUnpaged()) {
            return movieRepository.findAll(PageRequest.of(0,15));
        }

        return movieRepository.findAll(pageable);
    }

    public User addMovieToFavorites(long headerId, long movieId) {
        log.info("Adding movie {} to user {}", movieId, headerId);
        User user = userRepository.findById(headerId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        user.getFavoriteMovies().add(movie);
        return userRepository.save(user);
    }

    public List<Movie> discoverMovies(long headerId, String loaderType) {

        User user = userRepository.findById(headerId).orElseThrow();

        return switch (LoaderType.valueOf(loaderType)) {
            case LoaderType.sql -> findMoviesNotInFavoritesBySql(headerId);
            case LoaderType.inMemory -> findMoviesNotInFavoritesByInMemory(headerId);
        };
    }

    private List<Movie> findMoviesNotInFavoritesByInMemory(long headerId) {

        User user = userRepository.findById(headerId).orElseThrow();

        Set<Long> favoriteMoviesIds = user.getFavoriteMovies().stream()
                .map(Movie::getId)
                .collect(Collectors.toSet());
        List<Movie> allMovies = movieRepository.findAll();

        return allMovies.stream()
                .filter(movie -> !favoriteMoviesIds.contains(movie.getId()))
                .collect(Collectors.toList());
    }

    private List<Movie> findMoviesNotInFavoritesBySql(long headerId) {
        return movieRepository.findMoviesNotInFavorites(headerId);
    }


}
