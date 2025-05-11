package model;

import lombok.*;


import javax.security.auth.Subject;
import java.time.LocalDateTime;
@Getter
@Setter

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Email {
    private int ID;
    private String sender;
    private String receiver;
    private String subject;
    private String message;
    private LocalDateTime timeStamp;

    public String toString() {
        return "Email ID: " + ID + ", From: " + sender + ", To: " + receiver + "Subject: " + subject + ", Sent: " + timeStamp;
    }

}
