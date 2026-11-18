package waypoint.model;

import java.util.Objects;

/**
 * Entity identified only by SpotId.
 * Invariant: at most one vehicle can occupy this spot at a time.
 */
public final class Spot {
    private final SpotId id;
    private VehicleId occupant;

    public Spot(SpotId id) {
        this.id = Objects.requireNonNull(id, "spot id");
    }

    public SpotId id() {
        return id;
    }

    public boolean isFree() {
        return occupant == null;
    }

    /**
     * Claims the spot if free. Returns false without changing it if occupied.
     */
    public boolean tryAssign(VehicleId vehicle) {
        Objects.requireNonNull(vehicle, "vehicle");
        if (!isFree()) return false;
        occupant = vehicle;
        return true;
    }

    /**
     * Releasing a free spot is harmless; a different vehicle cannot release
     * the current occupant.
     */
    public void release(VehicleId vehicle) {
        Objects.requireNonNull(vehicle, "vehicle");
        if (isFree()) return;
        if (!occupant.equals(vehicle)) {
            throw new IllegalArgumentException("Spot assigned to another vehicle");
        }
        occupant = null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Spot spot)) return false;
        return id.equals(spot.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Spot[" + id.value() + ", free=" + isFree() + "]";
    }
}
