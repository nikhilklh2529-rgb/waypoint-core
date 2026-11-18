package waypoint.model;

import java.time.Instant;

/** Pricing policy is injected into Ticket.close(), not hard-coded in Ticket. */
@FunctionalInterface
public interface FarePolicy {
    Money fareFor(Instant entry, Instant exit, SpotId spot);
}
