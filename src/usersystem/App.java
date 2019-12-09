/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usersystem;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP-OMEN 17-AN014NU
 */
public class App {
    public static void main(String[] args) {
        UserSystem userSystem = new UserSystem();
        try {
            userSystem.connectToDB();
            userSystem.createTableUser();
            userSystem.createTableOdit();
            
            try {
                userSystem.addUser1();
            } catch (SQLException e) {                
            }
            
            try {
                userSystem.addUser2();
            } catch (SQLException e) {                
            }
            userSystem.showUsers();
            Scanner scanner = new Scanner(System.in);
            
            System.out.print("username: ");
            String username = scanner.next();
            
            if (userSystem.blockLogin(username)){
                 System.out.print("Too many failed logins! Try again in one minute.");
            } else {
                System.out.print("password: ");
                String password = scanner.next();
                userSystem.loginUser(username, password);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                userSystem.close();
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
