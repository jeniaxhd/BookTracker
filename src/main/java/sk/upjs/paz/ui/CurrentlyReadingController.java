package sk.upjs.paz.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.service.ReadingSessionService;
import sk.upjs.paz.service.CurrentlyReadingService;
import sk.upjs.paz.ui.dto.ActiveBookCard;
import sk.upjs.paz.ui.i18n.I18N;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentlyReadingController {

    @FXML private BorderPane root;

    // Sidebar navigation
    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;


    // User / header
    @FXML private Button userProfileButton;
    @FXML private Label userNameLabel;
    @FXML private Label headerTitleLabel;

    // Header actions

    @FXML private ToggleButton themeToggle;
    @FXML private Button startSessionButton;

    // Header icons
    @FXML private ImageView searchIcon;

    @FXML private ImageView themeIcon;

    // Content
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentRoot;

    // Container for cards
    @FXML private VBox cardsBox;

    // Side stats: today's reading
    @FXML private Label todayMinutesLabel;
    @FXML private ProgressBar todayGoalProgressBar;
    @FXML private Label todayGoalSubtitleLabel;

    // Side stats: quick stats
    @FXML private Label booksInProgressValueLabel;
    @FXML private Label pagesThisWeekValueLabel;
    @FXML private Label sessionsThisWeekValueLabel;

    private CurrentlyReadingService currentlyReadingService;
    private ReadingSessionService readingSessionService;
    private List<ActiveBookCard> cachedCards = new ArrayList<>();

    // Icons
    private Image searchLight, searchDark;

    private Image moonIcon, sunIcon;

    private static final int DAILY_GOAL_MINUTES = 60;

    public void setCurrentlyReadingService(CurrentlyReadingService s) {
        this.currentlyReadingService = s;
        refresh(); // safe call after injection
    }

    @FXML
    private void initialize() {
        ToggleGroup navGroup = new ToggleGroup();
        if (dashboardNavButton != null) dashboardNavButton.setToggleGroup(navGroup);
        if (libraryNavButton != null) libraryNavButton.setToggleGroup(navGroup);
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setToggleGroup(navGroup);
        if (statisticsNavButton != null) statisticsNavButton.setToggleGroup(navGroup);


        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setSelected(true);
        if (headerTitleLabel != null) headerTitleLabel.setText("Currently Reading");

        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        searchLight = load("/sk/upjs/paz/ui/img/logoLight/search.png");
        searchDark = load("/sk/upjs/paz/ui/img/logoDark/search.png");
        moonIcon = load("/sk/upjs/paz/ui/img/logoLight/moon.png");
        sunIcon = load("/sk/upjs/paz/ui/img/logoDark/sun.png");

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                updateIconsForTheme();
            }
        });

        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
            updateIconsForTheme();
        }

        // If service was not injected via SceneNavigator, try lazy fallback
        if (currentlyReadingService == null) {
            try {
                currentlyReadingService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getCurrentlyReadingService();
            } catch (Exception ignored) {
                // Service may not exist yet; controller can still work if injected later
            }
        }

        if (readingSessionService == null) {
            try {
                readingSessionService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getReadingSessionService();
            } catch (Exception ignored) {
            }
        }

        refresh();
    }

    public void refresh() {
        if (cardsBox == null) return;
        if (currentlyReadingService == null) return;

        if (!AppState.isLoggedIn()) {
            cardsBox.getChildren().clear();
            cachedCards = new ArrayList<>();
            renderSideStatsEmpty();
            return;
        }

        long userId = AppState.getCurrentUser().getId();

        Task<List<ActiveBookCard>> task = new Task<>() {
            @Override
            protected List<ActiveBookCard> call() {
                return currentlyReadingService.listActiveBooks(userId);
            }
        };

        task.setOnSucceeded(e -> {
            cachedCards = task.getValue() != null ? task.getValue() : new ArrayList<>();
            renderCards(cachedCards);

            // Side stats depend on active books + reading sessions
            refreshSideStats(userId, cachedCards);
        });

        task.setOnFailed(e -> {
            if (task.getException() != null) task.getException().printStackTrace();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderSideStatsEmpty() {
        if (todayMinutesLabel != null) todayMinutesLabel.setText("0 min");
        if (todayGoalProgressBar != null) todayGoalProgressBar.setProgress(0);
        if (todayGoalSubtitleLabel != null) todayGoalSubtitleLabel.setText("0%");

        if (booksInProgressValueLabel != null) booksInProgressValueLabel.setText("0");
        if (pagesThisWeekValueLabel != null) pagesThisWeekValueLabel.setText("0");
        if (sessionsThisWeekValueLabel != null) sessionsThisWeekValueLabel.setText("0");
    }

    private record SideStats(int minutesToday,
                             int dailyGoalMinutes,
                             int booksInProgress,
                             int pagesThisWeek,
                             int sessionsThisWeek) {
    }

    private void refreshSideStats(long userId, List<ActiveBookCard> activeBooks) {
        if (readingSessionService == null) {
            // Still update the “books in progress” number; others stay empty.
            if (booksInProgressValueLabel != null) {
                booksInProgressValueLabel.setText(String.valueOf(activeBooks != null ? activeBooks.size() : 0));
            }
            return;
        }

        final int booksInProgress = activeBooks != null ? activeBooks.size() : 0;

        Task<SideStats> task = new Task<>() {
            @Override
            protected SideStats call() {
                List<ReadingSession> sessions = readingSessionService.getSessionsForUser(userId);
                if (sessions == null) sessions = List.of();

                LocalDate today = LocalDate.now();
                LocalDate weekStart = today.minusDays(6); // last 7 days incl. today

                int minutesToday = 0;
                int sessionsThisWeek = 0;

                // Baselines (latest endPage before weekStart)
                Map<Long, Integer> baselineEndPage = new HashMap<>();
                Map<Long, List<ReadingSession>> sessionsInWeekByBook = new HashMap<>();

                for (ReadingSession s : sessions) {
                    if (s == null || s.getUser() == null || s.getBook() == null) continue;
                    if (s.getUser().getId() == null || s.getBook().getId() == null) continue;

                    LocalDateTime ts = s.getLastTimeRead();
                    if (ts == null) ts = s.getStart();
                    if (ts == null) continue;

                    LocalDate d = ts.toLocalDate();

                    if (d.isEqual(today)) {
                        minutesToday += Math.max(0, s.getDuration());
                    }

                    if (!d.isBefore(weekStart)) {
                        sessionsThisWeek++;
                        sessionsInWeekByBook.computeIfAbsent(s.getBook().getId(), k -> new ArrayList<>()).add(s);
                    } else {
                        // keep baseline as the latest endPage before weekStart
                        long bookId = s.getBook().getId();
                        Integer prev = baselineEndPage.get(bookId);
                        int endPage = Math.max(0, s.getEndPage());
                        if (prev == null) {
                            baselineEndPage.put(bookId, endPage);
                        } else {
                            // choose the bigger endPage as baseline (best-effort approximation)
                            baselineEndPage.put(bookId, Math.max(prev, endPage));
                        }
                    }
                }

                // Compute pages progressed this week from endPage deltas per book.
                int pagesThisWeek = 0;
                for (Map.Entry<Long, List<ReadingSession>> entry : sessionsInWeekByBook.entrySet()) {
                    long bookId = entry.getKey();
                    List<ReadingSession> list = entry.getValue();
                    list.sort(Comparator.comparing(s -> {
                        LocalDateTime ts = s.getLastTimeRead();
                        return ts != null ? ts : (s.getStart() != null ? s.getStart() : LocalDateTime.MIN);
                    }));

                    int prevEnd = baselineEndPage.getOrDefault(bookId, 0);
                    for (ReadingSession s : list) {
                        int curEnd = Math.max(0, s.getEndPage());
                        int delta = curEnd - prevEnd;
                        if (delta > 0) pagesThisWeek += delta;
                        prevEnd = Math.max(prevEnd, curEnd);
                    }
                }

                return new SideStats(minutesToday, DAILY_GOAL_MINUTES, booksInProgress, pagesThisWeek, sessionsThisWeek);
            }
        };

        task.setOnSucceeded(e -> {
            SideStats s = task.getValue();
            if (s == null) {
                renderSideStatsEmpty();
                return;
            }

            // Today minutes
            if (todayMinutesLabel != null) todayMinutesLabel.setText(s.minutesToday + " min");

            // Goal progress
            double progress = s.dailyGoalMinutes > 0 ? Math.min(1.0, (double) s.minutesToday / (double) s.dailyGoalMinutes) : 0.0;
            if (todayGoalProgressBar != null) todayGoalProgressBar.setProgress(progress);

            int pct = s.dailyGoalMinutes > 0 ? (int) Math.round(progress * 100.0) : 0;
            if (todayGoalSubtitleLabel != null) {
                // e.g. "70% of daily goal (60 min)"
                todayGoalSubtitleLabel.setText(I18N.tr("currentlyReading.daily_goal_progress", pct, s.dailyGoalMinutes));
            }

            // Quick stats
            if (booksInProgressValueLabel != null) booksInProgressValueLabel.setText(String.valueOf(s.booksInProgress));
            if (pagesThisWeekValueLabel != null) pagesThisWeekValueLabel.setText(String.valueOf(s.pagesThisWeek));
            if (sessionsThisWeekValueLabel != null) sessionsThisWeekValueLabel.setText(String.valueOf(s.sessionsThisWeek));
        });

        task.setOnFailed(e -> {
            if (task.getException() != null) task.getException().printStackTrace();
            // Still show books-in-progress at least
            if (booksInProgressValueLabel != null) {
                booksInProgressValueLabel.setText(String.valueOf(booksInProgress));
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderCards(List<ActiveBookCard> cards) {
        cardsBox.getChildren().clear();
        for (ActiveBookCard card : cards) {
            cardsBox.getChildren().add(loadCard(card));
        }
    }

    private Node loadCard(ActiveBookCard card) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/cards/activeBookCard.fxml"), I18N.getBundle());
            Node node = loader.load();

            ActiveBookCardController c = loader.getController();
            c.setData(card);
            c.setOnContinue(this::startSessionForCard);
            c.setOnDetails(this::openBookDetailsForCard);

            return node;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load activeBookCard.fxml", ex);
        }
    }

    private void startSessionForCard(ActiveBookCard card) {
        if (card == null) return;
        if (!AppState.isLoggedIn()) return;

        long userId = AppState.getCurrentUser().getId();

        var readingSessionService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getReadingSessionService();
        var userHasBookService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getUserHasBookService();

        int startPage = Math.max(0, card.currentPage());

        // Start session in DB and get generated session id
        var session = readingSessionService.startNewSession(userId, card.bookId(), startPage);
        long sessionId = session.getId();

        // Ensure user-book state is READING
        userHasBookService.upsert(userId, card.bookId(), sk.upjs.paz.enums.BookState.READING);

        String subtitle = card.authorsText() + " · " + card.genreName();

        // Show session bar with context so end-session can update DB
        SceneNavigator.startSessionBar(userId, card.bookId(), sessionId, card.title(), subtitle, "READING");

        // Optional: refresh cards so the state/progress updates immediately
        refresh();
    }


    private void openBookDetailsForCard(ActiveBookCard card) {
        if (root == null || root.getScene() == null) return;

        // Open the details modal by bookId (new API)
        SceneNavigator.showBookDetailsModal(root.getScene().getWindow(), card.bookId());
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    // ===== Theme =====

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
        }
        updateIconsForTheme();
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeToggle != null) themeToggle.setSelected(dark);
        if (searchIcon != null) searchIcon.setImage(dark ? searchDark : searchLight);

        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);
    }

    // ===== Navigation =====

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        SceneNavigator.showDashboard();
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        SceneNavigator.showLibrary();
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setSelected(true);
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        SceneNavigator.showStatistics();
    }

    // ===== Header actions =====

    @FXML
    private void onSearch(ActionEvent event) {
        // later
    }


    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }

    @FXML
    private void onStartSession(ActionEvent event) {
        if (cachedCards == null || cachedCards.isEmpty()) return;
        startSessionForCard(cachedCards.get(0));
    }

    @FXML
    private void onAddAnotherBook(ActionEvent event) {
        // Use navigator so services (BookService, GenreService, UserHasBookService) are injected correctly
        if (root != null && root.getScene() != null) {
            SceneNavigator.showAddBookModal(root.getScene().getWindow());
            refresh(); // reload cards after modal closes
        }
    }

}
