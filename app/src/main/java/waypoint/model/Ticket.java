package waypoint.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity identified only by TicketId.
 *
 * I1 entry is never null.
 * I2 exit is null or not before entry.
 * I3 fare is null or nonnegative.
 * I4 fare exists if and only if exit exists.
 * I5 a closed ticket never reopens.
 */
public final class Ticket {
    private final TicketId id;
    private final SpotId spot;
    private final Instant entry;

    // Exactly these two domain fields change once, together, at close().
    private Instant exit;
    private Money fare;

    private Ticket(TicketId id, SpotId spot, Instant entry) {
        this.id = Objects.requireNonNull(id, "ticket id");
        this.spot = Objects.requireNonNull(spot, "spot id");
        this.entry = Objects.requireNonNull(entry, "entry");
    }

    public static Ticket open(TicketId id, SpotId spot, Instant entry) {
        return new Ticket(id, spot, entry);
    }

    public TicketId id() {
        return id;
    }

    public SpotId spot() {
        return spot;
    }

    public Instant entry() {
        return entry;
    }

    public Instant exit() {
        return exit;
    }

    public Money fare() {
        return fare;
    }

    public boolean isOpen() {
        return exit == null;
    }

    /**
     * Validates and calculates first; commits both mutable fields only after
     * every check succeeds. A rejected close changes nothing.
     */
    public Money close(Instant at, FarePolicy policy) {
        if (!isOpen()) {
            throw new TicketAlreadyClosed();
        }
        Objects.requireNonNull(at, "exit time");
        Objects.requireNonNull(policy, "fare policy");
        if (at.isBefore(entry)) {
            throw new IllegalArgumentException("Exit cannot be before entry");
        }

        Money calculated = Objects.requireNonNull(
                policy.fareFor(entry, at, spot), "calculated fare");
        if (calculated.isNegative()) {
            throw new IllegalArgumentException("Fare cannot be negative");
        }

        exit = at;
        fare = calculated;
        return calculated;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Ticket ticket)) return false;
        return id.equals(ticket.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Ticket[" + id.value() + ", open=" + isOpen() + "]";
    }
}

final class TicketAlreadyClosed extends IllegalStateException {
    TicketAlreadyClosed() {
        super("Ticket is already closed");
    }
}
