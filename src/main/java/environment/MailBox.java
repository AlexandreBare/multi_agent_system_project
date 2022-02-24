package environment;


@SuppressWarnings("FieldCanBeLocal")
public class MailBox {

    private final Mail[] box;
    private final int DEFAULT_MAX_NUMBER_OF_MAILS = 1024;
    private int numberOfMailsInBox;
    private final int size;

    
    public MailBox() {
        box = new Mail[DEFAULT_MAX_NUMBER_OF_MAILS];
        size = DEFAULT_MAX_NUMBER_OF_MAILS;
        numberOfMailsInBox = 0;
    }

    public MailBox(int size) {
        box = new Mail[size];
        this.size = size;
        numberOfMailsInBox = 0;
    }

    public void putMail(Mail mail) {
        box[numberOfMailsInBox] = mail;
        numberOfMailsInBox++;
    }

    public Mail pickMail() {
        Mail mail = null;
        if (numberOfMailsInBox > 0) {
            mail = box[numberOfMailsInBox - 1];
            numberOfMailsInBox--;
        }
        return mail;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return numberOfMailsInBox == 0;
    }

}
