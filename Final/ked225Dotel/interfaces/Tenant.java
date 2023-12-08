package Final.ked225Dotel.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import Final.ked225Dotel.DatabaseUtil;

public class Tenant {

    public static void tenantLogin(Scanner scnr) {
        System.out.println("You have selected: Tenant");
        
        int tenantId = -1;  // Initialize with an invalid tenant ID
        while (tenantId == -1) {
            System.out.println("Please input your email address for login:");
            String email = scnr.nextLine();
            
            if (isValidEmail(email)) {
                tenantId = authenticateTenantByEmail(email);
                if (tenantId == -1) {
                    System.out.println("Authentication failed. Please try again.");
                }
            } else {
                System.out.println("Invalid email format. Please try again.");
            }
        }

        if (tenantId != -1) {
            System.out.println("Login successful!");
            tenantMenu(scnr, tenantId);  // Pass tenantId to the tenantMenu method
        }
    }

    private static boolean isValidEmail(String email) {
        // Implement a more robust email validation 
        return email.contains("@") && email.contains(".");
    }

    private static int authenticateTenantByEmail(String email) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int tenantId = -1;  // Default to an invalid tenant ID

        try {
            Connection conn = DatabaseUtil.getConnection(); 
            String sql = "SELECT ten_id FROM Tenant WHERE email_addr = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                tenantId = rs.getInt("ten_id");  // Get the tenant ID from the query result
                System.out.println("Authentication successful.");
            } else {
                System.out.println("Authentication failed. Tenant not found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return tenantId;  // Return the tenant ID or -1 if not authenticated
    }

    // Method to display tenant menu 
    public static void tenantMenu(Scanner scanner, int tenantId) {
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
                    checkPaymentStatus(tenantId);
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

    //Method to check the status for a payment 
    private static void checkPaymentStatus(int tenantId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get the database connection
            conn = DatabaseUtil.getConnection();

            // SQL query to check for outstanding payment dues for the tenant
            String sql = "SELECT Lease.lease_id, Lease.rent_amt - NVL(SUM(Payment.amount), 0) amount_due " +
                     "FROM Lease " +
                     "LEFT JOIN Payment ON Lease.lease_id = Payment.lease_id " +
                     "WHERE Lease.ten_id = ? " +
                     "GROUP BY Lease.lease_id, Lease.rent_amt";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tenantId);

            rs = pstmt.executeQuery();

            // Check if there are results and output the payment status
            boolean hasDues = false;
            while (rs.next()) {
                int leaseId = rs.getInt("lease_id");
                double amountDue = rs.getDouble("amount_due");
                if (amountDue > 0) {
                    System.out.println("Lease ID: " + leaseId + " has an outstanding balance of: $" + amountDue);
                    hasDues = true;
                }
            }

            if (!hasDues) {
                System.out.println("No outstanding dues. All payments are up to date.");
            }

        } catch (SQLException e) {
            System.out.println("Database error occurred while checking payment status.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                // Don't close the connection if it's managed elsewhere
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Method to make a rental payment 
    private static void makeRentalPayment(Scanner scanner) {
        // Implement the logic to make a rental payment
        System.out.println("Making a rental payment...");
        // Example: Ask for payment details and process the payment
    }

    //Method to update a tenant's personal data 
    private static void updatePersonalData(Scanner scanner) {
        // Implement the logic to update personal data
        System.out.println("Updating personal data...");
        // Example: Ask for new data (e.g., phone number, email) and update the records
    }
}
