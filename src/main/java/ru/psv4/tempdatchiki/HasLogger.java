package ru.psv4.tempdatchiki;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HasLogger is a feature interface that provides Logging capability for anyone
 * implementing it where logger needs to operate in serializable environment
 * without being static.
 */
public interface HasLogger {

	default Logger getLogger() {
		return LogManager.getLogger(getClass());
	}
}
