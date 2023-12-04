package Final.ked225Dotel;
import java.sql.*;

public class DatabaseUtil {
    // Database URL
    private static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";
    // Database credentials
    private static final String USER = "ked225";
    private static final String PASS = "3SnowAcrid";
    // Database connection
    private static Connection connection = null;

    public static void connectToDatabase() {
        try {
            // Attempting to establish a database connection
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection Successful. Rad.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //add more here later 
}
