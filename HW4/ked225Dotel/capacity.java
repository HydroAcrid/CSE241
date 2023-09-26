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
       
        //Intro Message
        System.out.println("Welcome to my HW 4 Program!!! -Kevin Dotel Inc.");

        do {
            try {
                //Login info.
                System.out.print("Please Enter Username: ");
                user = scnr.nextLine();
                System.out.print ("Please Enter Password: ");
                password = scnr.nextLine();

                //Trying to connect to database
                connection = DriverManager.getConnection(DB_URL, user, password);
                System.out.println("Connection Successful. Epic.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (connection == null);
        



        
    }
}

