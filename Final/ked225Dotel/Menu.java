package Final.ked225Dotel;
import java.util.InputMismatchException;
import java.util.Scanner;

import Final.ked225Dotel.interfaces.PropertyManager;
import Final.ked225Dotel.interfaces.Tenant;

public class Menu {
    public static void displayMainMenu(Scanner scnr) {
        boolean valid = false;
        do {
            try {
                System.out.println("Welcome. Please select an interface and login:");
                System.out.println("1. Property Manager");
                System.out.println("2. Tenant");
                System.out.println("3. Exit");

                int option = scnr.nextInt();
                scnr.nextLine();  // Add this line to consume the remaining newline

                switch (option) {
                    case 1:
                        // Instantiate and use the Property Manager interface
                        System.out.println("Property Manager Interface selected.");
                        valid = true;
                        PropertyManager.propertyManagerMenu(scnr);
                        break;
                    case 2:
                        // Instantiate and use the Tenant interface
                        System.out.println("Tenant Interface selected.");
                        valid = true;
                        Tenant.tenantLogin(scnr);
                        break;
                    case 3:
                        System.out.println("Goodbye.");
                        DatabaseUtil.disconnectFromDatabase();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Please select options 1-3.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number 1-3.");
                scnr.nextLine(); // Consume the incorrect input
                continue;
            }
        } while (!valid);
    }

}
