package com.kodgemisi.soaplogger;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

class OnSoapClientExistsCondition extends AnyNestedCondition {

	OnSoapClientExistsCondition() {
		super(ConfigurationPhase.REGISTER_BEAN);
	}

	// This class is a part of Java runtime 8
	@ConditionalOnClass(name = "com.sun.xml.internal.ws.transport.http.HttpAdapter")
	static class OnJava8 {

	}

	/**
	 * This class comes from following dependency in Java 11 environments:
	 *
	 * <pre>
	 *  <groupId>com.sun.xml.ws</groupId>
	 *  <artifactId>rt</artifactId>
	 * </pre>
	 */
	@ConditionalOnClass(name = "com.sun.xml.ws.transport.http.HttpAdapter")
	static class OnJava11 {

	}

}