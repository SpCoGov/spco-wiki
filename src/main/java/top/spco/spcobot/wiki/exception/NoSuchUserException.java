package top.spco.spcobot.wiki.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String username) {
        super("There is no user by the name '" + username + "'. Check your spelling.");
    }

}
