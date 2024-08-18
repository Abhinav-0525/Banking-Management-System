package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class AccountsManager {
    private Connection con;
    private Scanner sc;

    public AccountsManager(Connection con, Scanner sc){
        this.con = con;
        this.sc = sc;
    }

    public void creditMoney(long accountNumber) throws SQLException{
        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter security pin: ");
        String pin = sc.nextLine();
        try {
            con.setAutoCommit(false);
            String query = "SELECT * FROM accounts WHERE account_no = ? AND pin = ?";
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setLong(1, accountNumber);
            prepStmt.setString(2, pin);
            ResultSet res = prepStmt.executeQuery();
            if(res.next()){
                String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
                PreparedStatement updStmt = con.prepareStatement(creditQuery);
                updStmt.setDouble(1, amount);
                updStmt.setLong(2, accountNumber);
                int rowsAffected = updStmt.executeUpdate();
                if(rowsAffected>0){
                    System.out.println("Rs."+amount+" deposited successfully!");
                    con.commit();
                    con.setAutoCommit(true);
                    return;
                }
                else{
                    System.out.println("Transaction failed!");
                    con.rollback();
                    con.setAutoCommit(true);
                }
            }
            else{
                System.out.println("Incorrect Pin!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        con.setAutoCommit(true);
    }

    public void debitMoney(long accountNumber) throws SQLException{
        System.out.print("Enter amount to withdraw: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter security pin: ");
        String pin = sc.nextLine();
        try {
            con.setAutoCommit(false);
            String query = "SELECT * FROM accounts WHERE account_no = ? AND pin = ?";
            PreparedStatement prepStmt = con.prepareStatement(query);
            prepStmt.setLong(1, accountNumber);
            prepStmt.setString(2, pin);
            ResultSet res = prepStmt.executeQuery();
            if(res.next()){
                double currBalance = res.getDouble("balance");
                if(amount<=currBalance){
                    String creditQuery = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
                    PreparedStatement updStmt = con.prepareStatement(creditQuery);
                    updStmt.setDouble(1, amount);
                    updStmt.setLong(2, accountNumber);
                    int rowsAffected = updStmt.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Rs."+amount+" withdrawn successfully!");
                        con.commit();
                        con.setAutoCommit(true);
                        return;
                    }
                    else{
                        System.out.println("Transaction failed!");
                        con.rollback();
                        con.setAutoCommit(true);
                    }
                }
                else{
                    System.out.println("Insufficient funds!");
                }
            }
            else{
                System.out.println("Incorrect Pin!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        con.setAutoCommit(true);
    }

    public void getBalance(long accountNumber){
        System.out.print("Enter security pin: ");
        sc.nextLine();
        String pin = sc.nextLine();
        try{
            String balQuery = "SELECT * FROM accounts WHERE account_no = ? AND pin = ?";
            PreparedStatement stmt = con.prepareStatement(balQuery);
            stmt.setLong(1, accountNumber);
            stmt.setString(2, pin);
            ResultSet res = stmt.executeQuery();
            if(res.next()){
                double balance = res.getDouble("balance");
                System.out.println("Balance: "+balance);
            }
            else System.out.println("Invalid Pin!");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void transferMoney(long senderAccNumber) throws SQLException{
        System.out.print("Enter the receiver account number: ");
        long receiverAccNumber = sc.nextLong();
        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security pin: ");
        String pin = sc.nextLine().substring(0,4);
        try {
            con.setAutoCommit(false);
            String query = "SELECT * FROM accounts WHERE account_no = ? AND pin = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setLong(1, senderAccNumber);
            stmt.setString(2, pin);
            ResultSet res = stmt.executeQuery();
            if(res.next()){
                double currBalance = res.getDouble("balance");
                if(currBalance>=amount){
                    String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
                    String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
                    PreparedStatement debitStmt = con.prepareStatement(debitQuery);
                    PreparedStatement creditStmt = con.prepareStatement(creditQuery);
                    debitStmt.setDouble(1, amount);
                    debitStmt.setLong(2, senderAccNumber);
                    creditStmt.setDouble(1, amount);
                    creditStmt.setLong(2, receiverAccNumber);
                    int debitRows = debitStmt.executeUpdate();
                    int creditRows = creditStmt.executeUpdate();
                    if(debitRows>0 && creditRows>0){
                        System.out.println("Transaction Successful!");
                        System.out.println("Rs."+amount+" has been transferred to A.c no: "+receiverAccNumber);
                        String transQuery = "INSERT INTO transactions(sender, receiver, amount, trans_date) values(?,?,?,?)";
                        PreparedStatement transStmt = con.prepareStatement(transQuery);
                        transStmt.setLong(1, senderAccNumber);
                        transStmt.setLong(2, receiverAccNumber);
                        transStmt.setDouble(3, amount);
                        Date currDate = new Date(System.currentTimeMillis());
                        transStmt.setDate(4, currDate);
                        int transRows = transStmt.executeUpdate();
                        if(transRows>0) System.out.println("Transaction recorded!");
                        con.commit();
                        con.setAutoCommit(true);
                        return;
                    }
                    else{
                        System.out.println("Transaction failed!");
                        con.rollback();
                        con.setAutoCommit(true);
                    }
                }
                else{
                    System.out.println("Insufficient funds!");
                    
                }
            }
            else{
                System.out.println("Incorrect Pin!");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        con.setAutoCommit(true);
    }

    public void getTransactions(long accountNumber){
        // debit side of transactions
        String debitQuery = "SELECT * FROM transactions WHERE sender = ? ORDER BY trans_date desc";
        String creditQuery = "SELECT * FROM transactions WHERE receiver = ? ORDER BY trans_date desc";
        try{
            PreparedStatement stmt1 = con.prepareStatement(debitQuery);
            PreparedStatement stmt2 = con.prepareStatement(creditQuery);
            stmt1.setLong(1, accountNumber);
            stmt2.setLong(1, accountNumber);
            ResultSet res = stmt1.executeQuery();   
            boolean hasDebit = false;
            while(res.next()){
                hasDebit = true;
                System.out.println("Rs."+res.getDouble("amount")+" has been transferred to A.c no: "+res.getLong("receiver")+" at "+res.getDate("trans_date"));
            }
            if(!hasDebit) System.out.println("There are no Debit Transactions yet!");
            ResultSet ans = stmt2.executeQuery();
            boolean hasCredit = false;
            while(ans.next()){
                hasCredit = true;
                System.out.println("Rs."+ans.getDouble("amount")+" has been recieved from A.c no: "+ans.getLong("receiver")+" at "+ans.getDate("trans_date"));
            }
            if(!hasCredit) System.out.println("There are no Credit Transactions yet!");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

}