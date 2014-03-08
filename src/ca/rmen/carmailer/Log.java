/*
 * ----------------------------------------------------------------------------
 * "THE WINE-WARE LICENSE" Version 1.0:
 * Authors: Carmen Alvarez. 
 * As long as you retain this notice you can do whatever you want with this stuff. 
 * If we meet some day, and you think this stuff is worth it, you can buy me a 
 * glass of wine in return. 
 * 
 * THE AUTHORS OF THIS FILE ARE NOT RESPONSIBLE FOR LOSS OF LIFE, LIMBS, SELF-ESTEEM,
 * MONEY, RELATIONSHIPS, OR GENERAL MENTAL OR PHYSICAL HEALTH CAUSED BY THE
 * CONTENTS OF THIS FILE OR ANYTHING ELSE.
 * ----------------------------------------------------------------------------
 */
package ca.rmen.carmailer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Use a custom log formatter and add wrapping methods to make it easier for calling classes to log.
 * 
 * The wrapping methods look a lot like Android...
 */
public class Log {

    static final Logger LOGGER = Logger.getLogger(CarMailer.class.getPackage().getName());
    private static final Level LEVEL = Level.FINE;

    static void init() {
        String logConfigFile = System.getProperty("java.util.logging.config.file");
        // Use our custom formatter unless another one was specified on the command line.
        if (logConfigFile == null) {
            LogManager.getLogManager().reset();
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new LogFormatter());
            handler.setLevel(LEVEL);
            LOGGER.setLevel(LEVEL);
            LOGGER.addHandler(handler);
        }
    }

    private static class LogFormatter extends Formatter {

        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        @Override
        public String format(LogRecord log) {
            Date date = new Date(log.getMillis());
            return String.format("%s %s %s: %s %n", DATE_FORMAT.format(date), log.getLevel(), log.getLoggerName(), log.getMessage());
        }
    }

    static void v(String tag, String message) {
        log(Level.FINER, tag, message, null);
    }

    static void d(String tag, String message) {
        log(Level.FINE, tag, message, null);
    }

    static void i(String tag, String message) {
        log(Level.INFO, tag, message, null);
    }

    static void w(String tag, String message, Throwable t) {
        log(Level.WARNING, tag, message, t);
    }

    static void e(String tag, String message, Throwable t) {
        log(Level.SEVERE, tag, message, t);
    }

    private static void log(Level level, String tag, String message, Throwable t) {
        LOGGER.log(level, tag + ": " + message, t);
    }
}