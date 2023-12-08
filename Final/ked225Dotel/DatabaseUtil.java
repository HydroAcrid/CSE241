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

    //Method to connect to the database
    public static void connectToDatabase() {
        try {
            // Attempting to establish a database connection
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection Successful. Rad.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get the current database connection
    public static Connection getConnection() {
        return connection;
    }

    //Method to disconnect from the database 
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

    // Method to ensure phonumbers are valid:
    public static boolean isValidPhoneNumber(String phoneNumber) {
        //This regex will validate a phone number of the format 'XXX-XXX-XXXX'
        return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}");
    }

    //Method to check that the email is in the correct format 
    public static boolean isValidEmail(String email) {
        return email.matches("\\S+@\\S+\\.\\S+");
    }

    // In DatabaseUtil class
    public static boolean emailExists(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM Tenant WHERE email_addr = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return exists;
    }


    
}
