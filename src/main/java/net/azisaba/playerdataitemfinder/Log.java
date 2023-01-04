package net.azisaba.playerdataitemfinder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public final class Log {
    private static final PrintStream REAL_STDOUT = System.out;

    static {
        System.setOut(new LoggedPrintStream(System.out, Log::info));
        System.setErr(new LoggedPrintStream(System.out, Log::error));
    }

    @Contract("null, _ -> !null")
    private static String format(@Nullable String msg, Object @NotNull ... args) {
        if (msg == null) {
            return "";
        }
        return String.format(msg.replace("{}", "%s"), args);
    }

    public static void debug(@Nullable String message) {
        REAL_STDOUT.println(Colors.GRAY + "[DEBUG] " + message + Colors.RESET);
    }

    public static void info(@Nullable String message) {
        REAL_STDOUT.println(Colors.WHITE + "[INFO] " + message + Colors.RESET);
    }

    public static void info(@Nullable String message, Object @NotNull ... args) {
        info(format(message, args));
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            ((Throwable) args[args.length - 1]).printStackTrace();
        }
    }

    public static void warn(@Nullable String message) {
        REAL_STDOUT.println(Colors.YELLOW + "[WARN] " + message + Colors.RESET);
    }

    public static void warn(@Nullable String message, Object @NotNull ... args) {
        warn(format(message, args));
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            ((Throwable) args[args.length - 1]).printStackTrace();
        }
    }

    public static void error(@Nullable String message) {
        REAL_STDOUT.println(Colors.RED + "[ERROR] " + message + Colors.RESET);
    }

    public static void error(@Nullable String message, Object @NotNull ... args) {
        error(format(message, args));
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            ((Throwable) args[args.length - 1]).printStackTrace();
        }
    }

    private static class LoggedPrintStream extends PrintStream {
        private final Consumer<String> log;

        private LoggedPrintStream(OutputStream out, Consumer<String> log) {
            super(out);
            this.log = log;
        }

        @Override
        public void println(@Nullable String x) {
            log.accept(x);
        }

        @Override
        public void println(@Nullable Object x) {
            log.accept(String.valueOf(x));
        }
    }
}
