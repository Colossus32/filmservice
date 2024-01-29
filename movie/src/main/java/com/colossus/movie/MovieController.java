package com.colossus.movie;

import com.colossus.user.User;
import com.colossus.user.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
@AllArgsConstructor
@RequiredArgsConstructor
public class MovieController {

    private MovieService movieService;
    private UserService userService;

    private final static ResponseEntity<String> INTERNAL_SERVER_ERROR =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"INTERNAL_ERROR\"}");

    @GetMapping
    public ResponseEntity<Page<Movie>> getAllMovies(Pageable pageable){

        return ResponseEntity.ok(movieService.getMovies(pageable));
    }

    @PostMapping("/favorite/{movieId}")
    public ResponseEntity<?> addMovieToFavorites(
            @RequestHeader("User-Id") long headerId,
            @PathVariable long movieId) {

        User response = movieService.addMovieToFavorites(headerId, movieId);
        if (response == null) return INTERNAL_SERVER_ERROR;
        return ResponseEntity.ok(String.format("Movie %d added to favorites", movieId));
    }

    @DeleteMapping("/favorite/{movieId}")
    public ResponseEntity<?> removeMovieFromFavorites(
            @RequestHeader("User-Id") long headerId,
            @PathVariable long movieId) {

        userService.removeMovieFromFavorites(headerId, movieId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/discovery")
    public ResponseEntity<?> discoveryNotInFavoritesMovies(
            @RequestHeader("User-Id") long headerId,
            @RequestParam(defaultValue = "sql") String loaderType){

        List<Movie> discoveredMovies = movieService.discoverMovies(headerId, loaderType);
        return ResponseEntity.ok(discoveredMovies);
    }


}
