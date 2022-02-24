package util.event;

import environment.Mail;

public class MsgSentEvent extends Event {

    private Mail msg;

    
    public MsgSentEvent(Object thrower) {
        super(thrower);
    }

    public Mail getMsg() {
        return msg;
    }

    public void setMsg(Mail msg) {
        this.msg = msg;
    }

    public boolean isQuestion() {
        return msg.getMessage().charAt(7) == '?';
    }
}
