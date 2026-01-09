package sk.ukf.PizzaDirectory.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException(String email, boolean exists) {
        super("Email " + email + " already exists");
    }
}

