package com.colossus.user;

import com.colossus.movie.Movie;
import com.colossus.movie.MovieRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private MovieRepository movieRepository;

    public boolean isUserCorrectForSaving(UserRegistrationRequest userRegistrationRequest) {
        log.info("{} is checking for correct saving...", userRegistrationRequest);

        if (userRegistrationRequest == null ||
                userRegistrationRequest.getEmail() == null ||
                userRegistrationRequest.getUsername() == null) return false;

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(userRegistrationRequest.getEmail()).matches()) return false;

        if (!isUsernamePatterCorrect(userRegistrationRequest.getUsername())) return false;

        Optional<User> checkEmail = userRepository.findByEmail(userRegistrationRequest.getEmail());
        Optional<User> checkUsername = userRepository.findByUsername(userRegistrationRequest.getEmail());

        return checkEmail.isEmpty() && checkUsername.isEmpty();
    }

    public boolean isUsernamePatterCorrect(String username) {
        return username.matches("[a-zA-Z]+");
    }

    public void saveUser(UserRegistrationRequest userRegistrationRequest) {
        log.info("Saving : {}...", userRegistrationRequest);
        userRepository.save(User.builder()
                .email(userRegistrationRequest.getEmail())
                .username(userRegistrationRequest.getUsername())
                .name(userRegistrationRequest.getName())
                .build());
    }

    public void saveUser(User user) {
        log.info("Saving user: {}...", user);
        userRepository.save(user);
    }

    public Optional<User> getUserById(long id) {
        log.info("Getting user by ID: {}...", id);
        return userRepository.findById(id);
    }

    public boolean editUserDetails(long id, UserEditingRequest userEditingRequest) {
        log.info("Editing user with ID: {}, details: {}...", id, userEditingRequest);
        Optional<User> userFromDB = userRepository.findById(id);
        if (isUsernamePatterCorrect(userEditingRequest.getUsername()) || userFromDB.isEmpty()) {
            log.error("Error with editing user with ID: {}", id);
            return false;
        }

        User existingUser = userFromDB.get();

        User editedUser = User.builder()
                .id(id)
                .email(existingUser.getEmail())
                .username(userEditingRequest.getUsername())
                .name(userEditingRequest.getName())
                .build();

        userRepository.save(editedUser);

        return true;
    }

    public boolean deleteUser(long id) {
        log.info("Deleting user with ID: {}...", id);
        Optional<User> userFromDB = userRepository.findById(id);
        if (userFromDB.isEmpty()) {
            log.error("Error with deleting user with ID: {}", id);
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    public void removeMovieFromFavorites(long headerId, long movieId) {

        log.info("Deleting movie {} from user {}", movieId, headerId);
        User user = userRepository.findById(headerId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        user.getFavoriteMovies().remove(movie);
        userRepository.save(user);
    }
}
