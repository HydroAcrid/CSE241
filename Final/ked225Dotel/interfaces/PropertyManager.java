package Final.ked225Dotel.interfaces;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import Final.ked225Dotel.DatabaseUtil;

public class PropertyManager {

    // Method to display tenant menu 
    public static void propertyManagerMenu(Scanner scnr) {
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("Property Manager Menu:");
            System.out.println("1. Add person to lease");
            System.out.println("2. Add pet to lease");
            System.out.println("3. Record a visitor");
            System.out.println("4. Record a lease");
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
                    recordVisitData(scnr);
                    break;
                case 4:
                    createNewLease(scnr);
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
    

    //Method to create a lease. First you need to make a tenant though because it is a full participation relationship 
    public static void createNewLease(Scanner scnr) {
        int tenantId = addPersonToLease(scnr);

        LocalDate startDate = null;
        while (startDate == null) {
            System.out.print("Enter lease start date (format YYYY-MM-DD): ");
            try {
                startDate = LocalDate.parse(scnr.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        LocalDate endDate = null;
        while (endDate == null) {
            System.out.print("Enter lease end date (format YYYY-MM-DD): ");
            try {
                endDate = LocalDate.parse(scnr.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        double rentAmount = -1;
        while (rentAmount < 0) {
            System.out.print("Enter rent amount: ");
            if (scnr.hasNextDouble()) {
                rentAmount = scnr.nextDouble();
                scnr.nextLine(); // Consume the newline left-over
                if (rentAmount < 0) {
                    System.out.println("Rent amount cannot be negative.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); // discard invalid input
            }
        }

        PreparedStatement pstmt = null;

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Assume lease_seq is the sequence created for auto-incrementing lease_id
            String sql = "INSERT INTO Lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (lease_seq.NEXTVAL, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            pstmt.setDouble(3, rentAmount);
            pstmt.setInt(4, tenantId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Lease successfully created for Tenant ID: " + tenantId);
            } else {
                System.out.println("Failed to create lease.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while creating lease.");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static void recordVisitData(Scanner scnr) {
        System.out.println("Record Visit Data");
    
        String visitorName;
        int age;
        double salary;
    
        // Get visitor's name
        do {
            System.out.print("Enter Visitor's Name: ");
            visitorName = scnr.nextLine();
            if (visitorName.isEmpty()) {
                System.out.println("Name cannot be empty. Please try again.");
            }
        } while (visitorName.isEmpty());
    
        // Get visitor's age
        do {
            System.out.print("Enter Visitor's Age: ");
            while (!scnr.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); // discard invalid input
            }
            age = scnr.nextInt();
            scnr.nextLine(); // Consume the newline left-over
            if (age <= 0) {
                System.out.println("Age must be greater than 0. Please try again.");
            }
        } while (age <= 0);
    
        // Get visitor's salary
        do {
            System.out.print("Enter Visitor's Salary: ");
            while (!scnr.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); // discard invalid input
            }
            salary = scnr.nextDouble();
            scnr.nextLine(); // Consume the newline left-over
            if (salary < 0) {
                System.out.println("Salary cannot be negative. Please try again.");
            }
        } while (salary < 0);
    
        Connection conn = null;
        PreparedStatement pstmt = null;
    
        try {
            // Get the database connection
            conn = DatabaseUtil.getConnection();
    
            // SQL to insert new visitor
            String sql = "INSERT INTO Visitor (visit_name, age, salary) VALUES (?, ?, ?)";
    
            // Create PreparedStatement
            pstmt = conn.prepareStatement(sql);
    
            // Set parameters
            pstmt.setString(1, visitorName);
            pstmt.setInt(2, age);
            pstmt.setDouble(3, salary);
    
            // Execute update
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Visitor data recorded successfully.");
            } else {
                System.out.println("Failed to record visitor data.");
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
    
    

    public static void recordLeaseData(Scanner scnr) {
        System.out.println("Record Lease Data");
    
        int tenantId;
        String leaseStartDate;
        String leaseEndDate;
        double rentAmount;
    
        // Get Tenant ID
        do {
            System.out.print("Enter Tenant ID: ");
            while (!scnr.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); // discard invalid input
            }
            tenantId = scnr.nextInt();
            scnr.nextLine(); // Consume the newline left-over
            if (!isTenantExists(tenantId)) {
                System.out.println("Tenant ID does not exist. Please try again.");
            }
        } while (!isTenantExists(tenantId));
    
        // Get Lease Start Date
        System.out.print("Enter Lease Start Date (YYYY-MM-DD): ");
        leaseStartDate = scnr.nextLine();
    
        // Get Lease End Date
        System.out.print("Enter Lease End Date (YYYY-MM-DD): ");
        leaseEndDate = scnr.nextLine();
    
        // Get Rent Amount
        do {
            System.out.print("Enter Rent Amount: ");
            while (!scnr.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number.");
                scnr.next(); // discard invalid input
            }
            rentAmount = scnr.nextDouble();
            scnr.nextLine(); // Consume the newline left-over
            if (rentAmount <= 0) {
                System.out.println("Rent amount must be greater than 0. Please try again.");
            }
        } while (rentAmount <= 0);
    
        Connection conn = null;
        PreparedStatement pstmt = null;
    
        try {
            // Get the database connection
            conn = DatabaseUtil.getConnection();
    
            // SQL to insert new lease
            String sql = "INSERT INTO Lease (lease_start_date, lease_end_date, rent_amt, ten_id) " +
                         "VALUES (TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
    
            // Create PreparedStatement
            pstmt = conn.prepareStatement(sql);
    
            // Set parameters
            pstmt.setString(1, leaseStartDate);
            pstmt.setString(2, leaseEndDate);
            pstmt.setDouble(3, rentAmount);
            pstmt.setInt(4, tenantId);
    
            // Execute update
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Lease data recorded successfully.");
            } else {
                System.out.println("Failed to record lease data.");
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
    
    // Helper method to check if a tenant exists
    private static boolean isTenantExists(int tenantId) {
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
    

    public static int addPersonToLease(Scanner scnr) {
        System.out.println("Adding a new tenant...");
    
        System.out.print("Enter tenant's name: ");
        String tenantName = scnr.nextLine();
    
        String phoneNumber;
        do {
            System.out.print("Enter tenant's phone number (format XXX-XXX-XXXX): ");
            phoneNumber = scnr.nextLine();
            if (!DatabaseUtil.isValidPhoneNumber(phoneNumber)) {
                System.out.println("Invalid phone format. Please try again.");
            }
        } while (!DatabaseUtil.isValidPhoneNumber(phoneNumber));
    
        String emailAddr;
        do {
            System.out.print("Enter tenant's email address: ");
            emailAddr = scnr.nextLine();
            if (!DatabaseUtil.isValidEmail(emailAddr)) {
                System.out.println("Invalid email format. Please try again.");
            } else if (DatabaseUtil.emailExists(emailAddr)) {
                System.out.println("Email already exists. Please enter a different email address.");
                emailAddr = "";
            }
        } while (!DatabaseUtil.isValidEmail(emailAddr) || DatabaseUtil.emailExists(emailAddr));
    
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int tenantId = -1; // Initialize tenantId to -1
    
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Tenant (name, phone_num, email_addr) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
    
            pstmt.setString(1, tenantName);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, emailAddr);
    
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Tenant successfully added.");
    
                // Retrieve the tenantId of the newly added tenant
                String selectSql = "SELECT ten_id FROM Tenant WHERE email_addr = ?";
                pstmt = conn.prepareStatement(selectSql);
                pstmt.setString(1, emailAddr);
                rs = pstmt.executeQuery();
    
                if (rs.next()) {
                    tenantId = rs.getInt("ten_id");
                }
            } else {
                System.out.println("Failed to add tenant.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while adding tenant.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    
        return tenantId; // Return the retrieved tenantId
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