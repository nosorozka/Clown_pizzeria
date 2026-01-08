package sk.ukf.EmployeeDirectory.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectType, Object id) {
        super(objectType + " s ID " + id + " nebol nájdený");
    }
}