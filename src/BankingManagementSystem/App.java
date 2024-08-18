package BankingManagementSystem;
import java.sql.*;
import java.util.Scanner;

public class App {
    private static final String url = "jdbc:mysql://localhost:3306/bankmanager";
    private static final String username = "root";
    private static final String password = "@b#inav@7325";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //used to load the jdbc driver
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Scanner sc = new Scanner(System.in);

            Customer user = new Customer(con, sc);
            Accounts accounts = new Accounts(con, sc);
            AccountsManager accManager = new AccountsManager(con, sc);

            String email;
            long accountNumber;

            while (true) {
                System.out.println("----- Banking Management System -----");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Please select a choice: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if(email!=null){
                            System.out.println("User logged in!");
                            System.out.println();
                            if(!accounts.accountExists(email)){
                                System.out.println("1. Open a new bank account");
                                System.out.println("2. Exit");
                                if(sc.nextInt()==1){
                                    accountNumber = accounts.openAccount(email);
                                    System.out.println("Account created successfully");
                                    System.out.println("Your account number is: "+accountNumber);
                                }
                                else break;
                            }
                            accountNumber = accounts.getAccountNumber(email);
                            int nextChoice = 0;
                            while(nextChoice!=6){
                                System.out.println();
                                System.out.println("1. Withdraw money");
                                System.out.println("2. Deposit money");
                                System.out.println("3. Transfer money");
                                System.out.println("4. Check balance");
                                System.out.println("5. View Transactions");
                                System.out.println("6. Exit");
                                System.out.print("Please select a choice: ");
                                nextChoice = sc.nextInt();
                                switch (nextChoice) {
                                    case 1:
                                        accManager.debitMoney(accountNumber);
                                        break;
                                    case 2:
                                        accManager.creditMoney(accountNumber);
                                        break;
                                    case 3:
                                        accManager.transferMoney(accountNumber);
                                        break;
                                    case 4:
                                        accManager.getBalance(accountNumber);
                                        break;
                                    case 5:
                                        accManager.getTransactions(accountNumber);
                                    case 6:
                                        break;
                                    default:
                                        System.out.println("Please enter a valid choice!");
                                        continue;
                                }
                            }
                        }
                        else{
                            System.out.println("Invalid email or password!");
                        }
                    case 3:
                        System.out.println("Thank you for using Banking System!!");
                        return;
                    default:
                        System.out.println("Please enter a valid choice!");
                        continue;
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
