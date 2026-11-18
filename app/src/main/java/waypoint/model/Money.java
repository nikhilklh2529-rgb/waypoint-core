package waypoint.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * Immutable, currency-aware value object.
 * All calculations use exact minor units (paise/cents), never floating point.
 */
public final class Money implements Comparable<Money> {
    public static final Currency INR = Currency.getInstance("INR");
    public static final Currency USD = Currency.getInstance("USD");

    private final long minor;
    private final Currency currency;

    private Money(long minor, Currency currency) {
        this.minor = minor;
        this.currency = Objects.requireNonNull(currency, "currency");
    }

    public static Money ofMinor(long minor, Currency currency) {
        return new Money(minor, currency);
    }

    public static Money rupees(long amount) {
        return new Money(Math.multiplyExact(amount, 100), INR);
    }

    public long minor() {
        return minor;
    }

    public Currency currency() {
        return currency;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other");
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatch(currency, other.currency);
        }
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(minor, other.minor), currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(minor, other.minor), currency);
    }

    public Money times(int multiplier) {
        return new Money(Math.multiplyExact(minor, (long) multiplier), currency);
    }

    public boolean isNegative() {
        return minor < 0;
    }

    /**
     * Returns immutable shares whose sum is exactly this amount.
     * Any remainder goes to the earliest shares, including negative amounts.
     */
    public List<Money> allocate(int parts) {
        if (parts <= 0) {
            throw new IllegalArgumentException("parts must be positive");
        }
        long base = minor / parts;
        long remainder = minor % parts;
        long extraShares = Math.abs(remainder);
        long extraSign = Long.signum(remainder);

        List<Money> shares = new ArrayList<>(parts);
        for (int i = 0; i < parts; i++) {
            long share = base + (i < extraShares ? extraSign : 0);
            shares.add(new Money(share, currency));
        }
        return List.copyOf(shares);
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(minor, other.minor);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Money money)) return false;
        return minor == money.minor && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minor, currency);
    }

    @Override
    public String toString() {
        int scale = currency.getDefaultFractionDigits();
        return currency.getCurrencyCode() + " "
                + BigDecimal.valueOf(minor, scale).toPlainString();
    }
}

final class CurrencyMismatch extends IllegalArgumentException {
    CurrencyMismatch(Currency left, Currency right) {
        super("Currencies differ: " + left.getCurrencyCode()
                + " vs " + right.getCurrencyCode());
    }
}
