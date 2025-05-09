package model;

public interface IUserManager {
    boolean registerUser(String email, String password);

    boolean checkIfPasswordsAreTheSame(String password, String confirmPassword);

    boolean checkIfPasswordsMatchRegex(String password, String confirmPassword);

    boolean loginUser(String email, String password);

    boolean checkIfUserExist(String email);

    boolean checkIfEmailExist(String username, String email);

    boolean checkIfEmailMatchRegex(String email);
}
