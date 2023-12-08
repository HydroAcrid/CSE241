package Final.ked225Dotel.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import Final.ked225Dotel.DatabaseUtil;

public class Tenant {
    
    public void checkPaymentStatus() {
        // Implementation code
    }

    public void makeRentalPayment() {
        // Implementation code
    }

    public void updatePersonalData() {
        // Implementation code
    }

    public static boolean tenantLogin(Scanner scnr) {
        System.out.println("You have selected: Tenant");
        System.out.println("Please input your email address for login:");
        String email = scanner.nextLine();
        
        if (isValidEmail(email)) {
            return authenticateTenantByEmail(email);
        } else {
            System.out.println("Invalid email format. Please try again.");
            return false;
        }
    }

    private static boolean isValidEmail(String email) {
        // Implement a more robust email validation if needed
        return email.contains("@") && email.contains(".");
    }

    private static boolean authenticateTenantByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getDBConnection(); // Make sure DatabaseUtil provides this method.
            String sql = "SELECT ten_id FROM Tenant WHERE email_addr = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // If there is a result, the email exists in the database.
                // Additional steps could be added here if necessary, e.g., setting user session data.
                System.out.println("Authentication successful.");
                return true;
            } else {
                System.out.println("Authentication failed. Tenant not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Additional methods related to Tenant functionality
}
