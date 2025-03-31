package pl.omnilogy.demoslowdown.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggingLevelChanger {

    public void changeRootLogLevel(LogLevel level) {
        LoggingSystem loggingSystem = LoggingSystem.get(ClassLoader.getSystemClassLoader());
        loggingSystem.setLogLevel("root", level);
    }

    public LogLevel getRootLogLevel() {
        LoggingSystem loggingSystem = LoggingSystem.get(ClassLoader.getSystemClassLoader());
        return loggingSystem.getLoggerConfigurations()
                .stream()
                .filter(config -> "ROOT".equals(config.getName()) || "root".equals(config.getName()))
                .findFirst()
                .map(LoggerConfiguration::getEffectiveLevel)
                .orElse(LogLevel.INFO);
    }
}
