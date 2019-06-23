package com.kodgemisi.soaplogger;

import com.sun.xml.ws.transport.http.client.HttpTransportPipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

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
	private static final Logger LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());

	private final SoapLoggerProperties properties;

	private final Optional<WsLogHandler> wsLogHandler;

	@Bean
	CommandLineRunner configureSoapLogger() {
		return args -> {
			log.debug("Configuring WS Logger...");

			final WsLogHandler handler = wsLogHandler.orElse(wsLogHandler());
			LOGGER.setLevel(Level.FINER); // The level have to be FINER in order the dump to work!
			LOGGER.addHandler(handler);

			// https://github.com/javaee/metro-jax-ws/issues/1237#issuecomment-439302776
			System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");

			com.sun.xml.ws.transport.http.HttpAdapter.dump_threshold = properties.getDumpThreshold();

			log.trace("WS Logger is configured with following configuration: handler -> {} properties: {}", handler.getClass().getCanonicalName(), properties);
		};
	}

	private WsLogHandler wsLogHandler() {
		return properties.isUseTandemLogger() ? new WsRequestResponseTandemLogger() : new WsLogHandler();
	}

}
