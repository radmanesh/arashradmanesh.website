package notifiers;

import models.Account;
import play.mvc.Mailer;

import javax.mail.internet.InternetAddress;

public class Notifier extends Mailer {

    public static boolean welcome(Account account) throws Exception {
        setFrom(new InternetAddress("admin@sampleforum.com", "Administrator"));
        setReplyTo(new InternetAddress("help@sampleforum.com", "Help"));
        setSubject("Welcome %s", account.fullname);
        addRecipient(account.email, new InternetAddress("new-users@sampleforum.com", "New users notice"));
        return sendAndWait(account);
    }
    
}

