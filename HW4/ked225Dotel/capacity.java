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

            } catch(Exception e) {
                System.out.println("Invalid input. Please enter a valid year or 0 to exit.");
                scnr.nextLine(); // Clear the scanner buffer
            }
        } while(!valid);
        



        
    }
}

