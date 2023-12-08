package Final.ked225Dotel;
import java.util.*;
import java.sql.*;

public class main {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        try {
            // Establishing database connection
            DatabaseUtil.connectToDatabase();

            // Starting the main menu
            Menu.displayMainMenu(scnr);

        }
        finally {
            scnr.close();
            // Ensure database connection is closed
            DatabaseUtil.disconnectFromDatabase();
        }
        
    }
    
    
    /*
     Property Manager:
        recordVisitData()
        recordLeaseData()
        recordMoveOut()
        addPersonToLease()
        addPetToLease()
        setMoveOutDate()
     Tenant:
        checkPaymentStatus()
        makeRentalPayment()
        updatePersonalData()
     Company Manager:
        addNewProperty()
        generatePropertyData() (possibly with parameters for size, bedrooms, etc.)
     Financial Manager:
        collectAggregateData() (with parameters to specify the scope, like a specific property or the entire enterprise)
        generateFinancialReports()
     */




   
}
