package Final.ked225Dotel.interfaces;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.sql.Date;


import Final.ked225Dotel.DatabaseUtil;
import Final.ked225Dotel.Menu;

public class PropertyManager {

    // Method for main menu 
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
            System.out.println("7. View all managers and which properties they oversee");
            System.out.println("8. View all properties, their addresses, and their amenities");
            System.out.println("9. View all apartments and their tenants");
            System.out.println("10. Exit");
            
    
            System.out.print("Choose an option: ");
            int choice = -1;
            if (scnr.hasNextInt()) {
                choice = scnr.nextInt();
                scnr.nextLine(); // Consume the newline left-over
            } else {
                System.out.println("Invalid option. Please try again.");
                scnr.next(); // Discard invalid input
                continue; // Skip the current iteration and go back to the start of the loop
            }
    
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
                    recordLeaseData(scnr);
                    break;
                case 5:
                    recordMoveOutDate();
                    break;
                case 6:
                    setMoveOutDate(scnr);
                    break;
                case 7:
                    viewManagers();
                    break;
                case 8:
                    viewPropertiesAndAmenities();
                    break;
                case 9:
                    viewApartmentsAndTenants();
                    break;
                case 10:
                    exitMenu = true;
                    System.out.println("Exiting Property Manager Menu.");
                    Menu.displayMainMenu(scnr);
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 10.");
            }
        }
    }
    
    

    //Method to create a lease. First you need to make a tenant though because it is a full participation relationship 
    public static void recordLeaseData(Scanner scnr) {
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
    
    //Records and adds a visitor 
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
    
    // Method to a person to a lease 
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
                // REMEMBER TO FIX THIS MEMORY LEAK
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
    

    // Method to add a pet to a tenant, which in turn adds it to the lease
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

    // Method to make sure tenant exists when adding a pet 
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

    
    // Method to change the move out date 
    public static void recordMoveOutDate() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT lease_id, lease_end_date FROM Lease");
            rs = pstmt.executeQuery();
    
            while (rs.next()) {
                int leaseId = rs.getInt("lease_id");
                Date moveOutDate = rs.getDate("lease_end_date");
                System.out.println("Lease ID: " + leaseId + " has a move-out date of: " + moveOutDate);
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred while retrieving move-out dates.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                // Don't close conn here if it's shared across the application
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    

    public static void setMoveOutDate(Scanner scnr) {
        int leaseId = -1;
        boolean validLeaseId = false;
        while (!validLeaseId) {
            System.out.print("Enter Lease ID for which you want to set a move-out date: ");
            if (scnr.hasNextInt()) {
                leaseId = scnr.nextInt();
                scnr.nextLine(); // consume the rest of the line
                validLeaseId = DatabaseUtil.checkLeaseIdExists(leaseId);
                if (!validLeaseId) {
                    System.out.println("Lease ID not found. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid Lease ID.");
                scnr.next(); // discard invalid input
            }
        }
    
        LocalDate moveOutDate = DatabaseUtil.promptForDate(scnr, "Enter new move-out date (YYYY-MM-DD): ");
    
        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("UPDATE Lease SET lease_end_date = ? WHERE lease_id = ?");
            pstmt.setDate(1, java.sql.Date.valueOf(moveOutDate));
            pstmt.setInt(2, leaseId);
    
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Move-out date set successfully for Lease ID: " + leaseId);
            } else {
                System.out.println("Failed to set move-out date.");
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred while setting move-out date.");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to check the managers and their properties 
    private static void viewManagers() {
        System.out.println("Viewing all managers and their respective properties...");
    
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            // Get database connection
            conn = DatabaseUtil.getConnection();
    
            // Query to select properties and their managers
            String sql = "SELECT prop_id, manager FROM Property";
    
            // Create PreparedStatement
            pstmt = conn.prepareStatement(sql);
    
            // Execute query
            rs = pstmt.executeQuery();
    
            // Process and display results
            while (rs.next()) {
                int propertyId = rs.getInt("prop_id");
                String managerName = rs.getString("manager");
    
                System.out.println("Property ID: " + propertyId + ", Manager: " + managerName);
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred while retrieving property and manager information.");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Methods to check properties and amenities 
    private static void viewPropertiesAndAmenities() {
        System.out.println("Viewing all properties, their addresses, and common amenities...");
    
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            // Get database connection
            conn = DatabaseUtil.getConnection();
    
            // Query to select property details including address and common amenities
            String sql = "SELECT p.prop_id, a.state, a.town, a.street, a.zip_code, p.common_amen "
                       + "FROM Property p "
                       + "JOIN Address a ON p.addr_id = a.addr_id";
    
            // Create PreparedStatement
            pstmt = conn.prepareStatement(sql);
    
            // Execute query
            rs = pstmt.executeQuery();
    
            // Process and display results
            while (rs.next()) {
                int propertyId = rs.getInt("prop_id");
                String state = rs.getString("state");
                String town = rs.getString("town");
                String street = rs.getString("street");
                String zipCode = rs.getString("zip_code");
                String commonAmenities = rs.getString("common_amen");
    
                System.out.println("Property ID: " + propertyId + ", Address: " + street + ", " + town + ", " + state + ", " + zipCode);
                System.out.println("Common Amenities: " + commonAmenities);
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred while retrieving property and address information.");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    private static void viewApartmentsAndTenants() {
        System.out.println("Viewing all apartments and their tenants...");
    
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            // Get database connection
            conn = DatabaseUtil.getConnection();
    
            // SQL query to join Apartment, Lease, and Tenant tables using LISTAGG for Oracle
            String sql = "SELECT a.apt_id, LISTAGG(t.name, ', ') WITHIN GROUP (ORDER BY t.name) AS tenants "
                        + "FROM Apartment a "
                        + "JOIN Lease l ON a.apt_id = l.apt_id "
                        + "JOIN Tenant t ON l.ten_id = t.ten_id "
                        + "GROUP BY a.apt_id "
                        + "ORDER BY a.apt_id";

    

            // Create PreparedStatement
            pstmt = conn.prepareStatement(sql);
            System.out.println("Executing query: " + sql);

            // Execute query
            rs = pstmt.executeQuery();
    
            // Process and display results
            while (rs.next()) {
                int aptId = rs.getInt("apt_id");
                String tenants = rs.getString("tenants");
    
                System.out.println("Apartment ID: " + aptId + ", Tenants: " + tenants);
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred while retrieving apartments and tenants.");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    


    
    
    
    
    
    
    


}