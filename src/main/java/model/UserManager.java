package model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.mindrot.jbcrypt.BCrypt;
public class UserManager {
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();


    public UserManager(){

        bootstrapUserList();
    }

    public boolean checkIfUserExist(String email){
        return users.containsKey(email);
    }


    public boolean registerUser(String password, String email){

        User userToBeRegistered = new User(email,hashPassword(password));

        return register(userToBeRegistered);
    }

    private boolean register(User u){
        boolean added = false;
        if(!users.containsKey(u.getEmail())) {
            added = true;
            users.put(u.getEmail(), u);
        }
        return added;
    }

    public boolean checkIfPasswordsAreTheSame(String password, String confirmPassword){

        boolean match = false;

        if (password.equals(confirmPassword)){

            match = true;
        }

        return match;
    }


    public boolean checkIfPasswordsMatchRegex(String password, String confirmPassword){

        boolean match = false;

        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

        if (password.matches(pattern) && confirmPassword.matches(pattern)){

            match = true;
        }


        return match;
    }
    public boolean checkIfEmailExist(String email){

        boolean exist = false;


        for (User u : users.values()){

            if (u.getEmail().equals(email)){

                exist = true;
            }
        }

        return exist;
    }

    public boolean checkIfEmailMatchRegex(String email){

        boolean match = false;

        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (email.matches(pattern)){

            match = true;
        }


        return match;
    }

    public boolean loginUser(String email, String password){

        boolean match = false;

        User u = users.get(email);

        if (u == null){

            match = false;
        }

        if (u != null){
            if (checkPassword(password, u.getPassword())){

                match = true;
            }
        }

        return match;
    }

    private void bootstrapUserList()
    {


        users.put("user@gmail.com", new User("user@gmail.com", "$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi"));
        users.put("user1@gmail.com", new User("user1@gmail.com", "$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi"));
        users.put("user3@gmail.com", new User("user3@gmail.com", "$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi"));

    }

    private static int workload = 12;

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);


        return(hashed_password);
    }


    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }
}
