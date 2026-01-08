package sk.ukf.PizzaDirectory.entity;

/**
 * Enum representing order statuses
 */
public enum OrderStatus {
    PENDING("Čakajúca"),
    COOKING("Pripravuje sa"),
    READY("Pripravená"),
    DELIVERING("Doručuje sa"),
    DELIVERED("Doručená"),
    CANCELLED("Zrušená");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

