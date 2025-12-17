package sk.upjs.paz.ui;

import sk.upjs.paz.entity.User;

public final class AppState {
    private static User currentUser; // null by default

    private AppState() {}

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
