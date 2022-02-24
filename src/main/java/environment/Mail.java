package environment;



public record Mail(String from, String to, String message) {

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return String.format("[Mail - FROM: %s, TO: %s, MESSAGE: %s]", this.from, this.to, this.message);
    }

}
