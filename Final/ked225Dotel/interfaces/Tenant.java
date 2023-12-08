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

    public static void tenantLogin(Scanner scnr) {
        System.out.println("You have selected: Tenant");
        
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            System.out.println("Please input your email address for login:");
            String email = scnr.nextLine();
            
            if (isValidEmail(email)) {
                isAuthenticated = authenticateTenantByEmail(email);
                if (!isAuthenticated) {
                    System.out.println("Authentication failed. Please try again.");
                }
            } else {
                System.out.println("Invalid email format. Please try again.");
            }
        }
    
        if (isAuthenticated) {
            System.out.println("Login successful!");
            // Proceed with further logic after successful login
            // Add more here 
        }
    }

    private static boolean isValidEmail(String email) {
        // Implement a more robust email validation if needed
        return email.contains("@") && email.contains(".");
    }

    private static boolean authenticateTenantByEmail(String email) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Use the static connection object from DatabaseUtil
            Connection conn = DatabaseUtil.getConnection(); 
            String sql = "SELECT ten_id FROM Tenant WHERE email_addr = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // If there is a result, the email exists in the database.
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
                // Do not close the connection here; it is managed by DatabaseUtil.
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Tenant Menu Method once email has been authenticated 
    public static void tenantMenu(Scanner scanner) {
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("Tenant Menu:");
            System.out.println("1. Check Payment Status");
            System.out.println("2. Make Rental Payment");
            System.out.println("3. Update Personal Data");
            System.out.println("4. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline left-over

            switch (choice) {
                case 1:
                    checkPaymentStatus();
                    break;
                case 2:
                    makeRentalPayment(scanner);
                    break;
                case 3:
                    updatePersonalData(scanner);
                    break;
                case 4:
                    exitMenu = true;
                    System.out.println("Exiting Tenant Menu.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
}
