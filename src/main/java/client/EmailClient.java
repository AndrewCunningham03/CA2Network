package client;

import model.*;
import network.TCPNetworkLayer;
import service.UserUtilities;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class EmailClient {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        TCPNetworkLayer networkLayer = new TCPNetworkLayer(UserUtilities.HOSTNAME,UserUtilities.PORT);

        UserManager userManager = new UserManager();

        EmailManager emailManager = new EmailManager();

        Boolean loggedIn = false;

        try{
            networkLayer.connect();
            Boolean running = true;

            while(running) {
                System.out.println("\nSelect a number:");
                if(!loggedIn){
                    System.out.println("1. Register");
                    System.out.println("2. Login");
                }
                if(loggedIn){
                    System.out.println("3. Send email");
                    System.out.println("4. list received emails");
                    System.out.println("5. Search received emails");
                    System.out.println("6. Read email by email id");
                    System.out.println("7. Logout");
                }

                System.out.println("8. Exit");

                System.out.println("\nEnter number: ");
                String choice = keyboard.nextLine();
                String response = "";

                switch (choice) {
                    case "1":
                        if(loggedIn) break;
                        System.out.println("Email address:");
                        String email = keyboard.nextLine();
                        System.out.println("Password:");
                        String password = keyboard.nextLine();
                        System.out.println("Confirm password:");
                        String confirmPassword = keyboard.nextLine();

                        response = UserUtilities.REGISTER+UserUtilities.DELIMITER+email+UserUtilities.DELIMITER+password+UserUtilities.DELIMITER+confirmPassword;

                        break;
                    case "2":
                        if(loggedIn) break;
                        System.out.println("Email address:");
                        String emailLogin = keyboard.nextLine();
                        System.out.println("Password:");
                        String passwordLogin = keyboard.nextLine();

                        response = UserUtilities.LOGIN+UserUtilities.DELIMITER+emailLogin+UserUtilities.DELIMITER+passwordLogin;

                        break;
                    case "3":
                        if(!loggedIn) break;
                        System.out.print("Recipient: ");
                        String recipient = keyboard.nextLine();
                        System.out.print("Subject: ");
                        String subject = keyboard.nextLine();
                        System.out.print("Message: ");
                        String body = keyboard.nextLine();

                        response = UserUtilities.SEND+UserUtilities.DELIMITER+recipient+UserUtilities.DELIMITER+subject+UserUtilities.DELIMITER+body;

                        break;
                    case "4":
                        if(!loggedIn) break;
                        response = UserUtilities.LIST_RECEIVED;
                        break;
                    case "5":
                        if(!loggedIn) break;
                        System.out.println("Enter subject word you would like to search by");
                        String word = keyboard.nextLine();
                        response = UserUtilities.SEARCH+UserUtilities.DELIMITER+word;
                        break;
                    case "6":
                        if(!loggedIn) break;
                        System.out.println("Enter Email id you would like to read");
                        String emailID= keyboard.nextLine();
                        response = UserUtilities.READ+UserUtilities.DELIMITER+emailID;
                        break;
                    case "7":
                        if(!loggedIn) break;
                        response = UserUtilities.LOGOUT;
                        break;
                    case "8":
                        response = UserUtilities.EXIT;
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice.");
                        continue;
            }
                networkLayer.send(response);
                String message = networkLayer.receive();
                System.out.println("Message: " + message);

                if (message.equals(UserUtilities.LOGIN_SUCCESSFUL) || message.equals(UserUtilities.REGISTER_SUCCESSFUL)) {
                    loggedIn = true;
                } else if (message.equals(UserUtilities.LOGOUT)) {
                    loggedIn = false;
                }
            }

            networkLayer.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

