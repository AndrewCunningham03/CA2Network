package model;

import java.util.ArrayList;

public interface IEmailManager {

    public int getEmailIdCount();
    public boolean addInboxEmails(String username, Email email);
    public ArrayList<Email> getInboxEmails(String username);
}
