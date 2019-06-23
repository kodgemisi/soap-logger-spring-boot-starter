package com.kodgemisi.soaplogger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("soap-logger")
@Getter
@Setter
@ToString
public class SoapLoggerProperties {

	/**
	 * Default 1024000 = 1mib
	 */
	private int dumpThreshold = 1024000; // 1mib

	private boolean useTandemLogger = false;

}