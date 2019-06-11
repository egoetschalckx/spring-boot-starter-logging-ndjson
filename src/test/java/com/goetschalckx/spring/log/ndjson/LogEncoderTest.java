package com.goetschalckx.spring.log.ndjson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.composite.CompositeJsonFormatter;
import net.logstash.logback.composite.ContextJsonProvider;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.composite.loggingevent.TagsJsonProvider;
import net.logstash.logback.composite.loggingevent.ThreadNameJsonProvider;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LogEncoderTest {

    private final LogEncoder logEncoder = new LogEncoder();

    @Test
    public void testCreateFormatter() {
        assertHasRequiredProviders(logEncoder.createFormatter());
    }

    @Test
    public void testStart() {
        logEncoder.start();
    }

    @Test
    public void testStartWithPrettyPrint() {
        logEncoder.setPrettyPrint(true);
        logEncoder.start();
    }

    @Test
    public void testPrettyPrint() {
        logEncoder.setPrettyPrint(true);

        assertTrue(logEncoder.isPrettyPrint());
    }

    private static void assertHasRequiredProviders(CompositeJsonFormatter<ILoggingEvent> formatter) {
        assertTrue(
                "LogEncoder is missing one or more required Logstash JSON Providers",
                hasRequiredProviders(formatter.getProviders().getProviders()));
    }

    private static boolean hasRequiredProviders(List<JsonProvider<ILoggingEvent>> providers) {
        final Set<Class> requiredProviders = new HashSet<>();
        requiredProviders.add(LoggerNameJsonProvider.class);
        requiredProviders.add(ThreadNameJsonProvider.class);
        requiredProviders.add(StackTraceJsonProvider.class);
        requiredProviders.add(MessageJsonProvider.class);
        requiredProviders.add(LogLevelJsonProvider.class);
        requiredProviders.add(ContextJsonProvider.class);
        requiredProviders.add(MdcJsonProvider.class);
        requiredProviders.add(LogstashMarkersJsonProvider.class);
        requiredProviders.add(ArgumentsJsonProvider.class);
        requiredProviders.add(LoggingEventFormattedTimestampJsonProvider.class);
        requiredProviders.add(TagsJsonProvider.class);

        providers.forEach(x -> requiredProviders.remove(x.getClass()));

        return requiredProviders.isEmpty();
    }

}
