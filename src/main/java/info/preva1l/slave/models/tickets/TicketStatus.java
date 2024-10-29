package info.preva1l.slave.models.tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketStatus {
    OPEN("Open"),
    CLOSED("Closed"),
    LOCKED("Locked"),
    ESCALATED("Escalated")
    ;

    private final String friendly;

    public boolean isOpen() {
        return this == OPEN || this == ESCALATED;
    }
}
