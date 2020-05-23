package io.github.goetschalckx.spring.boot.logging.ndjson;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.CompositeJsonFormatter;
import net.logstash.logback.composite.ContextJsonProvider;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventCompositeJsonFormatter;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackHashJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.composite.loggingevent.TagsJsonProvider;
import net.logstash.logback.composite.loggingevent.ThreadNameJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogEncoder extends LoggingEventCompositeJsonEncoder {

    private static final String LOGGER_FIELD_NAME = "log";
    private static final int LOGGER_NAME_LENGTH = 36;
    private static final String LOG_LEVEL_FIELD_NAME = "lvl";
    private static final String STACK_TRACE_FIELD_NAME = "stack";
    private static final String STACK_HASH_FIELD_NAME = "stack.hash";
    private static final String THREAD_FIELD_NAME = "thread";
    private static final String MESSAGE_FIELD_NAME = "msg";
    private static final String TIMESTAMP_FIELD_NAME = "ts";
    private static final int MAX_DEPTH_PER_THROWABLE = 50;

    private static final List<String> THROWABLE_EXCLUDES =
            Collections.unmodifiableList(
                    Arrays.asList(
                            "sun\\.reflect\\..*\\.invoke.*",
                            "net\\.sf\\.cglib\\.proxy\\.MethodProxy\\.invoke"));

    private boolean prettyPrint;
    private LoggingEventCompositeJsonFormatter encodedFormatter;

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    @Override
    protected CompositeJsonFormatter<ILoggingEvent> createFormatter() {
        encodedFormatter = new LoggingEventCompositeJsonFormatter(this);
        encodedFormatter.setProviders(createDefaultProviders());

        return encodedFormatter;
    }

    @Override
    public void start() {
        if (prettyPrint) {
            encodedFormatter.setJsonGeneratorDecorator(JsonGenerator::useDefaultPrettyPrinter);
        }

        super.start();
    }

    private static JsonProviders<ILoggingEvent> createDefaultProviders() {
        final LoggingEventJsonProviders jsonProviders = new LoggingEventJsonProviders();

        jsonProviders.addLoggerName(loggerNameJsonProvider());
        jsonProviders.addLogLevel(logLevelJsonProvider());
        jsonProviders.addThreadName(threadNameJsonProvider());
        jsonProviders.addStackTrace(stackTraceJsonProvider());
        jsonProviders.addMessage(messageJsonProvider());
        jsonProviders.addTimestamp(timestampProvider());

        // these fields allow applications to customize their log output to meet their needs
        jsonProviders.addContext(new ContextJsonProvider<>());
        jsonProviders.addMdc(new MdcJsonProvider());
        jsonProviders.addLogstashMarkers(new LogstashMarkersJsonProvider());
        jsonProviders.addArguments(new ArgumentsJsonProvider());
        jsonProviders.addTags(new TagsJsonProvider());
        jsonProviders.addStackHash(stackHashJsonProvider());

        return jsonProviders;
    }

    private static LoggingEventFormattedTimestampJsonProvider timestampProvider() {
        final LoggingEventFormattedTimestampJsonProvider provider = new LoggingEventFormattedTimestampJsonProvider();
        provider.setFieldName(TIMESTAMP_FIELD_NAME);
        provider.setTimeZone("UTC");

        return provider;
    }

    private static LoggerNameJsonProvider loggerNameJsonProvider() {
        final LoggerNameJsonProvider loggerNameJsonProvider = new LoggerNameJsonProvider();
        loggerNameJsonProvider.setFieldName(LOGGER_FIELD_NAME);
        loggerNameJsonProvider.setShortenedLoggerNameLength(LOGGER_NAME_LENGTH);
        return loggerNameJsonProvider;
    }

    private static LogLevelJsonProvider logLevelJsonProvider() {
        final LogLevelJsonProvider logLevelJsonProvider = new LogLevelJsonProvider();
        logLevelJsonProvider.setFieldName(LOG_LEVEL_FIELD_NAME);
        return logLevelJsonProvider;
    }

    private static ThreadNameJsonProvider threadNameJsonProvider() {
        final ThreadNameJsonProvider threadNameJsonProvider = new ThreadNameJsonProvider();
        threadNameJsonProvider.setFieldName(THREAD_FIELD_NAME);
        return threadNameJsonProvider;
    }

    private static StackTraceJsonProvider stackTraceJsonProvider() {
        final StackTraceJsonProvider stackTraceJsonProvider = new StackTraceJsonProvider();
        stackTraceJsonProvider.setThrowableConverter(createThrowableConverter());
        stackTraceJsonProvider.setFieldName(STACK_TRACE_FIELD_NAME);
        return stackTraceJsonProvider;
    }

    private static MessageJsonProvider messageJsonProvider() {
        final MessageJsonProvider messageProvider = new MessageJsonProvider();
        messageProvider.setFieldName(MESSAGE_FIELD_NAME);
        return messageProvider;
    }

    private static ThrowableHandlingConverter createThrowableConverter() {
        final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();

        throwableConverter.setMaxDepthPerThrowable(MAX_DEPTH_PER_THROWABLE);
        throwableConverter.setExcludes(THROWABLE_EXCLUDES);

        return throwableConverter;
    }

    private static StackHashJsonProvider stackHashJsonProvider() {
        StackHashJsonProvider provider = new StackHashJsonProvider();

        provider.setFieldName(STACK_HASH_FIELD_NAME);

        return provider;
    }

}
