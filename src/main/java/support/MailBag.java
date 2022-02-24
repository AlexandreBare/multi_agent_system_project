package support;

import environment.Mail;
import environment.Sphere;

public class MailBag {

    private final Sphere sender;
    private Mail[] mailSet;

    public MailBag(Sphere sender) {
        this.sender = sender;
        mailSet = new Mail[0];
    }

    public Sphere getSender() {
        return sender;
    }

    public Mail[] getMailSet() {
        return mailSet;
    }

    public void putInBag(Mail mail) {
        Mail[] temp = new Mail[mailSet.length + 1];
        System.arraycopy(mailSet, 0, temp, 0, mailSet.length);
        temp[mailSet.length] = mail;
        mailSet = temp;
    }
}
