package lat.soulware.poi.planning.domain.model.valueobjects;

import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/** Where a task stands between being planned and being confirmed as done. */
public enum TaskStatus implements ValueObject {
    /** Planned, with no delivery registered. The only status a delivery can be registered from. */
    PENDING,
    /** A delivery is registered and waits for its review. */
    DELIVERED,
    /** The review confirmed the delivery. Final. */
    COMPLETED
}
