package Final.ked225Dotel.interfaces;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Final.ked225Dotel.DatabaseUtil;

public class PropertyManager {

    // Method to display tenant menu 
    public static void propertyManagerMenu(Scanner scnr) {
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("Property Manager Menu:");
            System.out.println("1. Add person to lease");
            System.out.println("2. Add pet to lease");
            System.out.println("3. View all visitors");
            System.out.println("4. View all leases");
            System.out.println("5. View move-out dates");
            System.out.println("6. Set move-out date");
            System.out.println("7. Exit");


            System.out.print("Choose an option: ");
            int choice = scnr.nextInt();
            scnr.nextLine();  // Consume the newline left-over

            switch (choice) {
                case 1:
                    addPersonToLease(scnr);
                    break;
                case 2:
                    addPetToLease(scnr);
                    break;
                case 3:
                    recordVisitData();
                    break;
                case 4:
                    recordLeaseData();
                    break;
                case 5:
                    recordMoveOutDate();
                    break;
                case 6:
                    setMoveOutDate();
                    break;
                case 7:
                    exitMenu = true;
                    System.out.println("Exiting Tenant Menu.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private static void recordVisitData() {
        // Implementation code
    }

    public static void recordLeaseData() {
        // Implementation code
    }

    public static void addPersonToLease(Scanner scnr) {
        System.out.println("Adding a new tenant to the lease...");

        // Get tenant information
        System.out.print("Enter tenant's name: ");
        String tenantName = scnr.nextLine();

        // Input and validation for tenant's phone number
        String phoneNumber;
        do {
            System.out.print("Enter tenant's phone number (format XXX-XXX-XXXX): ");
            phoneNumber = scnr.nextLine();
            if (!DatabaseUtil.isValidPhoneNumber(phoneNumber)) {
                System.out.println("Invalid phone format. Please try again.");
            }
        } while (!DatabaseUtil.isValidPhoneNumber(phoneNumber));

        // Input and validation for tenant's email address
        String emailAddr;
        do {
            System.out.print("Enter tenant's email address: ");
            emailAddr = scnr.nextLine();
            if (!DatabaseUtil.isValidEmail(emailAddr)) {
                System.out.println("Invalid email format. Please try again.");
            } else if (DatabaseUtil.emailExists(emailAddr)) {
                System.out.println("Email already exists. Please enter a different email address.");
                emailAddr = ""; // Reset emailAddr to continue the loop
            }
        } while (!DatabaseUtil.isValidEmail(emailAddr) || DatabaseUtil.emailExists(emailAddr));

        // Insert new tenant into the database
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Tenant (name, phone_num, email_addr) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, tenantName);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, emailAddr);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Tenant successfully added to the lease.");
            } else {
                System.out.println("Failed to add tenant to the lease.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while adding tenant to the lease.");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    

    private static void addPetToLease(Scanner scanner) {
        System.out.println("Add a Pet to a Lease");

        // Get pet details from user
        System.out.print("Enter Pet's Name: ");
        String petName = scanner.nextLine();

        int tenantId = -1;
        boolean validTenantId = false;

        while (!validTenantId) {
            // Get tenant ID to associate with pet
            System.out.print("Enter Tenant ID to associate this pet with (or -1 to exit): ");
            tenantId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left-over

            if (tenantId == -1) {
                System.out.println("Exiting pet addition process.");
                return;
            }

            validTenantId = checkTenantIdExists(tenantId);
            if (!validTenantId) {
                System.out.println("Invalid Tenant ID. Please try again.");
            }
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Get the database connection
            conn = DatabaseUtil.getConnection();

            // SQL to insert new pet
            String sql = "INSERT INTO Pet (pet_name, ten_id) VALUES (?, ?)";

            // Create PreparedStatement
            pstmt = conn.prepareStatement(sql);

            // Set parameters
            pstmt.setString(1, petName);
            pstmt.setInt(2, tenantId);

            // Execute update
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Pet added successfully to the tenant.");
            } else {
                System.out.println("Failed to add the pet.");
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred.");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean checkTenantIdExists(int tenantId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM Tenant WHERE ten_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tenantId);
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

    

    public static void recordMoveOutDate() {

    }

    public static void setMoveOutDate() {
        // Implementation code
    }

    // Additional methods related to Property Manager functionality
}