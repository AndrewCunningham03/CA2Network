package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EmailManager {

    private ConcurrentHashMap<String, ArrayList<Email>> inbox = new ConcurrentHashMap<>();
    private static int emailIdCount = 0;

    public EmailManager(){

        bootstrapEmailList();
    }



    private void bootstrapEmailList()
    {


        ArrayList<Email> email = new ArrayList();
        email.add(new Email(emailIdCount+1, "user@gmail.com","user1@gmail.com", "Hip Hop", "I Love Hip Hop", LocalDateTime.of(2025, 03, 5, 5, 34)));

        ArrayList<Email> email2 = new ArrayList();
        email2.add(new Email(emailIdCount+1, "user1@gmail.com","user2@gmail.com", "Hip Hop", "I Love Hip Hop", LocalDateTime.of(2025, 03, 5, 5, 34)));

        ArrayList<Email> email3 = new ArrayList();
        email3.add(new Email(emailIdCount+1, "user2@gmail.com","user@gmail.com", "Hip Hop", "I Love Hip Hop", LocalDateTime.of(2025, 03, 5, 5, 34)));

        inbox.put("user@gmail.com", email);
        inbox.put("user1@gmail.com", email2);
        inbox.put("user2@gmail.com", email3);

    }

    public int getEmailIdCount(){
        return emailIdCount;
    }

    public boolean addInboxEmails(String username, Email email){
        List<Email> userInboxEmails = inbox.get(username);

        if(userInboxEmails.isEmpty()){
            userInboxEmails = new ArrayList<>();
            inbox.put(username, (ArrayList<Email>) userInboxEmails);
        }

        userInboxEmails.add(email);
        return true;
    }

    public ArrayList<Email> getInboxEmails(String username){
        ArrayList<Email> emails = inbox.get(username);
        return emails;
    }

}
