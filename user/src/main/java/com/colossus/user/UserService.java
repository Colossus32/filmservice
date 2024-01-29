package com.colossus.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
public record UserService(UserRepository repository) {

    public boolean isUserCorrectForSaving(UserRegistrationRequest userRegistrationRequest) {
        log.info("{} is checking for correct saving...", userRegistrationRequest);

        if (userRegistrationRequest == null ||
                userRegistrationRequest.getEmail() == null ||
                userRegistrationRequest.getUsername() == null) return false;

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(userRegistrationRequest.getEmail()).matches()) return false;

        if (!isUsernamePatterCorrect(userRegistrationRequest.getUsername())) return false;

        Optional<User> checkEmail = repository.findByEmail(userRegistrationRequest.getEmail());
        Optional<User> checkUsername = repository.findByUsername(userRegistrationRequest.getEmail());

        return checkEmail.isEmpty() && checkUsername.isEmpty();
    }

    public boolean isUsernamePatterCorrect(String username) {
        return username.matches("[a-zA-Z]+");
    }


    public void saveUser(UserRegistrationRequest userRegistrationRequest) {
        log.info("Saving : {}...", userRegistrationRequest);
        repository.save(User.builder()
                .email(userRegistrationRequest.getEmail())
                .username(userRegistrationRequest.getUsername())
                .name(userRegistrationRequest.getName())
                .build());
    }

    public void saveUser(User user) {
        log.info("Saving user: {}...", user);
        repository.save(user);
    }

    public Optional<User> getUserById(long id) {
        log.info("Getting user by ID: {}...", id);
        return repository.findById(id);
    }

    public boolean editUserDetails(long id, UserEditingRequest userEditingRequest) {
        log.info("Editing user with ID: {}, details: {}...", id, userEditingRequest);
        Optional<User> userFromDB = repository.findById(id);
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

        repository.save(editedUser);

        return true;
    }

    public boolean deleteUser(long id) {
        log.info("Deleting user with ID: {}...", id);
        Optional<User> userFromDB = repository.findById(id);
        if (userFromDB.isEmpty()) {
            log.error("Error with deleting user with ID: {}", id);
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
