package com.kodgemisi.soaplogger;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

class OnSoapClientExistsCondition extends AnyNestedCondition {

	static final String HTTP_ADAPTER_JAVA_8 = "com.sun.xml.ws.transport.http.HttpAdapter";

	static final String HTTP_ADAPTER_JAVA_11 = "com.sun.xml.internal.ws.transport.http.HttpAdapter";

	OnSoapClientExistsCondition() {
		super(ConfigurationPhase.REGISTER_BEAN);
	}

	// This class is a part of Java runtime 8
	@ConditionalOnClass(name = HTTP_ADAPTER_JAVA_11)
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
	@ConditionalOnClass(name = HTTP_ADAPTER_JAVA_8)
	static class OnJava11 {

	}

}