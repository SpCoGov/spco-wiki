package top.spco.spcobot.wiki.exception;

public class AlreadyBlockedException extends RuntimeException {
    public AlreadyBlockedException(String username) {
        super("'" + username + "' is already blocked.");
    }
}
