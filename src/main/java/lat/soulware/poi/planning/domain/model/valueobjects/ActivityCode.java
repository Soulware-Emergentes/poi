package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lat.soulware.poi.planning.domain.model.exceptions.InvalidActivityCodeException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * The business identity of an activity: its objective's code and its number under it, written {@code 2025-2030-01-03}.
 *
 * @param objective the code of the objective it belongs to, never null
 * @param number its number under that objective, from 1 to 99
 */
public record ActivityCode(ObjectiveCode objective, int number) implements ValueObject {

    private static final Pattern FORMAT = Pattern.compile("^(\\d{4}-\\d{4}-\\d{2})-(\\d{2})$");

    public ActivityCode {
        boolean isInRange = number >= 1 && number <= 99;
        if (objective == null || !isInRange) {
            String parent = objective == null ? "?" : objective.value();
            throw new InvalidActivityCodeException("%s-%02d".formatted(parent, number));
        }
    }

    /**
     * @param text a code as written, such as {@code 2025-2030-01-03}
     * @return the code it spells
     * @throws InvalidActivityCodeException if {@code text} is not one
     */
    public static ActivityCode parse(String text) {
        Matcher matcher = text == null ? null : FORMAT.matcher(text);
        if (matcher == null || !matcher.matches()) {
            throw new InvalidActivityCodeException(text);
        }
        return new ActivityCode(ObjectiveCode.parse(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    /**
     * @return the code numbered right after this one under the same objective
     * @throws InvalidActivityCodeException if this one is already the last number there
     */
    public ActivityCode next() {
        return new ActivityCode(this.objective, this.number + 1);
    }

    /** @return the code as written, such as {@code 2025-2030-01-03} */
    public String value() {
        return "%s-%02d".formatted(this.objective.value(), this.number);
    }
}
