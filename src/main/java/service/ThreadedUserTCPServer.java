package service;

import model.EmailManager;
import model.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedUserTCPServer {
    public static void main(String[] args) {

        try (ServerSocket connectionSocket = new ServerSocket(UserUtilities.PORT)){

            UserManager userManager = new UserManager();

            EmailManager emailManager = new EmailManager();

            boolean validServerSession = true;
            while(validServerSession){
                Socket Socket = connectionSocket.accept();
                TCPEmailServer clientHandler = new TCPEmailServer(Socket, userManager,emailManager);
                Thread wrapper = new Thread(clientHandler);
                wrapper.start();
            }
        }catch (IOException e){
            System.out.println("Connection socket cannot be established");
        }

    }
}
