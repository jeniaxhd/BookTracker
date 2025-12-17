        package sk.upjs.paz.ui;

        import javafx.fxml.FXMLLoader;
        import javafx.geometry.Bounds;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.layout.BorderPane;
        import javafx.stage.Modality;
        import javafx.stage.Popup;
        import javafx.stage.Stage;
        import javafx.stage.Window;
        import sk.upjs.paz.ui.i18n.I18N;

        import java.io.IOException;
        import java.net.URL;

        public final class SceneNavigator {

            private static Stage primaryStage;

            // Remember last shown page root (for session bar attach)
            private static BorderPane currentPageRoot;

            // Notifications popup (reused)
            private static Popup notificationsPopup;
            private static Parent notificationsRoot;
            private static NotificationsPopoverController notificationsController;
            private static String loginPrefillEmail;
            private static String currentFxmlPath;
            private static String currentTitleKey;

            private SceneNavigator() {
            }

            public static void init(Stage stage) {
                primaryStage = stage;
                I18N.localeProperty().addListener((obs, oldL, newL) -> reloadCurrentPage());
                showDashboard();
                primaryStage.show();

            }

            private static void reloadCurrentPage() {
                if (primaryStage == null || primaryStage.getScene() == null) return;

                clearNotificationsCache();

                if (currentFxmlPath != null && currentTitleKey != null) {
                    if (currentFxmlPath.endsWith("/login.fxml")) {
                        showLogin(loginPrefillEmail);
                    } else {
                        setPage(currentFxmlPath, currentTitleKey);
                    }
                }

                if (SessionBarHost.isVisible() && SessionBarHost.getController() != null) {
                    SessionBarHost.getController().refreshI18n();
                }
            }

            public static void refresh() {
                reloadCurrentPage();
            }

            private static FXMLLoader createLoader(String fxmlPath) {
                URL url = SceneNavigator.class.getResource(fxmlPath);
                if (url == null) throw new IllegalStateException("Missing FXML: " + fxmlPath);
                return new FXMLLoader(url, I18N.getBundle());
            }

            private static void clearNotificationsCache() {
                if (notificationsPopup != null) notificationsPopup.hide();
                notificationsPopup = null;
                notificationsRoot = null;
                notificationsController = null;
            }



            public static Stage getPrimaryStage() {
                return primaryStage;
            }

            public static Window getWindow() {
                return primaryStage;
            }

            // ===== Pages =====

            public static void showDashboard() {
                setPage("/sk/upjs/paz/ui/dashboard.fxml", "nav.dashboard");
            }

            public static void showLibrary() {
                setPage("/sk/upjs/paz/ui/library.fxml", "nav.library");
            }

            public static void showCurrentlyReading() {
                setPage("/sk/upjs/paz/ui/currentlyReading.fxml", "nav.currentlyReading");
            }

            public static void showStatistics() {
                setPage("/sk/upjs/paz/ui/statistics.fxml", "nav.statistics");
            }

            public static void showSettings() {
                setPage("/sk/upjs/paz/ui/settings.fxml", "nav.settings");
            }

            public static void showUserProfile() {
                setPage("/sk/upjs/paz/ui/userProfile.fxml", "nav.profile");
            }

            public static void showLogin() {
                setPage("/sk/upjs/paz/ui/login.fxml", "nav.login");
            }

            public static void showRegister() {
                setPage("/sk/upjs/paz/ui/register.fxml", "nav.register");
            }



            private static void setPage(String fxmlPath, String title) {
                    currentFxmlPath = fxmlPath;
                    currentTitleKey = title;

                    Parent root = loadRoot(fxmlPath);

                    Scene scene;
                    if (primaryStage.getScene() == null) {
                        scene = new Scene(root);
                        primaryStage.setScene(scene);
                    } else {
                        scene = primaryStage.getScene();
                        scene.setRoot(root);
                    }

                    primaryStage.setTitle(I18N.tr(title));

                    ThemeManager.apply(scene);
                    applyWindowSizeFor(fxmlPath);

                    primaryStage.sizeToScene();
                    primaryStage.centerOnScreen();

                    attachSessionBarIfPossible(root);

                    syncFloatingOverlaysTheme();
                }



            // ===== Session Bar =====

            public static void attachSessionBarIfPossible(Parent pageRootNode) {
                BorderPane bp = findBorderPaneRoot(pageRootNode);
                if (bp != null) {
                    currentPageRoot = bp;
                    SessionBarHost.attach(bp);
                }
            }

            public static void startSessionBar(String title, String subtitle, String status) {
                if (currentPageRoot == null) return;
                SessionBarHost.show(currentPageRoot, title, subtitle, status);
            }

            public static void stopSessionBar() {
                SessionBarHost.hide();
            }

            private static BorderPane findBorderPaneRoot(Parent root) {
                if (root instanceof BorderPane bp) return bp;
                var node = root.lookup("#root");
                if (node instanceof BorderPane bp) return bp;
                return null;
            }

            // ===== Notifications Popover =====

            public static void toggleNotifications(Button anchorButton) {
                if (anchorButton == null || primaryStage == null) return;

                ensureNotificationsLoaded();

                if (notificationsPopup.isShowing()) {
                    notificationsPopup.hide();
                    return;
                }

                syncFloatingOverlaysTheme();

                Bounds b = anchorButton.localToScreen(anchorButton.getBoundsInLocal());
                if (b == null) return;

                double prefW = 360.0;
                double x = b.getMaxX() - prefW;
                double y = b.getMaxY() + 8;

                notificationsPopup.show(primaryStage, x, y);
            }

            public static void syncFloatingOverlaysTheme() {
                // Make popup follow current theme (it is NOT in the main Scene stylesheets)
                if (notificationsRoot != null) {
                    notificationsRoot.getStylesheets().clear();
                    String css = ThemeManager.isDarkMode() ? "/css/darkTheme.css" : "/css/lightTheme.css";
                    URL url = SceneNavigator.class.getResource(css);
                    if (url != null) notificationsRoot.getStylesheets().add(url.toExternalForm());
                }
            }

            private static void ensureNotificationsLoaded() {
                if (notificationsPopup != null && notificationsRoot != null) return;

                notificationsPopup = new Popup();
                notificationsPopup.setAutoHide(true);

                try {
                    URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/notificationsPopover.fxml");
                    if (url == null) throw new IllegalStateException("Missing FXML: /fxml/notificationsPopover.fxml");

                    FXMLLoader loader = new FXMLLoader(url, I18N.getBundle());
                    notificationsRoot = loader.load();
                    notificationsController = loader.getController();
                    notificationsController.setPopup(notificationsPopup);

                    notificationsPopup.getContent().clear();
                    notificationsPopup.getContent().add(notificationsRoot);

                } catch (IOException e) {
                    throw new RuntimeException("Cannot load notificationsPopover.fxml", e);
                }
            }

            // ===== Modals =====

            public static void showAddBookModal(Window owner) {
                try {
                    URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/addBookModal.fxml");
                    if (url == null) throw new IllegalStateException("Missing FXML: /sk/upjs/paz/ui/addBookModal.fxml");

                    FXMLLoader loader = new FXMLLoader(url, I18N.getBundle());
                    Parent dialogRoot = loader.load();

                    AddBookModalController controller = loader.getController();
                    controller.setBookService(sk.upjs.paz.service.ServiceFactory.INSTANCE.getBookService());
                    controller.setGenreService(sk.upjs.paz.service.ServiceFactory.INSTANCE.getGenreService());
                    controller.setUserHasBookService(sk.upjs.paz.service.ServiceFactory.INSTANCE.getUserHasBookService());

                    if (!AppState.isLoggedIn()) {
                        throw new IllegalStateException("No logged-in user");
                    }
                    controller.setCurrentUserId(AppState.getCurrentUser().getId());


                    controller.initData();

                    Scene dialogScene = new Scene(dialogRoot);
                    ThemeManager.apply(dialogScene);

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle(I18N.tr("common.addBook"));
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(owner);
                    dialogStage.setScene(dialogScene);
                    dialogStage.showAndWait();

                } catch (IOException e) {
                    throw new RuntimeException("Cannot open addBookModal.fxml", e);
                }
            }



            public static BookDetailsModalController showBookDetailsModal(Window owner, String title) {
                try {
                    URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/bookDetailsModal.fxml");
                    if (url == null) throw new IllegalStateException("Missing FXML: /fxml/bookDetailsModal.fxml");

                    FXMLLoader loader = new FXMLLoader(url, I18N.getBundle());
                    Parent dialogRoot = loader.load();
                    BookDetailsModalController controller = loader.getController();

                    Scene dialogScene = new Scene(dialogRoot);
                    ThemeManager.apply(dialogScene);

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle(title != null ? title :  I18N.tr("book.details"));
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(owner);
                    dialogStage.setScene(dialogScene);
                    dialogStage.showAndWait();

                    return controller;
                } catch (IOException e) {
                    throw new RuntimeException("Cannot open bookDetailsModal.fxml", e);
                }
            }

            public static EndSessionController showEndSessionModal(
                    Window owner,
                    String bookTitle,
                    String bookMeta,
                    String statusText,
                    String currentPageText,
                    String startTime,
                    String durationMinutes,
                    String startPage,
                    String endPage
            ) {
                try {
                    URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/endSession.fxml");
                    if (url == null) throw new IllegalStateException("Missing FXML: /fxml/endSession.fxml");

                    FXMLLoader loader = new FXMLLoader(url, I18N.getBundle());
                    Parent dialogRoot = loader.load();
                    EndSessionController controller = loader.getController();

                    controller.setInitialData(
                            bookTitle,
                            bookMeta,
                            statusText,
                            currentPageText,
                            startTime,
                            durationMinutes,
                            startPage,
                            endPage
                    );

                    Scene dialogScene = new Scene(dialogRoot);
                    ThemeManager.apply(dialogScene);

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle(I18N.tr("session.endSession"));
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(owner);
                    dialogStage.setScene(dialogScene);
                    dialogStage.showAndWait();

                    return controller;
                } catch (IOException e) {
                    throw new RuntimeException("Cannot open endSession.fxml", e);
                }
            }

            private static void openModal(String fxmlPath, String title, Window owner) {
                try {
                    URL url = SceneNavigator.class.getResource(fxmlPath);
                    if (url == null) throw new IllegalStateException("Missing FXML: " + fxmlPath);

                    FXMLLoader loader = new FXMLLoader(url, I18N.getBundle());
                    Parent dialogRoot = loader.load();

                    Scene dialogScene = new Scene(dialogRoot);
                    ThemeManager.apply(dialogScene);

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle(title);
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(owner);
                    dialogStage.setScene(dialogScene);
                    dialogStage.showAndWait();

                } catch (IOException e) {
                    throw new RuntimeException("Cannot open modal: " + fxmlPath, e);
                }
            }

            private static Parent loadRoot(String fxmlPath) {
                try {
                    FXMLLoader loader = createLoader(fxmlPath);
                    return loader.load();
                } catch (IOException e) {
                    throw new RuntimeException("Cannot load: " + fxmlPath, e);
                }
            }

            private static void show(Stage stage, Parent root) {
                Scene scene = new Scene(root);
                stage.setScene(scene);

                stage.sizeToScene();
                stage.centerOnScreen();

                stage.setMinWidth(1100);
                stage.setMinHeight(700);

                stage.show();
            }

            public static void showLogin(String prefillEmail) {
                loginPrefillEmail = prefillEmail;
                currentFxmlPath = "/sk/upjs/paz/ui/login.fxml";
                currentTitleKey = "page.login";

                try {
                    FXMLLoader loader = createLoader(currentFxmlPath);
                    Parent root = loader.load();

                    LoginController c = loader.getController();
                    if (prefillEmail != null) c.prefillEmail(prefillEmail);

                    Scene scene = primaryStage.getScene();
                    if (scene == null) primaryStage.setScene(new Scene(root));
                    else scene.setRoot(root);

                    primaryStage.setTitle(I18N.tr(currentTitleKey));
                    ThemeManager.apply(primaryStage.getScene());
                    attachSessionBarIfPossible(root);
                    syncFloatingOverlaysTheme();
                    applyWindowSizeFor(currentFxmlPath);

                } catch (IOException e) {
                    throw new RuntimeException("Cannot load login.fxml", e);
                }
            }


            private static void applyWindowSizeFor(String fxmlPath) {
                if (primaryStage == null) return;

                boolean login = fxmlPath.endsWith("/login.fxml");
                boolean register = fxmlPath.endsWith("/register.fxml");

                if (login) {
                    // smaller window for login
                    primaryStage.setMinWidth(820);
                    primaryStage.setMinHeight(520);
                    primaryStage.setWidth(860);
                    primaryStage.setHeight(540);

                } else if (register) {
                    // bigger window for register
                    primaryStage.setMinWidth(940);
                    primaryStage.setMinHeight(640);
                    primaryStage.setWidth(980);
                    primaryStage.setHeight(720);

                } else {
                    // main app pages
                    primaryStage.setMinWidth(1200);
                    primaryStage.setMinHeight(700);
                }
            }


        }

