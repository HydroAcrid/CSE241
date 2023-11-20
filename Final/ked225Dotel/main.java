package Final.ked225Dotel;
import java.util.*;
import java.sql.*;

public class main {
    //Database URL
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    //This is temporary
    static final String DotelUser = "ked225";
    static final String DotelPassword = "3SnowAcrid";

    public static void main(String[] args) {
        //Variable setup
        String user;
        String password;
        Connection connection = null;
        Scanner scnr = new Scanner(System.in);

        //Make all of the table variables here 

        //Try to connect to oracle database 
        do {
            try {
                //Login info.
                System.out.print("Enter Oracle user id: ");
                user = scnr.nextLine();
                System.out.print ("Enter Oracle password for dbcourse: ");
                password = scnr.nextLine();

                //Trying to connect to database
                //connection = DriverManager.getConnection(DB_URL, user, password);
                connection = DriverManager.getConnection(DB_URL, DotelUser, DotelPassword); //TEMP LINE 
                System.out.println("Connection Successful. Rad.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (connection == null);

        boolean valid = false; 
        do {
            try {

            } catch(InputMismatchException e) {
                System.out.println("Invalid input.");
            }
        } while(!valid);

        scnr.close();

    }



    public static void menu() {
        System.out.println();
    }

    public static void propertyManInterface() {
        System.out.println();
    }

    public static void tenantInterface() {
        System.out.println();
    }

    public static void companyManInterface() {
        System.out.println();
    }

    public static void financialManInterface() {
        System.out.println();
    }



   
}
