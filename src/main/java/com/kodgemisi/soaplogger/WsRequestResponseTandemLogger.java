package com.kodgemisi.soaplogger;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;

/**
 * <p>This implementation is heavily dependent on internal implementation of {@link com.sun.xml.ws.transport.http.client.HttpTransportPipe} class,
 * especially its {@code dump} method.</p>
 *
 * <p>Currently compatible with following version of the dependency</p>
 *
 * <p><pre>{@code
 * <dependency>
 *    <groupId>com.sun.xml.ws</groupId>
 *    <artifactId>rt</artifactId>
 *    <version>2.3.1</version>
 * </dependency>
 * }
 * </pre></p>
 *
 * Created on June, 2019
 *
 * @author destan
 * @see com.sun.xml.ws.transport.http.client.HttpTransportPipe#dump(com.sun.xml.ws.util.ByteArrayBuffer, String, java.util.Map)
 */
@Slf4j
public class WsRequestResponseTandemLogger extends WsLogHandler {

	private static final Map<Integer, LogRecord> requestLogRecordsMap = Collections.synchronizedMap(new HashMap<>());

	@Override
	public void publish(LogRecord logRecord) {

		final int threadId = logRecord.getThreadID();
		assert threadId == Thread.currentThread().getId();

		if (requestLogRecordsMap.containsKey(threadId)) {

			assert isResponse(logRecord);

			final LogRecord request = requestLogRecordsMap.remove(threadId);
			this.publishRequestResponse(request, logRecord);
		}
		else {

			assert isRequest(logRecord);

			requestLogRecordsMap.put(threadId, logRecord);
		}
	}

	private boolean isRequest(LogRecord logRecord) {
		return logRecord.getMessage().contains("---[HTTP request");
	}

	private boolean isResponse(LogRecord logRecord) {
		return logRecord.getMessage().contains("---[HTTP response");
	}

	protected void publishRequestResponse(LogRecord request, LogRecord response) {
		log.info("Request: {}\n\nResponse: {}", request.getMessage(), response.getMessage());
	}

	/**
	 *
	 * @param logRecord
	 * @throws UnsupportedOperationException subclasses may override this method
	 */
	@Override
	protected void log(LogRecord logRecord) {
		throw new UnsupportedOperationException("This class logs through 'publishRequestResponse' method.");
	}
}


