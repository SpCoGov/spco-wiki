package top.spco.spcobot.wiki.core.exception;

public class AlreadyBlockedException extends RuntimeException {
    public AlreadyBlockedException(String username) {
        super("'" + username + "' is already blocked.");
    }
}
