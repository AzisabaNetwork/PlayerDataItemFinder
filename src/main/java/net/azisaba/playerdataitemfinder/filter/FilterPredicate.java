package net.azisaba.playerdataitemfinder.filter;

import net.azisaba.playerdataitemfinder.ItemData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class FilterPredicate implements Predicate<ItemData> {
    public static final FilterPredicate TRUE = new FilterPredicate(Op.AND, Collections.emptyList());
    public static final FilterPredicate FALSE = new FilterPredicate(Op.AND, Collections.singletonList(i -> false));
    private final Op op;
    private final List<Predicate<ItemData>> predicates;

    public FilterPredicate(@NotNull Op op, @NotNull List<Predicate<ItemData>> predicates) {
        this.op = op;
        this.predicates = predicates;
    }

    @Override
    public boolean test(ItemData itemData) {
        if (predicates.isEmpty()) {
            return true;
        }
        if (op == Op.AND) {
            return predicates.stream().allMatch(p -> p.test(itemData));
        } else {
            return predicates.stream().anyMatch(p -> p.test(itemData));
        }
    }

    enum Op {
        OR,
        AND,
    }
}
