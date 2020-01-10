package com.bracso.test.routing;

import java.util.Locale;
import java.util.Optional;

/**
 *
 * @author oburgosm
 */
public class LocaleContextHolder {

    private static final ThreadLocal<Locale> CONTEXT = new ThreadLocal<>();

    public static final Locale LOCALE_ES = new Locale("es", "ES");

    public static Optional<Locale> getLocale() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static void setLocale(Locale currentLocale) {
        CONTEXT.set(currentLocale);
    }

}
