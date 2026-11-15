package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lat.soulware.poi.planning.domain.model.exceptions.InvalidObjectiveCodeException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * The business identity of an objective: its plan's code and its number under it, written {@code 2025-2030-01}.
 *
 * @param plan the code of the plan it belongs to, never null
 * @param number its number under that plan, from 1 to 99
 */
public record ObjectiveCode(PlanCode plan, int number) implements ValueObject {

    private static final Pattern FORMAT = Pattern.compile("^(\\d{4}-\\d{4})-(\\d{2})$");

    public ObjectiveCode {
        boolean isInRange = number >= 1 && number <= 99;
        if (plan == null || !isInRange) {
            String parent = plan == null ? "?" : plan.value();
            throw new InvalidObjectiveCodeException("%s-%02d".formatted(parent, number));
        }
    }

    /**
     * @param text a code as written, such as {@code 2025-2030-01}
     * @return the code it spells
     * @throws InvalidObjectiveCodeException if {@code text} is not one
     */
    public static ObjectiveCode parse(String text) {
        Matcher matcher = text == null ? null : FORMAT.matcher(text);
        if (matcher == null || !matcher.matches()) {
            throw new InvalidObjectiveCodeException(text);
        }
        return new ObjectiveCode(PlanCode.parse(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    /**
     * @return the code numbered right after this one under the same plan
     * @throws InvalidObjectiveCodeException if this one is already the last number there
     */
    public ObjectiveCode next() {
        return new ObjectiveCode(this.plan, this.number + 1);
    }

    /** @return the code as written, such as {@code 2025-2030-01} */
    public String value() {
        return "%s-%02d".formatted(this.plan.value(), this.number);
    }
}
