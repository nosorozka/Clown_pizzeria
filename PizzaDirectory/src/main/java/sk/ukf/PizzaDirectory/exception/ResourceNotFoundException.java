package sk.ukf.PizzaDirectory.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Integer id) {
        super(resourceName + " with id " + id + " not found");
    }
}

