package com.donglu.carpark.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

/**
 * Created by xiaopan on 2016-06-23.
 */
public class PrintLoggerThreadWithCallback extends Thread{
    public interface PrintCall{
        void print(String log);
    }
    private final String layout = "%d{yy-MM-dd HH:mm:ss.SSS} -%msg%n";
    private final String level = "info";

    private final PrintCall callable;
    private final String packageName;

    public PrintLoggerThreadWithCallback(PrintCall callable, String packageName) {
        this.callable = callable;
        this.packageName = packageName;
    }

    @Override
    public void run() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(this.packageName);
        logger.setAdditive(true);
        

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        encoder.setPattern(layout);
        encoder.start();

        ThresholdFilter consoleFilter=new ThresholdFilter();
        consoleFilter.setLevel(level);
        consoleFilter.start();

        PrintLoggerAppender simpleAppender = new PrintLoggerAppender(callable,(PatternLayout)encoder.getLayout());
        simpleAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        simpleAppender.addFilter(consoleFilter);
        simpleAppender.start();
        logger.addAppender(simpleAppender);
    }

    class PrintLoggerAppender extends AppenderBase<ILoggingEvent>{

        private final PrintCall printCall;
        private final PatternLayout layout;

        PrintLoggerAppender(PrintLoggerThreadWithCallback.PrintCall printCall, PatternLayout layout) {
            this.printCall = printCall;
            this.layout = layout;
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            printCall.print(this.layout.doLayout(eventObject));
        }
    }
}
