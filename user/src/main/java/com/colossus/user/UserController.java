package com.colossus.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
public record UserController(UserService service) {
    private final static ResponseEntity<String> INTERNAL_SERVER_ERROR =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"INTERNAL_ERROR\"}");

    @PostMapping
    public ResponseEntity<?> userRegistration(@RequestBody UserRegistrationRequest userRegistrationRequest){
        if (service.isUserCorrectForSaving(userRegistrationRequest)) {
            service.saveUser(userRegistrationRequest);
            return ResponseEntity.ok(null);
        }
        else return INTERNAL_SERVER_ERROR;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@RequestHeader("User-Id") long headerId, @PathVariable("id") long id){
        if (headerId != id) return INTERNAL_SERVER_ERROR;

        Optional<User> userFromDB = service.getUserById(id);
        if (userFromDB.isEmpty()) return INTERNAL_SERVER_ERROR;

        return ResponseEntity.ok(userFromDB.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editUserDetails(@RequestBody UserEditingRequest userEditingRequest,
                                             @RequestHeader("User-Id") long headerId,
                                             @PathVariable("id") long id){

        if (headerId != id) return INTERNAL_SERVER_ERROR;

        if (!service.editUserDetails(id, userEditingRequest)) return INTERNAL_SERVER_ERROR;

        return ResponseEntity.ok(service.getUserById(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("User-Id") long headerId,
                                        @PathVariable("id") long id){

        if (headerId != id || !service.deleteUser(id)) return INTERNAL_SERVER_ERROR;

        return ResponseEntity.ok(null);
    }
}
