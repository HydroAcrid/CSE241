package Final.ked225Dotel.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    //THIS NEEDS TO BE UPDATEDDDDDD 
    private static void makeRentalPayment(Scanner scnr, int tenantId) {
        System.out.println("Making a rental payment...");
    
        // Ask for the lease ID and the amount to pay
        System.out.print("Enter Lease ID: ");
        int leaseId = scnr.nextInt();
        scanner.nextLine(); // Consume the newline left-over
    
        System.out.print("Enter payment amount: ");
        double amount = scnr.nextDouble();
        scanner.nextLine(); // Consume the newline left-over
    
        // This is where you would collect payment method details.
        // For simplicity, let's assume the tenant is paying with a saved payment method, and you have a paymentMethodId.
        int paymentMethodId = getPaymentMethodId(tenantId, scnr); // Implement this method as needed.
    
        // Insert payment into the database
        Connection conn = null;
        PreparedStatement pstmt = null;
    
        try {
            conn = DatabaseUtil.getConnection();
    
            // Start a transaction
            conn.setAutoCommit(false);
    
            String sql = "INSERT INTO Payment (pay_id, pay_date, amount, payment_method_id, lease_id) " +
                         "VALUES (payment_seq.NEXTVAL, CURRENT_DATE, ?, ?, ?)";
    
            pstmt = conn.prepareStatement(sql);
    
            // Set parameters for the payment
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, paymentMethodId);
            pstmt.setInt(3, leaseId);
    
            // Execute the payment insert
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1) {
                // Commit the transaction if the insert was successful
                conn.commit();
                System.out.println("Payment was successful.");
            } else {
                // Rollback the transaction if the insert failed
                conn.rollback();
                System.out.println("Payment failed to process.");
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            System.out.println("Database error occurred while processing payment.");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true); // Reset to default
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static int getPaymentMethodId(Scanner scanner, int tenantId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int paymentMethodId = -1;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT pay_id, method_name FROM PaymentMethod WHERE ten_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tenantId);

            rs = pstmt.executeQuery();
            
            List<Integer> validIds = new ArrayList<>();
            System.out.println("Available payment methods:");
            while (rs.next()) {
                int id = rs.getInt("pay_id");
                String methodName = rs.getString("method_name");
                validIds.add(id);
                System.out.println(id + ": " + methodName);
            }

            int inputId;
            do {
                System.out.print("Enter the ID of the payment method you wish to use: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("That's not a valid ID. Please enter a number.");
                    scanner.next(); // discard non-integer input
                    System.out.print("Enter the ID of the payment method you wish to use: ");
                }
                inputId = scanner.nextInt();
                scanner.nextLine(); // Consume the newline left-over

                if (validIds.contains(inputId)) {
                    paymentMethodId = inputId;
                } else {
                    System.out.println("Invalid payment method selected. Please try again.");
                }
            } while (paymentMethodId == -1);

        } catch (SQLException e) {
            System.out.println("Database error occurred while retrieving payment methods.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return paymentMethodId;
    }


    //Method to make sure the payment you're selecting matches your payment type in the database 
    private static boolean isValidPaymentMethodId(int paymentMethodId, int tenantId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValid = false;
    
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM (" +
                         "SELECT pay_id FROM DebitCard WHERE ten_id = ? AND pay_id = ? " +
                         "UNION ALL " +
                         "SELECT pay_id FROM CreditCard WHERE ten_id = ? AND pay_id = ? " +
                         "UNION ALL " +
                         "SELECT pay_id FROM BankTransfer WHERE ten_id = ? AND pay_id = ?" +
                         ")";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tenantId);
            pstmt.setInt(2, paymentMethodId);
            pstmt.setInt(3, tenantId);
            pstmt.setInt(4, paymentMethodId);
            pstmt.setInt(5, tenantId);
            pstmt.setInt(6, paymentMethodId);
    
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                isValid = true;
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
    
        return isValid;
    }
    
    
    //Method to update a tenant's personal data 
    private static void updatePersonalData(Scanner scanner) {
        // Implement the logic to update personal data
        System.out.println("Updating personal data...");
        // Example: Ask for new data (e.g., phone number, email) and update the records
    }
}
