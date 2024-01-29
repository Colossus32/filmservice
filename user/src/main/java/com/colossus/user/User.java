package com.colossus.user;

import com.colossus.movie.Movie;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@Table(name = "users")
public class User {

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_movies",
            joinColumns = @JoinColumn (name = "user_id"),
            inverseJoinColumns = @JoinColumn (name = "movie_id")
    )
    private Set<Movie> favoriteMovies = new HashSet<>();
}
