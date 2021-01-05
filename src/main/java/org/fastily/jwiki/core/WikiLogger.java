package org.fastily.jwiki.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class WikiLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(Wiki.class);

    public static String getName() {
        return LOGGER.getName();
    }

    public static boolean isTraceEnabled() {
        return LOGGER.isTraceEnabled();
    }

    public static void trace(Wiki wiki, String msg) {
        if (isTraceEnabled())
            LOGGER.trace(format(wiki, msg));
    }

    public static void trace(Wiki wiki, String format, Object... arguments) {
        if (isTraceEnabled())
            LOGGER.trace(format(wiki, format), arguments);
    }

    public static void trace(Wiki wiki, String msg, Throwable t) {
        if (isTraceEnabled())
            LOGGER.trace(format(wiki, msg), t);
    }

    public static boolean isTraceEnabled(Marker marker) {
        return LOGGER.isTraceEnabled(marker);
    }

    public static void trace(Wiki wiki, Marker marker, String msg) {
        if (isTraceEnabled(marker))
            LOGGER.trace(marker, format(wiki, msg));
    }

    public static void trace(Wiki wiki, Marker marker, String format, Object... arguments) {
        if (isTraceEnabled(marker))
            LOGGER.trace(marker, format(wiki, format), arguments);
    }

    public static void trace(Wiki wiki, Marker marker, String msg, Throwable t) {
        if (isTraceEnabled(marker))
            LOGGER.trace(marker, format(wiki, msg), t);
    }

    public static boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    public static void debug(Wiki wiki, String msg) {
        if (isDebugEnabled())
            LOGGER.debug(format(wiki, msg));
    }

    public static void debug(Wiki wiki, String format, Object... arguments) {
        if (isDebugEnabled())
            LOGGER.debug(format(wiki, format), arguments);
    }

    public static void debug(Wiki wiki, String msg, Throwable t) {
        if (isDebugEnabled())
            LOGGER.debug(format(wiki, msg), t);
    }

    public static boolean isDebugEnabled(Marker marker) {
        return LOGGER.isDebugEnabled(marker);
    }

    public static void debug(Wiki wiki, Marker marker, String msg) {
        if (isDebugEnabled(marker))
            LOGGER.debug(marker, format(wiki, msg));
    }

    public static void debug(Wiki wiki, Marker marker, String format, Object... arguments) {
        if (isDebugEnabled(marker))
            LOGGER.debug(marker, format(wiki, format), arguments);
    }

    public static void debug(Wiki wiki, Marker marker, String msg, Throwable t) {
        if (isDebugEnabled(marker))
            LOGGER.debug(marker, format(wiki, msg), t);
    }

    public static boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }

    public static void info(Wiki wiki, String msg) {
        if (isInfoEnabled())
            LOGGER.info(format(wiki, msg));
    }

    public static void info(Wiki wiki, String format, Object... arguments) {
        if (isInfoEnabled())
            LOGGER.info(format(wiki, format), arguments);
    }

    public static void info(Wiki wiki, String msg, Throwable t) {
        if (isInfoEnabled())
            LOGGER.info(format(wiki, msg), t);
    }

    public static boolean isInfoEnabled(Marker marker) {
        return LOGGER.isInfoEnabled(marker);
    }

    public static void info(Wiki wiki, Marker marker, String msg) {
        if (isInfoEnabled(marker))
            LOGGER.info(marker, format(wiki, msg));
    }

    public static void info(Wiki wiki, Marker marker, String format, Object... arguments) {
        if (isInfoEnabled(marker))
            LOGGER.info(marker, format(wiki, format), arguments);
    }

    public static void info(Wiki wiki, Marker marker, String msg, Throwable t) {
        if (isInfoEnabled(marker))
            LOGGER.info(marker, format(wiki, msg), t);
    }

    public static boolean isWarnEnabled() {
        return LOGGER.isWarnEnabled();
    }

    public static void warn(Wiki wiki, String msg) {
        if (isWarnEnabled())
            LOGGER.warn(format(wiki, msg));
    }

    public static void warn(Wiki wiki, String format, Object... arguments) {
        if (isWarnEnabled())
            LOGGER.warn(format(wiki, format), arguments);
    }

    public static void warn(Wiki wiki, String msg, Throwable t) {
        if (isWarnEnabled())
            LOGGER.warn(format(wiki, msg), t);
    }

    public static boolean isWarnEnabled(Marker marker) {
        return LOGGER.isWarnEnabled(marker);
    }

    public static void warn(Wiki wiki, Marker marker, String msg) {
        if (isWarnEnabled(marker))
            LOGGER.warn(marker, format(wiki, msg));
    }

    public static void warn(Wiki wiki, Marker marker, String format, Object... arguments) {
        if (isWarnEnabled(marker))
            LOGGER.warn(marker, format(wiki, format), arguments);
    }

    public static void warn(Wiki wiki, Marker marker, String msg, Throwable t) {
        if (isWarnEnabled(marker))
            LOGGER.warn(marker, format(wiki, msg), t);
    }

    public static boolean isErrorEnabled() {
        return LOGGER.isErrorEnabled();
    }

    public static void error(Wiki wiki, String msg) {
        if (isErrorEnabled())
            LOGGER.error(format(wiki, msg));
    }

    public static void error(Wiki wiki, String format, Object... arguments) {
        if (isErrorEnabled())
            LOGGER.error(format(wiki, format), arguments);
    }

    public static void error(Wiki wiki, String msg, Throwable t) {
        if (isErrorEnabled())
            LOGGER.error(format(wiki, msg), t);
    }

    public static boolean isErrorEnabled(Marker marker) {
        return LOGGER.isErrorEnabled(marker);
    }

    public static void error(Wiki wiki, Marker marker, String msg) {
        if (isErrorEnabled(marker))
            LOGGER.error(marker, format(wiki, msg));
    }

    public static void error(Wiki wiki, Marker marker, String format, Object... arguments) {
        if (isErrorEnabled(marker))
            LOGGER.error(marker, format(wiki, format), arguments);
    }

    public static void error(Wiki wiki, Marker marker, String msg, Throwable t) {
        if (isErrorEnabled(marker))
            LOGGER.error(marker, format(wiki, msg), t);
    }

    private static String format(Wiki wiki, String s) {
        if (wiki == null || !wiki.conf.prefixLogs)
            return s;

        return wiki + ": " + s;
    }
}
