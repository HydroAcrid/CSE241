import java.util.*;
import java.sql.*;


public class capacity {
    //Database URL
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args) {
        //Variable setup
        String user;
        String password;
        Connection connection = null;
        Scanner scnr = new Scanner(System.in);

        int section; //Must be a year a.k.a 4 digits 
        String semester; //spring, summer, fall, winter
        int courseID; //Must be 3 ints
        int sectionID; //Must be between 1 and 9

       
        //Intro Message
        System.out.println("Welcome to my HW 4 Program!!! -Kevin Dotel Inc.");

        //Try to connect to oracle database 
        do {
            try {
                //Login info.
                System.out.print("Enter Oracle user id: ");
                user = scnr.nextLine();
                System.out.print ("Enter Oracle password for dbcourse: ");
                password = scnr.nextLine();

                //Trying to connect to database
                connection = DriverManager.getConnection(DB_URL, user, password);
                System.out.println("Connection Successful. Epic.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (connection == null);


        //Asking for section -> classroom capacity 
        //Make this a loop
        boolean valid = false; 
        do {
            try {
                //Ask for section
                System.out.println("Input data on the section whose classroom capacity you wish to check.");
                System.out.println("Year(yyyy) or 0 to exit: ");
                section = scnr.nextInt();

                //If they press 0
                if(section == 0) {
                    System.out.println("Exiting...");
                    break; 
                }

                //Check if the year exists 
                if(!yearExists(connection, section)) {
                    System.out.println("Year not in database.");
                }

                //Ask for Semester
                System.out.println("Semester(String): ");
                scnr.nextLine(); //get rid of the old newline
                semester = scnr.nextLine();

                //Check if the semester is valid
                if(!semesterExists(semester)) {
                    System.out.println("Please input one of fall, winter, spring, summer.");
                    continue; //restart the loop 
                }

                //Ask for courseID
                System.out.println("Input course ID as 3 digit integer:");
                courseID = scnr.nextInt();

                //Check if it's valid
                if(!courseIdExists(courseID)) {
                    System.out.println("Invalid course ID. Please enter a 3-digit integer.");
                    continue;
                }

                //Ask for section Id
                System.out.println("Input section ID as integer: ");
                sectionID = scnr.nextInt();

                //Check if it's valid
                if(!sectionIdExists(sectionID)) {
                    System.out.println("Section not found. Must be an integer between 1 and 9.");
                    continue;
                }

                if(!capacityCheck(connection, section, semester, courseID, sectionID)) {
                    System.out.println("Error. Row could not be found. It does not exist in the database.");
                    continue;
                }


            } catch(InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid year or 0 to exit.");
                scnr.nextLine(); // Clear the scanner buffer
            }
        } while(!valid);
        


    }


    //Function to check if the year exists 
    public static boolean yearExists(Connection conn, int year) {
        String query = "SELECT COUNT(*) FROM section WHERE year = ?";

        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                int count = rs.getInt(1);
                return count > 0; //will return true of false depending on the num of years
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return false;
    }

    //Function to check if the semester is valid
    public static boolean semesterExists(String semester) {
        return semester.equalsIgnoreCase("spring") || 
               semester.equalsIgnoreCase("summer") || 
               semester.equalsIgnoreCase("fall") ||
               semester.equalsIgnoreCase("winter");
    }

    //Function to check if the courseId is a 3 digit int. the smallest is 104 and the largest is 996
    public static boolean courseIdExists(int courseID) {
        return courseID >= 100 && courseID <= 999;
    }

    //Function to check if the section is between 1 and 5
    public static boolean sectionIdExists(int sectionID) {
        return sectionID >= 1 && sectionID <= 9;
    }

    //Function to find the capacity of the room
public static boolean capacityCheck(Connection conn, int year, String semester, int courseID, int sectionID) {
    // SQL query to find the capacity, enrollment, and open spaces. Very long 
    String query = "WITH SectionDetails AS (SELECT classroom.capacity, section.course_id, section.sec_id, section.semester, section.year FROM classroom JOIN section ON section.room_number = classroom.room_number WHERE section.course_id = ? AND section.sec_id = ? AND section.semester = ? AND section.year = ?), EnrollmentCount AS (SELECT s.capacity, COUNT(t.course_id) AS ENROLLMENT FROM SectionDetails s JOIN takes t ON s.course_id = t.course_id AND s.sec_id = t.sec_id AND s.semester = t.semester AND s.year = t.year GROUP BY s.capacity) SELECT capacity, ENROLLMENT, (capacity - ENROLLMENT) AS EmptySpots FROM EnrollmentCount";
 
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        // Set the parameters
        stmt.setInt(1, courseID);
        stmt.setInt(2, sectionID);
        stmt.setString(3, semester);
        stmt.setInt(4, year);

        // Execute the query
        ResultSet rs = stmt.executeQuery();

        // Retrieve and print the results
        if (rs.next()) {
            int capacity = rs.getInt("capacity");
            int enrollment = rs.getInt("ENROLLMENT");
            int emptySpots = rs.getInt("EmptySpots");

            System.out.println("Capacity is " + capacity + ". Enrollment is " + enrollment + ".");
            System.out.println("There are " + emptySpots + " open seats.");

            return true; // Data found and printed
        } else {
            System.out.println("No data found for the given parameters.");
            return false; // No data found
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Error occurred
    }
}



}

