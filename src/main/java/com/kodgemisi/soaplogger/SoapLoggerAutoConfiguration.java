package com.kodgemisi.soaplogger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on June, 2019
 *
 * @author destan
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SoapLoggerProperties.class)
@Conditional(OnSoapClientExistsCondition.class)
public class SoapLoggerAutoConfiguration {

	/**
	 * <p>Need to hold a strong reference. See <em>Note</em> part of {@link java.util.logging.Logger#getLogger(String)}.</p>
	 * <p>Otherwise, until {@link com.sun.xml.ws.transport.http.client.HttpTransportPipe} class gets a reference to this logger, this instance may
	 * get garbage collected resulting in the loss of any programmatic configuration.</p>
	 *
	 * @see <a href="https://stackoverflow.com/a/43544608/878361">https://stackoverflow.com/a/43544608/878361</a>
	 */
	private static Logger LOGGER;

	private final SoapLoggerProperties properties;

	private final Optional<WsLogHandler> wsLogHandler;

	@Bean
	CommandLineRunner configureSoapLogger() {
		return args -> {
			log.debug("Configuring WS Logger...");

			final Class<?> aClass = getHttpAdapterClass();
			final Field field = aClass.getField("dump_threshold");
			field.set(null, properties.getDumpThreshold());

			final WsLogHandler handler = wsLogHandler.orElse(wsLogHandler());
			LOGGER.setLevel(Level.FINER); // The level have to be FINER in order the dump to work!
			LOGGER.addHandler(handler);

			log.trace("WS Logger is configured with following configuration: handler -> {} properties: {}", handler.getClass().getCanonicalName(), properties);
		};
	}

	private WsLogHandler wsLogHandler() {
		return properties.isUseTandemLogger() ? new WsRequestResponseTandemLogger() : new WsLogHandler();
	}

	private Class<?> getHttpAdapterClass() {
		try {
			// Comes from Java 8 rt.jar
			//System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
			LOGGER = Logger.getLogger("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe");
			return Class.forName("com.sun.xml.internal.ws.transport.http.HttpAdapter");
		}
		catch (ClassNotFoundException e) {
			try {
				// https://github.com/javaee/metro-jax-ws/issues/1237#issuecomment-439302776
				System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
				//System.setProperty("com.sun.xml.ws.transport.http.HttpTransportPipe.dump", "true");
				LOGGER = Logger.getLogger("com.sun.xml.ws.transport.http.HttpTransportPipe");
				return Class.forName("com.sun.xml.ws.transport.http.HttpAdapter");
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException("Neither com.sun.xml.ws.transport.http.HttpAdapter nor com.sun.xml.internal.ws.transport.http.HttpAdapter class is on the classpath!");
			}
		}
	}

}
