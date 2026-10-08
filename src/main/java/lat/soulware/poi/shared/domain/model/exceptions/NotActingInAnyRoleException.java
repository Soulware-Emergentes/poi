package lat.soulware.poi.shared.domain.model.exceptions;

/**
 * Thrown when a caller's token names no poi role to act as. A person eligible for several roles picks
 * one before acting.
 */
public final class NotActingInAnyRoleException extends ForbiddenException {

    public NotActingInAnyRoleException() {
        super();
    }
}
