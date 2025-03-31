package pl.omnilogy.demoslowdown.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("normal")
@Slf4j
public class AsyncLoggingConfig {

    @PostConstruct
    public void configureAsyncLogging() {
        log.info("Configuring async logging");
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        Appender<ILoggingEvent> consoleAppender = rootLogger.getAppender("CONSOLE");

        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setName("ASYNC");
        asyncAppender.setContext(loggerContext);
        asyncAppender.addAppender(consoleAppender);
        asyncAppender.start();

        rootLogger.detachAppender("CONSOLE");
        rootLogger.addAppender(asyncAppender);
    }
}
