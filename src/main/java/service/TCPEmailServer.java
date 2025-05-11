package service;

import model.Email;
import model.EmailManager;
import model.UserManager;
import network.TCPNetworkLayer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
@Slf4j
public class TCPEmailServer implements Runnable{
    private Socket socket;
    private UserManager userManager;
    private TCPNetworkLayer networkLayer;
    private EmailManager emailManager;
    private String loggedInUser;
    private BufferedReader in;
    private PrintWriter out;

    public TCPEmailServer(Socket socket, UserManager userManager, EmailManager emailManager) {
        this.socket = socket;
        this.userManager = userManager;
        this.emailManager = emailManager;
    }

    /**
     * Runs the client handling loop
     * Listens for requests from the client, processes them, and sends responses
     * Closes the socket when the session ends or an error occurs.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            log.info("Client connected: {}", socket.getInetAddress());

            String request;
            while ((request = in.readLine()) != null) {
                String response = handleRequest(request);
                out.println(response);
                if (UserUtilities.ACK.equals(response)) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                log.info("Closed connection with client: {}", socket);
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }


    private String handleRequest(String request) {
        String[] components = request.split(UserUtilities.DELIMITER);
        String command = components[0];

        switch (command) {
            case UserUtilities.REGISTER:
                return handleRegister(components);
            case UserUtilities.LOGIN:
                return handleLogin(components);
            case UserUtilities.SEND:
                return handleSendEmail(components);
            case UserUtilities.LIST_RECEIVED:
                return handleListInbox();
            case UserUtilities.READ:
                return handleRead(components);
            case UserUtilities.SEARCH:
                return handleSearchReceived(components);
            case UserUtilities.LOGOUT:
                return handleLogout();

            case UserUtilities.EXIT:
                return UserUtilities.ACK;
            default:
                return UserUtilities.INVALID;
        }
    }

    private String handleRegister(String[] components){
        String response = "";
            if (components.length < 4) {
                return UserUtilities.INVALID_INPUT;
            }

            String email = components[1];
            String password = components[2];
            String confirmPassword = components[3];

            boolean checkIfEmailExist = userManager.checkIfEmailExist(email);

            boolean checkPasswordsMatch = userManager.checkIfPasswordsAreTheSame(password, confirmPassword);

            boolean checkPasswordFormat = userManager.checkIfPasswordsMatchRegex(password, confirmPassword);

            boolean checkEmailFormat = userManager.checkIfEmailMatchRegex(email);

            if (checkIfEmailExist) {
                response = UserUtilities.USER_ALREADY_EXIST;
            } else if (!checkPasswordsMatch) {
                response = UserUtilities.PASSWORDS_DONT_MATCH;
            } else if (!checkPasswordFormat) {
                response = UserUtilities.INVALID_PASSWORD_FORMAT;
            } else if (!checkEmailFormat) {
                response = UserUtilities.INVALID_EMAIL_FORMAT;
            } else {
                userManager.registerUser(email, password);
                response = UserUtilities.REGISTER_SUCCESSFUL;
                loggedInUser = email;
            }
        return response;
    }
    private String handleLogin(String[] components){
        String response = "";
            if (components.length < 3) {
                return UserUtilities.INVALID_INPUT;
            }
            String emailLogin = components[1];
            String passwordLogin = components[2];

            boolean loginUser = userManager.loginUser(emailLogin, passwordLogin);

            if (loginUser == true) {
                response = UserUtilities.LOGIN_SUCCESSFUL;
                loggedInUser = emailLogin;
            } else {
                response = UserUtilities.LOGIN_FAILED;
            }
        return response;
    }
    private String handleSendEmail(String[] components){
        String response = "";
        if(loggedInUser == null){
            return UserUtilities.NOT_LOGGED_IN;
        }
        if(components.length<4){
            return UserUtilities.INVALID_INPUT;
        }
        String recipient = components[1];
        String subject = components[2];
        String body = components[3];
        if(!userManager.checkIfEmailExist(recipient)){
            return UserUtilities.USER_DOESNT_EXIST;
        }
        int id = emailManager.getEmailIdCount()+1;
        Email email1 = new Email(id,loggedInUser,recipient,subject,body, LocalDateTime.now());

        boolean sentEmail = emailManager.addInboxEmails(recipient,email1);

        if(sentEmail == true){
            response = UserUtilities.EMAIL_SENT;
        }else{
            response = UserUtilities.EMAIL_FAILED_TO_SEND;
        }
        return response;
    }
    private String handleListInbox(){
        String response = "";
        if(loggedInUser == null){
            return UserUtilities.NOT_LOGGED_IN;
        }
        ArrayList<Email> emails = emailManager.getInboxEmails(loggedInUser);


        if(emails == null){
            response = UserUtilities.CURRENTLY_NO_EMAILS;
        }else {
            response = emails.toString();
        }
        return response;
    }
    private String handleLogout() {
        loggedInUser = null;
        return UserUtilities.LOGOUT;
    }
    private String handleSearchReceived(String[] components){
        String response = "";
        if(loggedInUser == null){
            log.info("Not logged in.");
            return UserUtilities.NOT_LOGGED_IN;
        }
        if(components.length<2){
            return UserUtilities.INVALID_INPUT;
        }
        String search = components[1];

        ArrayList<Email> found = emailManager.searchBySubject(loggedInUser,search);

        if(found == null){
            response = UserUtilities.NO_EMAILS_FOUND;
        }else{
            response = found.toString();
        }

        return response;

    }
    private String handleRead(String[] components){
        String response = "";
        if(loggedInUser == null){
            return UserUtilities.NOT_LOGGED_IN;
        }
        if(components.length<2){
            return UserUtilities.INVALID_INPUT;
        }
        String stringID = components[1];

        int id;

        try {
            id = Integer.parseInt(stringID);
        } catch (NumberFormatException e) {
            return UserUtilities.INVALID_ID;
        }
        return emailManager.emailMessage(loggedInUser,id);
    }

}
