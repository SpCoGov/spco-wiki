package top.spco.spcobot.wiki.core.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String username) {
        super("There is no user by the name '" + username + "'. Check your spelling.");
    }
}
