package waypoint.model;

import java.util.Locale;
import java.util.Objects;

/** Immutable, validated Indian vehicle registration. */
public record Registration(String value) {
    public Registration {
        Objects.requireNonNull(value, "registration");
        value = value.trim().toUpperCase(Locale.ROOT);

        // Two letters, two digits, one or two letters, four digits.
        if (!value.matches("[A-Z]{2}\\d{2}[A-Z]{1,2}\\d{4}")) {
            throw new IllegalArgumentException("Invalid registration: " + value);
        }
    }
}
