package pl.omnilogy.demoslowdown.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("broken")
@Slf4j
public class BrokenLoggingConfig {

    @Value("${logging.file.path:.}")
    private String logFilePath;

    @Value("${logging.file.name:demo-slowdown.log}")
    private String logFileName;

    @PostConstruct
    public void configureLogging() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("fileAppender");
        fileAppender.setFile(logFilePath + "/" + logFileName);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

        fileAppender.setEncoder(encoder);
        fileAppender.start();

        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("root");
        rootLogger.addAppender(fileAppender);

        log.info("File logging configured to: {}/{}", logFilePath, logFileName);
    }
}