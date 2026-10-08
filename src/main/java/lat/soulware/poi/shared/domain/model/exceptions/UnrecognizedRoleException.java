package lat.soulware.poi.shared.domain.model.exceptions;

/**
 * Thrown when a caller's token names a role poi does not define.
 */
public final class UnrecognizedRoleException extends ForbiddenException {

    /**
     * @param role the value the token names
     */
    public UnrecognizedRoleException(String role) {
        super(role);
    }
}
