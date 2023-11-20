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
                menu(scnr);
                
            } catch(InputMismatchException e) {
                System.out.println("Invalid input.");
            }
        } while(!valid);

        scnr.close();

    }



    //Main menu 
    public static void menu(Scanner scnr) {
        System.out.println("Welcome. Please select an interface and login.");
        boolean valid = false;
        int option;

        do {
            try {
                System.out.println("1. Property Manager");
                System.out.println("2. Tenant");
                System.out.println("3. Company Manager");
                System.out.println("4. Financial Manager");
                System.out.println("5. Exit");

                option = scnr.nextInt();
                if(option == 1) {
                    propertyManInterface();
                    valid = true;
                }
                else if(option == 2) {
                    tenantInterface();
                    valid = true;
                }
                else if(option == 3) {
                    companyManInterface();
                    valid = true;
                }
                else if(option == 4) {
                    financialManInterface();
                    valid = true;
                }
                else if(option == 5) {
                    System.out.println("Goodbye.");
                    System.exit(0);
                }
                else {
                    System.out.println("Please select options 1-5.");
                }
            } catch(InputMismatchException e) {
                System.out.println("Invalid input.");
            }
        } while(!valid);
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

    /**
     * Method to check your login privileges. 
     * Change this to a boolean later to identify which type of user you are. 
     * @param scnr
     */
    public static void privilegeLogin(Scanner scnr) {

    }



   
}
