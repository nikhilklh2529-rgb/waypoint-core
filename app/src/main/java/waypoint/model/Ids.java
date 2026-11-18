package waypoint.model;

import java.util.Objects;

/**
 * Strongly typed identifiers. Each is a distinct immutable value, so
 * accidentally swapping a TicketId and SpotId is a compile-time error.
 */
record TicketId(String value) {
    TicketId {
        Objects.requireNonNull(value, "ticket id");
        value = value.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Ticket id is blank");
    }
}

record SpotId(String value) {
    SpotId {
        Objects.requireNonNull(value, "spot id");
        value = value.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Spot id is blank");
    }
}

record VehicleId(String value) {
    VehicleId {
        Objects.requireNonNull(value, "vehicle id");
        value = value.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Vehicle id is blank");
    }
}

record FacilityId(String value) {
    FacilityId {
        Objects.requireNonNull(value, "facility id");
        value = value.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Facility id is blank");
    }
}
