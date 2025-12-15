package sk.upjs.paz.ui;

import sk.upjs.paz.entity.User;

import java.time.LocalDateTime;

public final class AppState {
    private static User currentUser = new User(1L, "Robert", "hh11@gmail.com", LocalDateTime.now(), 1);


    private AppState() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
