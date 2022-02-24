package environment;

/**
 * A class facilitating communication between the Spheres and the PostalService.
 */
public class MailBag extends ToHandle {

    private Mail[] mailSet;

    public MailBag(Sphere sender) {
        super(sender);
        mailSet = new Mail[0];
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
