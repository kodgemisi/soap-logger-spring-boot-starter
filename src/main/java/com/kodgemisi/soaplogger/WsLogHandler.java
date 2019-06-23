package com.kodgemisi.soaplogger;

import lombok.extern.slf4j.Slf4j;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * <p>This log handler is specially designed to handle logs produced by
 * {@link com.sun.xml.ws.transport.http.client.HttpTransportPipe#dump(com.sun.xml.ws.util.ByteArrayBuffer, String, java.util.Map)} method.</p>
 *
 * <p>The log produced by {@code HttpTransportPipe#dump} method is the most suitable log for SOAP web service request & response logging due to
 * following facts:
 * <ul>
 * <li>Real XML which is transferred to the server can be retrieved. When using a {@link javax.xml.ws.handler.soap.SOAPHandler}</li> logged XML is
 * slightly different than the one sent to the server.
 * <li>HTTP request header values are available. When using a {@link javax.xml.ws.handler.soap.SOAPHandler}</li> this is not possible.</li>
 * </ul>
 * </p>
 *
 * <p>By overriding {@link #publish(java.util.logging.LogRecord)}</p> method you can store the log in database, file or even send it to another
 * server.</p>
 *
 * Created on June, 2019
 *
 * @author destan
 */
@Slf4j
public class WsLogHandler extends Handler {

	@Override
	public void publish(LogRecord logRecord) {
		log(logRecord);
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}

	protected void log(LogRecord logRecord) {
		log.info(logRecord.getMessage());
	}

	protected void debug(LogRecord logRecord) {
		log.debug("====================");
		log.debug("{}", logRecord.getMessage());
		log.debug("{}", logRecord.getLevel());
		log.debug("{}", logRecord.getLoggerName());
		log.debug("{}", logRecord.getMillis());
		// logRecord.getParameters() always null
		log.debug("{}", logRecord.getSequenceNumber());
		log.debug("{}", logRecord.getSourceClassName());
		log.debug("{}", logRecord.getSourceMethodName());
		log.debug("{}", logRecord.getThreadID());
		log.debug("====================");
	}

}
