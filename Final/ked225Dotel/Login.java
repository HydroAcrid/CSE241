package Final.ked225Dotel;
import java.util.Scanner;

public class Login {
    
    public static boolean authenticate(String username, String password) {
        // Dummy method for authentication logic. You would replace this with real authentication.
        return "admin".equals(username) && "admin".equals(password);
    }

    public static void promptLogin(Scanner scnr) {
        System.out.print("Enter Oracle user id: ");
        String user = scnr.nextLine();
        System.out.print("Enter Oracle password for dbcourse: ");
        String password = scnr.nextLine();

        if (authenticate(user, password)) {
            System.out.println("Login successful.");
            // Proceed to the menu or appropriate interface
        } else {
            System.out.println("Login failed. Please try again.");
            // Optionally loop or exit
        }
    }
}
