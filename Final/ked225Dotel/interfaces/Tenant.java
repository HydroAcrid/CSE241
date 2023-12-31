package Final.ked225Dotel.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Final.ked225Dotel.DatabaseUtil;
import Final.ked225Dotel.Menu;

public class Tenant {

    //Login for tenant 
    public static void tenantLogin(Scanner scnr) {
        System.out.println("You have selected: Tenant");
        
        int tenantId = -1;  // Initialize with an invalid tenant ID
        while (tenantId == -1) {
            System.out.println("Please input your email address for login:");
            String email = scnr.nextLine();
            
            if (DatabaseUtil.isValidEmail(email)) {
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

    //Makes sure the email is valid 
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
    public static void tenantMenu(Scanner scnr, int tenantId) {
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("Tenant Menu:");
            System.out.println("1. Check Payment Status");
            System.out.println("2. Make Rental Payment");
            System.out.println("3. Update Personal Data");
            System.out.println("4. Back to Interfaces");

            System.out.print("Choose an option: ");
            int choice = scnr.nextInt();
            scnr.nextLine();  // Consume the newline left-over

            switch (choice) {
                case 1:
                    checkPaymentStatus(tenantId);
                    break;
                case 2:
                    makeRentalPayment(scnr, tenantId);
                    break;
                case 3:
                    updatePersonalData(scnr, tenantId);
                    break;
                case 4:
                    exitMenu = true;
                    System.out.println("Exiting Tenant Menu.");
                    Menu.displayMainMenu(scnr);
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

    // Method to make a rental payment
    private static void makeRentalPayment(Scanner scnr, int tenantId) {
        System.out.println("Making a rental payment...");

        int leaseId = -1;
        double rentAmount = -1;
        double amount = 0;

        // Loop until a valid lease ID is entered and the lease exists
        while (true) {
            System.out.print("Enter Lease ID: ");
            if (scnr.hasNextInt()) {
                leaseId = scnr.nextInt();
                scnr.nextLine(); // Consume the newline left-over

                if (DatabaseUtil.checkLeaseIdExists(leaseId)) {
                    rentAmount = getRentAmount(leaseId);
                    if (rentAmount <= 0) {
                        System.out.println("Invalid Lease ID or no rent amount found. Please try again.");
                        continue;
                    }
                    break; // Break the loop if a valid lease ID is entered
                } else {
                    System.out.println("No such lease found. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a numeric lease ID.");
                scnr.next(); // discard non-integer input
            }
        }

        // Loop until a valid payment amount is entered
        while (true) {
            System.out.print("Enter payment amount: ");
            if (scnr.hasNextDouble()) {
                amount = scnr.nextDouble();
                scnr.nextLine(); // Consume the newline left-over

                if (amount <= 0 || amount > rentAmount) {
                    System.out.println("The payment amount must be greater than $0 and not more than the rent amount of $" + rentAmount + ". Please try again.");
                } else {
                    break; // Break the loop if a valid payment amount is entered
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); // discard non-double input
            }
        }

        // Insert payment into the database
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseUtil.getConnection();

            // Start a transaction
            conn.setAutoCommit(false);

            String sql = "INSERT INTO Payment (pay_id, pay_date, amount, lease_id) " +
                        "VALUES (payment_seq.NEXTVAL, CURRENT_DATE, ?, ?)";

            pstmt = conn.prepareStatement(sql);

            // Set parameters for the payment
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, leaseId);

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
            } 
            catch (SQLException se) {
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

    // Helper method to get the rent amount for a lease
    private static double getRentAmount(int leaseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double rentAmount = -1;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT rent_amt FROM Lease WHERE lease_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, leaseId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                rentAmount = rs.getDouble("rent_amt");
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

        return rentAmount;
    }


    //Method to make a rental payment
    //THIS NEEDS TO BE UPDATEDDDDDD 
    private static void makeRentalPaymentOLD(Scanner scnr, int tenantId) {
        System.out.println("Making a rental payment...");
    
        // Ask for the lease ID and the amount to pay
        System.out.print("Enter Lease ID: ");
        int leaseId = scnr.nextInt();
        scnr.nextLine(); // Consume the newline left-over
    
        System.out.print("Enter payment amount: ");
        double amount = scnr.nextDouble();
        scnr.nextLine(); // Consume the newline left-over
    
        // This is where you would collect payment method details.
        // For simplicity, let's assume the tenant is paying with a saved payment method, and you have a paymentMethodId.
        int paymentMethodId = getPaymentMethodId(scnr, tenantId); // Implement this method as needed.
    
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
            } 
            catch (SQLException se) {
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

    // Method to get the type of payment method 
    private static int getPaymentMethodId(Scanner scnr, int tenantId) {
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
                while (!scnr.hasNextInt()) {
                    System.out.println("That's not a valid ID. Please enter a number.");
                    scnr.next(); // discard non-integer input
                    System.out.print("Enter the ID of the payment method you wish to use: ");
                }
                inputId = scnr.nextInt();
                scnr.nextLine(); // Consume the newline left-over

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
    
    
    // Method to update a tenant's personal data. Specifically, their phone number or email
    private static void updatePersonalData(Scanner scnr, int tenantId) {
        System.out.println("Updating personal data...");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction block

            // Fetch current data
            String fetchSql = "SELECT email_addr, phone_num FROM Tenant WHERE ten_id = ?";
            pstmt = conn.prepareStatement(fetchSql);
            pstmt.setInt(1, tenantId);
            rs = pstmt.executeQuery();

            String currentEmail = "";
            String currentPhone = "";
            if (rs.next()) {
                currentEmail = rs.getString("email_addr");
                currentPhone = rs.getString("phone_num");
            }
            rs.close();
            pstmt.close();

            // Ask for new phone number
            String newPhone;
            do {
                System.out.print("Enter your new phone number (or press enter to keep [" + currentPhone + "]): ");
                newPhone = scnr.nextLine();
                if (!newPhone.isEmpty() && !DatabaseUtil.isValidPhoneNumber(newPhone)) {
                    System.out.println("Invalid phone format. Please try again.");
                    continue;
                }
                newPhone = newPhone.isEmpty() ? currentPhone : newPhone;
                break;
            } while (true);

            // Ask for new email
            String newEmail;
            do {
                System.out.print("Enter your new email (or press enter to keep [" + currentEmail + "]): ");
                newEmail = scnr.nextLine();
                if (!newEmail.isEmpty() && !DatabaseUtil.isValidEmail(newEmail)) {
                    System.out.println("Invalid email format. Please try again.");
                    continue;
                }
                newEmail = newEmail.isEmpty() ? currentEmail : newEmail;
                break;
            } while (true);

            // Update tenant's information
            String updateSql = "UPDATE Tenant SET phone_num = ?, email_addr = ? WHERE ten_id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newPhone);
            pstmt.setString(2, newEmail);
            pstmt.setInt(3, tenantId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Your personal data has been updated successfully.");
                conn.commit(); // Commit the transaction
            } else {
                System.out.println("No changes were made to your personal data.");
                conn.rollback(); // Rollback any changes
            }

        } catch (SQLException e) {
            System.out.println("Error occurred while updating personal data.");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback the transaction on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
                if (pstmt != null && !pstmt.isClosed()) pstmt.close();
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    
    
    
    
}
