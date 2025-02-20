package net.azisaba.playerdataitemfinder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Colors {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    DARK_RED("\u001B[31m"),
    DARK_GREEN("\u001B[32m"),
    DARK_YELLOW("\u001B[33m"),
    DARK_BLUE("\u001B[34m"),
    DARK_PURPLE("\u001B[35m"),
    DARK_CYAN("\u001B[36m"),
    DARK_WHITE("\u001B[37m"),
    GRAY("\u001B[90m"),
    RED("\u001B[91m"),
    GREEN("\u001B[92m"),
    YELLOW("\u001B[93m"),
    BLUE("\u001B[94m"),
    MAGENTA("\u001B[95m"),
    CYAN("\u001B[96m"),
    WHITE("\u001B[97m"),
    BRIGHT_BLACK("\u001B[90;1m"),
    BRIGHT_RED("\u001B[91;1m"),
    BRIGHT_GREEN("\u001B[92;1m"),
    BRIGHT_YELLOW("\u001B[93;1m"),
    BRIGHT_BLUE("\u001B[94;1m"),
    BRIGHT_MAGENTA("\u001B[95;1m"),
    BRIGHT_CYAN("\u001B[96;1m"),
    BRIGHT_WHITE("\u001B[97;1m"),
    ;

    private final String code;

    Colors(@NotNull String code) {
        this.code = code;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return code;
    }
}
