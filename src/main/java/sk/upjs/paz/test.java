package sk.upjs.paz;
import java.sql.*;

public class test {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3307/booktracker?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection c = DriverManager.getConnection(url, "app", "BookTrackerYevhenVadym2025");
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT DATABASE()")) {
            rs.next();
            System.out.println("Connected to: " + rs.getString(1));
        }
    }
}
