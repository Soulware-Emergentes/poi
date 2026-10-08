package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lat.soulware.poi.planning.domain.model.exceptions.InvalidTaskCodeException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * The business identity of a task: its activity's code and its number under it, written {@code 2025-2030-01-03-0032}.
 *
 * @param activity the code of the activity it belongs to, never null
 * @param number its number under that activity, from 1 to 9999
 */
public record TaskCode(ActivityCode activity, int number) implements ValueObject {

    private static final Pattern FORMAT = Pattern.compile("^(\\d{4}-\\d{4}-\\d{2}-\\d{2})-(\\d{4})$");

    public TaskCode {
        boolean isInRange = number >= 1 && number <= 9999;
        if (activity == null || !isInRange) {
            String parent = activity == null ? "?" : activity.value();
            throw new InvalidTaskCodeException("%s-%04d".formatted(parent, number));
        }
    }

    /**
     * @param text a code as written, such as {@code 2025-2030-01-03-0032}
     * @return the code it spells
     * @throws InvalidTaskCodeException if {@code text} is not one
     */
    public static TaskCode parse(String text) {
        Matcher matcher = text == null ? null : FORMAT.matcher(text);
        if (matcher == null || !matcher.matches()) {
            throw new InvalidTaskCodeException(text);
        }
        return new TaskCode(ActivityCode.parse(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    /**
     * @return the code numbered right after this one under the same activity
     * @throws InvalidTaskCodeException if this one is already the last number there
     */
    public TaskCode next() {
        return new TaskCode(this.activity, this.number + 1);
    }

    /** @return the code as written, such as {@code 2025-2030-01-03-0032} */
    public String value() {
        return "%s-%04d".formatted(this.activity.value(), this.number);
    }
}
