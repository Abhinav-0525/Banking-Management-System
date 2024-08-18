package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Accounts {
    private Connection con;
    private Scanner sc;

    public Accounts(Connection con, Scanner sc){
        this.con = con;
        this.sc = sc;
    }

    public long openAccount(String email){
        if(accountExists(email)){
            throw new RuntimeException("Account already exists!");
        }
        
        String createAccQuery = "INSERT INTO accounts(account_no, name, email, balance, pin) values(?,?,?,?,?)";
        sc.nextLine();
        System.out.print("Enter full name: ");
        String name = sc.nextLine();
        System.out.print("Enter initial amount: ");
        double balance = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter 4-digit security pin: ");
        String pin = sc.nextLine().substring(0,4);
        long accountNo = generateAccountNumber();
        try {
            PreparedStatement prepStmt = con.prepareStatement(createAccQuery);
            prepStmt.setLong(1, accountNo);
            prepStmt.setString(2, name);
            prepStmt.setString(3, email);
            prepStmt.setDouble(4, balance);
            prepStmt.setString(5, pin);
            int rowsAffected = prepStmt.executeUpdate();
            if(rowsAffected>0) return accountNo;
            else{
                throw new RuntimeException("Account Creation failed");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accountNo;
    }

    public boolean accountExists(String email){
        String checkQuery = "SELECT * FROM accounts WHERE email = ?";
        try{
            PreparedStatement prepStmt = con.prepareStatement(checkQuery);
            prepStmt.setString(1, email);
            ResultSet res = prepStmt.executeQuery();
            return res.next();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    private long generateAccountNumber(){
        String query = "SELECT * FROM accounts ORDER BY account_no DESC LIMIT 1";
        try {
            PreparedStatement prepStmt = con.prepareStatement(query);
            ResultSet res = prepStmt.executeQuery();
            if(res.next()){
                long lastAccNo = res.getLong("account_no");
                return lastAccNo+1;
            }
            else return 10000100;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 10000100;
    }

    public long getAccountNumber(String email){
        String searchQuery = "SELECT * FROM accounts WHERE email = ?";
        try{
            PreparedStatement prepStmt = con.prepareStatement(searchQuery);
            prepStmt.setString(1, email);
            ResultSet res = prepStmt.executeQuery();
            if(res.next()) return res.getLong("account_no");
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Account number doesn't exist!");
    }
}
