package au.com.agic.apptesting.utils.impl;


import static com.google.common.base.Preconditions.checkArgument;



import au.com.agic.apptesting.utils.LoggingConfiguration;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

/**
 * An implementation of the LogginConfiguration service that configures LogBack
 */
public class LogbackConfiguration implements LoggingConfiguration {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogbackConfiguration.class);

	@Override
	public void configureLogging(@NotNull final String logfile) {
		checkArgument(StringUtils.isNotBlank(logfile));

		try {
			final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

			final FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
			fileAppender.setContext(loggerContext);
			fileAppender.setName("timestamp");
			// set the file name
			fileAppender.setFile(logfile);

			final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
			encoder.setContext(loggerContext);
			encoder.setPattern("%r %thread %level - %msg%n");
			encoder.start();

			fileAppender.setEncoder(encoder);
			fileAppender.start();

			// attach the rolling file appender to the logger of your choice
			final Logger logbackLogger =
				(Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
			logbackLogger.addAppender(fileAppender);

			SLF4JBridgeHandler.install();
		} catch (final Exception ex) {
			LOGGER.error("WEBAPPTESTER-BUG-0006: Could not configure Logback", ex);
		}
	}

	@Override
	public void logVersion() {
		try {
			final Optional<InputStream> inputStream =
				Optional.ofNullable(
					getClass().getClassLoader().getResourceAsStream("build.properties"));

			if (inputStream.isPresent()) {
				final Properties prop = new Properties();
				prop.load(inputStream.get());
				LOGGER.info("Version {}", prop.get("build"));
			}
		} catch (final IOException ex) {
			LOGGER.error("Exception thrown while loading build.properties", ex);
		}
	}
}
