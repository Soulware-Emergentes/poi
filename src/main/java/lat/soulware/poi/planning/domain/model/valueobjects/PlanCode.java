package lat.soulware.poi.planning.domain.model.valueobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lat.soulware.poi.planning.domain.model.exceptions.InvalidPlanCodeException;
import lat.soulware.poi.shared.domain.model.valueobjects.ValueObject;

/**
 * The business identity of a plan: the years it spans, written {@code 2025-2030}.
 *
 * @param startYear the first year, four digits
 * @param endYear   the last year, four digits, not before {@code startYear}
 */
public record PlanCode(int startYear, int endYear) implements ValueObject {

    private static final Pattern FORMAT = Pattern.compile("^(\\d{4})-(\\d{4})$");

    public PlanCode {
        boolean isFourDigitYears = isFourDigits(startYear) && isFourDigits(endYear);
        if (!isFourDigitYears || endYear < startYear) {
            throw new InvalidPlanCodeException("%d-%d".formatted(startYear, endYear));
        }
    }

    /**
     * @param text a plan code as written, such as {@code 2025-2030}
     * @return the code it spells
     * @throws InvalidPlanCodeException if {@code text} is not one
     */
    public static PlanCode parse(String text) {
        Matcher matcher = text == null ? null : FORMAT.matcher(text);
        if (matcher == null || !matcher.matches()) {
            throw new InvalidPlanCodeException(text);
        }
        return new PlanCode(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    /** @return the code as written, such as {@code 2025-2030} */
    public String value() {
        return "%d-%d".formatted(this.startYear, this.endYear);
    }

    private static boolean isFourDigits(int year) {
        return year >= 1000 && year <= 9999;
    }
}
