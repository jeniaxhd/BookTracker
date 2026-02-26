package sk.upjs.paz.service;

import sk.upjs.paz.PasswordHasher;
import sk.upjs.paz.entity.User;

public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public void register(String name, String email, String rawPassword) {
        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalStateException("All fields are required");
        }

        String trimmedName = name.trim();
        String trimmedEmail = email.trim();

        if (userService.getByEmail(trimmedEmail).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }

        User u = new User();
        u.setName(trimmedName);
        u.setEmail(trimmedEmail);
        u.setReadBooks(0);
        u.setPasswordHash(PasswordHasher.hash(rawPassword));

        userService.add(u);
    }

    public User login(String email, String rawPassword) {
        if (email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalStateException("Email and password are required");
        }

        String trimmedEmail = email.trim();

        User u = userService.getByEmail(trimmedEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!PasswordHasher.verify(rawPassword, u.getPasswordHash())) {
            throw new IllegalStateException("Wrong password");
        }

        return u;
    }
}
