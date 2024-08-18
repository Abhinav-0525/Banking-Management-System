package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Customer {
    private Connection con;
    private Scanner sc;

    public Customer(Connection con, Scanner sc){
        this.con = con;
        this.sc = sc;
    }

    public void register(){
        sc.nextLine();
        System.out.print("Full Name: ");
        String name = sc.nextLine();
        System.out.print("E-mail: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        if(userExists(email)){
            System.out.println("Email id already exists!");
            return;
        }

        String registerQuery = "INSERT INTO customers(name, email, password) VALUES(?, ?, ?);";
        try {
            PreparedStatement prepStmt = con.prepareStatement(registerQuery);
            prepStmt.setString(1, name);
            prepStmt.setString(2, email);
            prepStmt.setString(3, password);
            int rowsAffected = prepStmt.executeUpdate();
            if(rowsAffected>0) System.out.println("Registration Successful!");
            else System.out.println("Registration failed");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String login(){
        sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String loginQuery = "SELECT * FROM customers WHERE email = ? AND password = ?";

        try {
            PreparedStatement prepStmt = con.prepareStatement(loginQuery);
            prepStmt.setString(1, email);
            prepStmt.setString(2, password);
            ResultSet res = prepStmt.executeQuery();
            if (res.next()) return email;
            else return null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean userExists(String email){
        String checkQuery = "SELECT * FROM customers WHERE email = ?";
        try {
            PreparedStatement prepStmt = con.prepareStatement(checkQuery);
            prepStmt.setString(1, email);
            ResultSet res = prepStmt.executeQuery();
            return res.next();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}