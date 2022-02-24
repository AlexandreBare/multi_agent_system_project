package environment;

import java.util.logging.Logger;

/**
 * This is a cloneable container for Mail
 */
public class MailBuffer implements Cloneable {

    private Mail[] buffer;

    private final Logger logger = Logger.getLogger(MailBuffer.class.getName());

    /**
     * Creates a new MailBuffer
     */
    public MailBuffer() {
        buffer = new Mail[0];
    }

    /**
     * Adds a new mail to the buffer
     */
    public void addMail(Mail mail) {
        Mail[] temp = new Mail[buffer.length + 1];
        System.arraycopy(buffer, 0, temp, 0, buffer.length);
        temp[buffer.length] = mail;
        buffer = temp;
    }

    /**
     * Returns the mails in an array
     */
    public Mail[] getMails() {
        return buffer;
    }

    /**
     * Removes all mails from this buffer
     */
    public void clear() {
        buffer = new Mail[0];
    }

    /**
     * Clones this MailBuffer
     */
    public Object clone() {
        MailBuffer mailBuffer;
        try {
            mailBuffer = (MailBuffer) super.clone();
        } catch (CloneNotSupportedException exc) {
            this.logger.severe("Cloning not supported for the MailBuffer.");
            throw new RuntimeException(exc);
        }
        mailBuffer.buffer = new Mail[buffer.length];
        System.arraycopy(buffer, 0, mailBuffer.buffer, 0, buffer.length);
        return mailBuffer;
    }
}
