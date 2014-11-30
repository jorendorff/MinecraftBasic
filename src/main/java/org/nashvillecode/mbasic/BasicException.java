package org.nashvillecode.mbasic;

import java.io.File;

public class BasicException extends RuntimeException {
	public BasicException(String message, File file, int line) {
		super(message + " at " + file + ":" + line);
	}
}
