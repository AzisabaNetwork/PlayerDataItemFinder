package net.azisaba.playerdataitemfinder.filter;

import net.azisaba.playerdataitemfinder.ItemData;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class FilterParser {
    public static @NotNull FilterPredicate parse(@NotNull StringReader reader) throws InvalidArgumentException {
        reader.skipWhitespace();
        if (reader.isEOF()) return FilterPredicate.TRUE;
        FilterPredicate.Op currentOp = null;
        List<Predicate<ItemData>> currentPredicates = new ArrayList<>();
        while (!reader.isEOF()) {
            reader.skipWhitespace();
            currentPredicates.add(parseSingle(reader));
            reader.skipWhitespace();
            if (reader.isEOF()) break;
            switch (reader.read(2)) {
                case "&&" -> {
                    if (currentOp == null) currentOp = FilterPredicate.Op.AND;
                    else if (currentOp == FilterPredicate.Op.OR) {
                        throw new IllegalArgumentException("Cannot change operator to $read (Use parentheses)");
                    }
                }
                case "||" -> {
                    if (currentOp == null) currentOp = FilterPredicate.Op.OR;
                    else if (currentOp == FilterPredicate.Op.AND) {
                        throw new IllegalArgumentException("Cannot change operator to $read (Use parentheses)");
                    }
                }
                default -> {
                    reader.skip(-2);
                    throw new InvalidArgumentException("Expected && or ||, but got " + reader.read(2));
                }
            }
            reader.skipWhitespace();
            if (reader.isEOF()) throw new InvalidArgumentException("Unexpected EOF");
        }
        if (currentOp == null) currentOp = FilterPredicate.Op.AND;
        return new FilterPredicate(currentOp, currentPredicates);
    }

    private static @NotNull Predicate<ItemData> parseSingle(@NotNull StringReader reader) throws InvalidArgumentException {
        if (reader.isEOF()) {
            return i -> true;
        }
        if (reader.peek() == '(') {
            reader.skipWhitespace();
            FilterPredicate group = parse(reader);
            reader.skipWhitespace();
            if (reader.peek() == ')') {
                try {
                    reader.skip();
                } catch (InvalidArgumentException e) {
                    throw new AssertionError();
                }
                return group;
            }
            throw new IllegalArgumentException("Expected ')' but got '" + reader.peek() + "'");
        }
        reader.skipWhitespace();
        String left = reader.readToken();
        if (!Arrays.asList("id", "count", "damage", "name", "lore").contains(left)) {
            throw new IllegalArgumentException("Expected 'id', 'count', 'damage', 'name' or 'lore' but got '" + left + "'");
        }
        reader.skipWhitespace();
        EqOp op;
        if (reader.peek() == '=') {
            reader.skip();
            if (reader.peek() == '=') reader.skip();
            op = EqOp.EQUALS;
        } else if (reader.peek() == '!') {
            reader.skip();
            if (reader.peek() == '=') {
                reader.skip();
            } else {
                throw new IllegalArgumentException("Expected '=' after '!' but got '" + reader.peek() + "'");
            }
            op = EqOp.NOT_EQUALS;
        } else {
            throw new IllegalArgumentException("Expected '=', '==', or '!=' but got '" + reader.peek() + "'");
        }
        reader.skipWhitespace();
        String token = reader.readQuotedString('"', '\n');
        Predicate<ItemData> predicate;
        if (token.startsWith("pattern:")) {
            Pattern pattern = Pattern.compile(token.substring(8));
            predicate = switch (left) {
                case "id" -> i -> pattern.matcher(i.id()).matches();
                case "name" -> i -> i.name() != null && pattern.matcher(i.name()).matches();
                case "lore" -> i -> i.lore() != null && pattern.matcher(i.lore()).matches();
                default -> throw new IllegalArgumentException("Cannot use pattern with " + left);
            };
        } else {
            predicate = switch (left) {
                case "id" -> i -> Objects.equals(i.id(), token);
                case "count" -> i -> Objects.equals(i.count(), Integer.parseInt(token));
                case "damage" -> i -> Objects.equals(i.damage(), Integer.parseInt(token));
                case "name" -> i -> Objects.equals(i.name(), token);
                case "lore" -> i -> Objects.equals(i.lore(), token);
                default -> throw new AssertionError();
            };
        }
        reader.skipWhitespace();
        if (op == EqOp.EQUALS) {
            return predicate;
        } else {
            return predicate.negate();
        }
    }
}
