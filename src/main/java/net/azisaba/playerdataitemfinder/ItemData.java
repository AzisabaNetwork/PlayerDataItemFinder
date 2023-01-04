package net.azisaba.playerdataitemfinder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ItemData(@NotNull String id, int count, int damage, @Nullable String name, @Nullable String lore) {
}
